package twikor

import twitter4j.Status
import twitter4j.User
import twitter4j.MediaEntity

fun Subscriptions.isEmpty() = followings.isEmpty() && terms.isEmpty()      
fun Subscriptions.getChannelId(tweet: Status): String? {
    return followings.find { 
        it.twitterId == tweet.user.id 
        || (it.twitterId == tweet.retweetedStatus?.user?.id)
    }?.channelId ?: terms.find { 
        val term = it.term.replace("#", "").replace("$", "")
        val retweet = tweet.retweetedStatus

        tweet.text.contains(term, ignoreCase = true)
        || tweet.hashtagEntities.any { it.text.toLowerCase() == term }
        || tweet.symbolEntities.any { it.text.toLowerCase() == term }
        || (retweet?.text?.contains(term, ignoreCase = true)) ?: false
        || retweet?.hashtagEntities?.any { it.text.toLowerCase() == term.toLowerCase() } ?: false
        || retweet?.symbolEntities?.any { it.text.toLowerCase() == term.toLowerCase() } ?: false
    }?.channelId
}

fun userUrl(user: String) = "https://twitter.com/$user"
fun userName(name: String, handle: String) = "$name @$handle"
fun hashtagUrl(hashtag: String) = "https://twitter.com/hashtag/$hashtag?src=hashtag_click"
fun symbolUrl(symbol: String) = "https://twitter/search?q=$symbol"

fun TrackTerm.url() = hashtagUrl(term)

fun Following.userName() = userName(name, screenName)
fun Following.url() = userUrl(screenName)
fun Following.link() = "[${userName()}](${url()})"

fun User.name() = userName(name, screenName)
fun User.url() = userUrl(screenName)
fun User.imageUrl() = miniProfileImageURL

fun Status.parseText(): String {
    var parsed = text
    userMentionEntities.forEach { 
        val handle = "@${it.screenName}"
        parsed = parsed.replace(handle, "[$handle](${userUrl(it.screenName)})") 
    }
    hashtagEntities.forEach { 
        val hashtag = "#${it.text}"
        parsed = parsed.replace(hashtag, "[$hashtag](${hashtagUrl(it.text)})")
    }
    symbolEntities.forEach { 
        val symbol = "$${it.text}"
        parsed = parsed.replace(symbol, "[$symbol](${symbolUrl(symbol)})")
    }
    return parsed
}

fun MediaEntity.height() = this.sizes.get(0)?.height ?: 720
fun MediaEntity.width() = this.sizes.get(0)?.width ?: 480