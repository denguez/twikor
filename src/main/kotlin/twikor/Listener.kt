package twikor

import com.twitter.hbc.twitter4j.handler.StatusStreamHandler
import com.twitter.hbc.twitter4j.message.DisconnectMessage
import com.twitter.hbc.twitter4j.message.StallWarningMessage
import twitter4j.StallWarning
import twitter4j.Status
import twitter4j.StatusDeletionNotice

abstract class TwitterListener: StatusStreamHandler {
    abstract fun onTweet(tweet: Tweet)

    abstract fun onMessage(msg: TwitterMessage)

    override fun onStatus(status: Status) = onTweet(Tweet(status))

    override fun onException(ex: Exception) = onMessage(TwitterMessage(ex.javaClass.simpleName, ex.message ?: ""))

    override fun onDeletionNotice(notice: StatusDeletionNotice) = onMessage(TwitterMessage("Status Deletion Notice", notice.toString()))

    override fun onStallWarning(warning: StallWarning) = onMessage(TwitterMessage("Stall Warning", warning.message))
            
    override fun onDisconnectMessage(message: DisconnectMessage) = onMessage(TwitterMessage("Disconnect Message", message.disconnectReason))

    override fun onStallWarningMessage(warning: StallWarningMessage) = onMessage(TwitterMessage("Stall Warning Message", warning.message))

    override fun onTrackLimitationNotice(limit: Int) = println("onTrackLimitationNotice $limit")

    override fun onScrubGeo(user: Long, upToStatus: Long) = println("onScrubGeo User $user, UpToStatus $upToStatus")

    override fun onUnknownMessageType(s: String) = println("onUnknownMessageType $s")
}