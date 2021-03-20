# twikor
A Dockerized Discord bot that streams Twitter status in real time.

# How to use
- Create a Discord bot account. 
- Generate aproppiate developer tokens for [Discord](https://discord.com/developers/applications) and [Twitter](https://developer.twitter.com).
- Create `.env` configuration file. See [example](example.env).
- Build the project.
```
./gradlew build
```
- Start bot.
```
docker-compose up
```
- Invite the bot to a server with appropiate text permissions.
- You can now send `!t help` or `[your_prefix] help` to list all commands available!.

### Examples
- Follow user
```
!t follow vsauce
```
-  Track some word or hashtag
```
 !t track #Bitcoin
```
- Start real time stream
```
 !t start
```
- Stop streaming
```
 !t stop
```

# How it works
Subscriptions are added to a database with their corresponding channel ID, so you can configure many channels with different filters.
-  `track`: Saves the given word and channel ID.
-  `follow`: Checks with the Twitter Client if the given username exists and saves the retrieved user ID, screen name and channel ID.
- `start`: Start (or re-start) the streaming client with filter data loaded from database. This is done only once at initialization so you will have to restart the client every time you want to apply new filters from users and words. 
- `stop`: Stop the streaming client


# Libraries used
- The Discord API is managed by [Diskord](https://github.com/JesseCorbett/Diskord), a coroutine based Kotlin client with nice and neat DSL.
- The Twitter Streaming API is handled by Twitter's [Hosebird Client](https://github.com/twitter/hbc), more specifically the hbc-twitter4j module.
