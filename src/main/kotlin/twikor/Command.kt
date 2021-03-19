package twikor

import com.jessecorbett.diskord.dsl.*
import com.jessecorbett.diskord.api.model.Message
import com.jessecorbett.diskord.util.*

import kotlinx.coroutines.*

suspend fun Message.twitterStart(handlerBlock: TwitterHandler.() -> Unit): TwitterClient? {
    val handler = TwitterHandler().apply(handlerBlock)
    val subs =  Subscriptions.load(channelId)
    if (subs.isEmpty()) {
        handler.onMessage.invoke(TwitterMessage(
            "Cannot initialize Twitter client", 
            "Stream filter is empty. Try adding followings and terms", channelId
        ))
        return null
    }
    val endpoint = TwitterEndpoint(subs)
    return try {
        TwitterClient(endpoint, object: TwitterListener() {
            override fun onTweet(tweet: Tweet) {
                println(tweet.status.text)
                subs.getChannelId(tweet.status)?.let {
                    tweet.channelId = it
                    GlobalScope.launch {
                        handler.onTweet.invoke(tweet)
                    }
                }
            }
            override fun onMessage(msg: TwitterMessage) {
                GlobalScope.launch {
                    msg.channelId = channelId
                    handler.onMessage.invoke(msg)
                }
            }
        })
    } catch (e: Exception) {
        handler.onMessage.invoke(TwitterMessage("Error initializing twitter client", e.message ?: "", channelId))
        return null
    }
}

suspend fun Message.listFollowings(block: CommandHandler<Subscriptions>.()-> Unit) {
    val handler = CommandHandler<Subscriptions>().apply(block)
    val subs = Subscriptions.load(channelId)
    if (subs.isEmpty()) {
        handler.onEmpty.invoke()
    } else {
        handler.onResult.invoke(subs)
    }
}

suspend fun Message.follow(handle: String, block: CommandHandler<Following>.()-> Unit) {
    val handler = CommandHandler<Following>().apply(block)
    val user = TwitterClient.findUserId(handle)
    if (user == null) {
        handler.onEmpty.invoke()
    } else {
        Followings.follow(user, channelId).let {
            handler.onResult.invoke(it)
        }
    }
}

suspend fun Message.unFollow(handle: String, onResult: suspend ()-> Unit = {}) {
    Followings.unfollow(handle, channelId)
    onResult.invoke()
}

suspend fun Message.unFollowAll(onResult: suspend ()-> Unit = {}) {
    Followings.unfollowAll(channelId)
    onResult.invoke()
}

suspend fun Message.track(term: String, block: CommandHandler<TrackTerm>.()-> Unit) {
    val handler = CommandHandler<TrackTerm>().apply(block)
    TrackTerms.track(term, channelId).let {
        handler.onResult.invoke(it)
    }
}

suspend fun Message.unTrack(term: String, onResult: suspend ()-> Unit = {}) {
    TrackTerms.unTrack(term, channelId)
    onResult.invoke()
}

suspend fun Message.unTrackAll(onResult: suspend ()-> Unit = {}) {
    TrackTerms.unTrackAll(channelId)
    onResult.invoke()
}

suspend fun Message.reset(onResult: suspend ()-> Unit = {}) {
    unFollowAll()
    unTrackAll()
    onResult.invoke()
}
