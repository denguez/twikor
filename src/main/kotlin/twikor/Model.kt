package twikor

import twitter4j.Status
import twitter4j.User
import twitter4j.MediaEntity

class TwitterMessage(val title: String, val message: String, var channelId: String = "")

class Tweet(val status: Status, var channelId: String = "") {
    val author = status.user
    val authorUrl = "${author.url()}/status/${status.id}"
    var authorTitle = author.name()

    var url: String? = null
    var title: String? = null
    val text: String
    val date: String 
    val thumbnail: String
    val media = ArrayList<MediaEntity>(status.mediaEntities.toList())

    val likes: Int
    val retweets: Int

    val footer: String

    init {
        var tweet = status
        var user = author
        if (tweet.isRetweet) {
            authorTitle += "\t 🔃 retweeted"

            tweet = tweet.retweetedStatus!!
            user = tweet.user
            
            title = "${user.name()}"
            url = "${user.url()}/status/${status.id}"
            media.addAll(tweet.mediaEntities.toList())

        }

        text = tweet.parseText()
        date = tweet.createdAt.toString()
        thumbnail = user.biggerProfileImageURLHttps

        likes = tweet.favoriteCount
        retweets = tweet.retweetCount
        
        footer = "$date\n 🔃\t$retweets\t 💓\t$likes"
    }
}