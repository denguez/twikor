package twikor

import com.jessecorbett.diskord.api.rest.EmbedImage
import com.jessecorbett.diskord.dsl.*
import com.jessecorbett.diskord.util.*

fun Bot.getChannel(id: String) = clientStore.channels.get(id)

suspend fun main() {
    initializeDatabase()
    bot(DISCORD_BOT_TOKEN!!) {
        twitter {
            onTweet { tweet ->
                getChannel(tweet.channelId).sendMessage("") {
                    author(tweet.authorTitle) {
                        authorUrl = tweet.authorUrl
                        authorImageUrl = tweet.author.imageUrl()
                    }
                    url = tweet.url
                    title = tweet.title
                    description = tweet.text
                    thumbnail = EmbedImage(tweet.thumbnail)
                    tweet.media.forEach {
                        image(it.mediaURLHttps) {
                            imageWidth = it.width()
                            imageHeight = it.height()
                        }
                        video(it.mediaURLHttps) {
                            videoWidth = it.width()
                            videoHeight = it.height()
                        }
                    }
                    footer(tweet.footer)
                }
            }
            onMessage { msg ->
                getChannel(msg.channelId).sendMessage("") {
                    title = msg.title
                    description = msg.message
                }
            }
        }
    }
}

fun Bot.twitter(handler: TwitterHandler.() -> Unit) {
    var client: TwitterClient? = null
    commands(TWITTER_PREFIX) {
        command(" start") {
            client?.stop()
            client = twitterStart(handler)
            reply { title = "Twitter stream started" }
        }
        command(" stop") {
            client?.stop()
            reply { title = "Twitter stream stopped" }
        }
        command(" ls") {
            listFollowings {
                onResult {
                    reply {
                        title = "Streaming tweets from:"
                        if (it.terms.isNotEmpty()) {
                            field("Hastags", it.terms.map { it.term }.joinToString("\n"), true)
                        }
                        it.followings.forEach { field(it.userName(), it.url(), false) }
                    }
                }
                onEmpty { reply { title = "There are no subscriptions" } }
            }
        }
        command(" follow") {
            val handle = words.drop(2).joinToString("")
            follow(handle) {
                onResult {
                    reply {
                        url = it.url()
                        title = "Following ${it.userName()}"
                    }
                }
                onEmpty { reply { title = "User @$handle not found" } }
            }
        }
        command(" unfollow") {
            val handle = words.drop(2).joinToString("")
            if (handle.isNotEmpty()) {
                unFollow(handle) { reply { title = "Unfollowed $handle" } }
            }
        }
        command(" -a unfollow") { unFollowAll { reply { title = "Unfollowed all users" } } }
        command(" track") {
            val term = words.drop(2).joinToString("")
            track(term) { onResult { reply { title = "Following ${it.term}" } } }
        }
        command(" untrack") {
            val term = words.drop(2).joinToString("")
            if (term.isNotEmpty()) {
                unTrack(term) { reply { title = "Untracked $term" } }
            }
        }
        command(" -a untrack") { unTrackAll { reply { title = "Untracked all words" } } }
        command(" clear") { reset { reply { title = "Clear subscriptions" } } }
        command(" help") {
            reply {
                title = "Commands"
                author("Prefix $TWITTER_PREFIX")
                field("start", "(Re) Start streaming client", false)
                field("stop", "Stop streaming client", false)
                field("ls", "List all subscriptions (users and words)", false)
                field("follow [handle]", "Follow user by handle", false)
                field("unfollow [handle]", "Unfollows user by handle", false)
                field("-a unfollow", "Unfollows all users", false)
                field("track [word]", "Tracks given word", false)
                field("untrack [word]", "Stop tracking word", false)
                field("-a untrack", "Stop tracking all words", false)
                field("clear", "Follows all users and untracks all words", false)
            }
        }
    }
}
