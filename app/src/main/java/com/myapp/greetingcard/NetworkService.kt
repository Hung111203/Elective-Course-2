package com.myapp.greetingcard

import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Url
/**
 * Represents the JSON response from the token generation API. * e.g., {"code": 200, "message": "Email sent successfully."}
 */
data class ApiResponse(
    val code: Int,
    val message: String
)
data class AudioRequest(
    val word: String,
    val email: String,
    val token: String
)
interface NetworkService {
    @PUT
    suspend fun generateToken(
        @Url url: String = "https://egsbwqh7kildllpkijk6nt4soq0wlgpe.lambda-url.ap-southeast-1.on.aws/",
        @Body email: UserCredential): ApiResponse

    @PUT
    suspend fun getAudio(
        @Url url: String = "https://ityqwv3rx5vifjpyufgnpkv5te0ibrcx.lambda-url.ap-southeast-1.on.aws/",
        @Body request: AudioRequest
    ): ApiResponse
}