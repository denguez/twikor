package twikor

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

import twitter4j.User

fun initializeDatabase() {
    Database.connect(DATABASE_URL, 
        driver = DATABASE_DRIVER, 
        user = DATABASE_USER, 
        password = DATABASE_PASSWORD
    )
    transaction {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(Followings, TrackTerms)
    }
}

class Subscriptions(val followings: List<Following>, val terms: List<TrackTerm>) {
    companion object {
        fun load(channelId: String): Subscriptions {
            return Subscriptions(Followings.all(channelId), TrackTerms.all(channelId))
        }
    }
}

class Following(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Following>(Followings)
    var twitterId by Followings.twitterId
    var name by Followings.name
    var screenName by Followings.screenName
    var channelId by Followings.channelId
}
 
object Followings : IntIdTable() {
    val twitterId = long("twitter_id")
    val name = varchar("name", 225)
    val screenName = varchar("screen_name", 225)
    val channelId = varchar("discord_channel_id", 225)

    fun all(channelId: String): List<Following> {
        return transaction {
            Following.find { Followings.channelId eq channelId }.toList()
        }
    }

    fun follow(user: User, _channelId: String): Following {
        return transaction {
            Following.new { 
                twitterId = user.id
                name = user.name
                screenName = user.screenName
                channelId = _channelId
            }
        }
    }

    fun unfollow(handle: String, channelId: String) {
        transaction {
            Following.find { 
                (Followings.screenName eq handle).and(Followings.channelId eq channelId)
            }.forEach { it.delete() }
        }
    }

    fun unfollowAll(channelId: String) {
        transaction {
            Following.find { Followings.channelId eq channelId }.forEach { it.delete() }
        }
    }
}


class TrackTerm(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TrackTerm>(TrackTerms)
    var term by TrackTerms.term
    var channelId by TrackTerms.channelId
}

object TrackTerms : IntIdTable() {
    val term = varchar("term", 225)
    val channelId = varchar("discord_channel_id", 225)

    fun all(channelId: String): List<TrackTerm> {
        return transaction {
            TrackTerm.find { TrackTerms.channelId eq channelId }.toList()
        }
    }

    fun track(_term: String, _channelId: String): TrackTerm {
        return transaction {
            TrackTerm.new { 
                term = _term
                channelId = _channelId
            }
        }
    }

    fun unTrack(term: String, channelId: String) {
        transaction {
            TrackTerm.find { 
                (TrackTerms.term eq term).and(TrackTerms.channelId eq channelId)  
            }.forEach { it.delete() }
        }
    }

    fun unTrackAll(channelId: String) {
        transaction {
            TrackTerm.find { TrackTerms.channelId eq channelId }.forEach { it.delete() }
        }
    }
}


