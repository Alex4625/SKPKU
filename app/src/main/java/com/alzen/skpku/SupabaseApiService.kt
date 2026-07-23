package com.alzen.skpku

import com.google.gson.annotations.SerializedName
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

data class AuthRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class AuthResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("user") val user: UserData
)

data class UserData(
    @SerializedName("id") val id: String
)

interface SupabaseApiService {

    @POST("auth/v1/signup")
    suspend fun signUp(
        @Body request: AuthRequest
    ): Response<AuthResponse>

    @POST("auth/v1/token?grant_type=password")
    suspend fun login(
        @Body request: AuthRequest
    ): Response<AuthResponse>

    @GET("rest/v1/skp_records")
    suspend fun getSkpRecords(
        @Query("user_key") userKey: String,
        @Query("select") select: String = "*",
        @Query("order") order: String = "timestamp.desc",
        @Header("Range") range: String
    ): Response<List<Skp>>

    @POST("rest/v1/skp_records")
    suspend fun insertSkpRecord(
        @Body skp: Skp,
        @Header("Prefer") prefer: String = "return=representation"
    ): Response<List<Skp>>

    @PATCH("rest/v1/skp_records")
    suspend fun updateSkpRecord(
        @Query("id") idQuery: String,
        @Body skp: Map<String, @JvmSuppressWildcards Any?>,
        @Header("Prefer") prefer: String = "return=representation"
    ): Response<List<Skp>>

    @DELETE("rest/v1/skp_records")
    suspend fun deleteSkpRecord(
        @Query("id") idQuery: String
    ): Response<Unit>

    @POST("storage/v1/object/{bucket}/{path}")
    suspend fun uploadFile(
        @Path("bucket") bucket: String,
        @Path("path") path: String,
        @Body file: RequestBody,
        @Header("Content-Type") contentType: String,
        @Header("x-upsert") upsert: Boolean = true
    ): Response<ResponseBody>
}
