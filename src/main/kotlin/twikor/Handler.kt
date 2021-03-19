package twikor

typealias OnTweet = suspend (Tweet) -> Unit

typealias OnMessage = suspend (TwitterMessage) -> Unit

class TwitterHandler(var onTweet: OnTweet = {}, var onMessage: OnMessage = {}) {
    fun onTweet(onTweet: OnTweet) {
        this.onTweet = onTweet
    }
    fun onMessage(onMessage: OnMessage) {
        this.onMessage = onMessage
    }
}

class CommandHandler<T> (var onResult: suspend (T)-> Unit = {}, var onEmpty: suspend ()-> Unit = {}) {
    fun onResult(onResult: suspend (T)-> Unit) {
        this.onResult = onResult
    }
    fun onEmpty(onEmpty: suspend ()-> Unit) {
        this.onEmpty = onEmpty
    }
}