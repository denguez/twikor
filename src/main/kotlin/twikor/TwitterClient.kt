package twikor

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.Executors

import com.twitter.hbc.core.processor.StringDelimitedProcessor
import com.twitter.hbc.core.Constants
import com.twitter.hbc.twitter4j.Twitter4jStatusClient
import com.twitter.hbc.ClientBuilder
import twitter4j.TwitterFactory
import twitter4j.User

class TwitterClient(endpoint: TwitterEndpoint, listener: TwitterListener) {
    companion object {
        private var STREAMS = 0
        private const val THREADS = 5
        private val client by lazy {
            TwitterFactory().getInstance()
        }

        suspend fun findUserId(handle: String): User? {
            try { 
                return client.showUser(handle)
            } catch(_: Exception) {
                return null
            }
        }
    }

    val stream: Twitter4jStatusClient
    init {
        val queue = LinkedBlockingQueue<String>()
        val baseClient = ClientBuilder()
            .name("Twitter HB Client #$STREAMS")
            .hosts(Constants.STREAM_HOST)
            .authentication(TwitterAuth.instance)
            .processor(StringDelimitedProcessor(queue))
            .endpoint(endpoint)
            .build()

        stream = Twitter4jStatusClient(baseClient, queue, listOf(listener),  Executors.newFixedThreadPool(THREADS))
        stream.connect(); 
        for (i in 0..THREADS)  {
            stream.process() //
        }
        STREAMS++
    }
    
    fun stop() {
        if(isAlive()) stream.stop()
    }

    fun isAlive() = !stream.isDone
}

