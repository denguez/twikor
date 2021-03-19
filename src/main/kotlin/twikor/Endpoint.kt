package twikor

import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint

class TwitterEndpoint(subs: Subscriptions): StatusesFilterEndpoint() {
    init {
        if (subs.followings.isNotEmpty()) {
            followings(subs.followings.map { it.twitterId }.toList())
        }
        if (subs.terms.isNotEmpty()) {
            trackTerms(subs.terms.map { it.term }.toList())
        }
    }
}