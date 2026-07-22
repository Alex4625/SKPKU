package com.alzen.skpku

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface SupabaseApiService {

    @GET("rest/v1/skp_records")
    suspend fun getSkpRecords(
        @Query("user_key") userKey: String,
        @Query("select") select: String = "*",
        @Query("order") order: String = "timestamp.desc"
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
