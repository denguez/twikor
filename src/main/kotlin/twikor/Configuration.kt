package twikor

import com.twitter.hbc.httpclient.auth.OAuth1

val DISCORD_BOT_TOKEN = System.getenv("DISCORD_BOT_TOKEN")
val BOT_PREFIX = System.getenv("BOT_PREFIX") ?: "!t"
val DEBUG = System.setProperty("com.jessecorbett.diskord.debug", System.getenv("DEBUG") ?: false.toString())

val DATABASE_URL = System.getenv("DATABASE_URL") ?: "jdbc:mysql://localhost:3306/test?useSSL=false"
val DATABASE_DRIVER = System.getenv("DATABASE_DRIVER") ?: "com.mysql.jdbc.Driver"
val DATABASE_USER = System.getenv("DATABASE_USER") ?: "root"
val DATABASE_PASSWORD = System.getenv("DATABASE_PASSWORD") ?: ""

object TwitterAuth {
    val instance by lazy { OAuth1(
        System.getenv("TWITTER_API_TOKEN")!!,
        System.getenv("TWITTER_API_SECRET")!!, 
        System.getenv("TWITTER_ACCESS_TOKEN")!!,
        System.getenv("TWITTER_ACCESS_SECRET")!!
    )}
}