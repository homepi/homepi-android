package com.mrjosh.homepi.client

import retrofit2.Call
import retrofit2.http.*
import org.json.JSONObject
import java.math.BigInteger
import com.mrjosh.homepi.client.responses.*

interface Client {

  @get:GET("api/")
  val service: Call<SystemResult?>?

  @FormUrlEncoded
  @POST("auth/create.json")
  fun authenticate(@Field("user") username: String?, @Field("pass") password: String?): Call<AuthenticationResult?>?

  @POST("auth/refresh.json")
  fun refreshToken(@Header("Authorization") token: String?): Call<AuthenticationResult?>?

  @GET("users/me.json")
  fun getUser(@Header("Authorization") token: String?): Call<UserResult?>?

  @GET("accessories/{accessory_id}/run.json")
  fun runAccessory(@Header("Authorization") token: String?, @Path("accessory_id") accessory_id: BigInteger?): Call<JSONObject?>?

  @GET("accessories.json")
  fun getAccessories(@Header("Authorization") token: String?): Call<AccessoriesResult?>?

  @GET("users/logs.json")
  fun getLogs(@Header("Authorization") token: String?): Call<LogsResult?>?

}
