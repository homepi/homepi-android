package net.mrjosh.homepi.client.responses

class AuthenticationResult(val result: Result) {
    inner class Result(val token: String, val refreshed_token: String)
}
