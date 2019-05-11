package com.hamz4k.data.posts.service

import com.google.gson.annotations.SerializedName
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BestPostsService {
    @GET("posts")
    fun posts(): Observable<List<PostData>>

    @GET("comments")
    fun comments(@Query("postId") postId: Int): Observable<List<CommentData>>

    @GET("users/{userId}")
    fun user(@Path("userId") userId: Int): Observable<UserData>
}


data class PostData(
    @SerializedName("userId") val userId: Int,
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String
)

data class UserData(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("address") val address: AddressData,
    @SerializedName("phone") val phone: String,
    @SerializedName("website") val website: String,
    @SerializedName("company") val company: CompanyData
)

data class CommentData(
    @SerializedName("id") val id: Int,
    @SerializedName("postId") val postIdr: Int,
    @SerializedName("name") val title: String,
    @SerializedName("email") val email: String,
    @SerializedName("body") val body: String
)

data class AddressData(
    @SerializedName("street") val street: String,
    @SerializedName("suite") val suite: String,
    @SerializedName("city") val city: String,
    @SerializedName("zipcode") val zipcode: String,
    @SerializedName("geo") val latlon: LatLonData
)

data class LatLonData(
    @SerializedName("lat") val lat: String,
    @SerializedName("lng") val lon: String
)

data class CompanyData(
    @SerializedName("name") val name: String,
    @SerializedName("catchPhrase") val catchPhrase: String,
    @SerializedName("bs") val description: String
)


//
//"id": 1,
//"name": "Leanne Graham",
//"username": "Bret",
//"email": "Sincere@april.biz",
//"address": {
//    "street": "Kulas Light",
//    "suite": "Apt. 556",
//    "city": "Gwenborough",
//    "zipcode": "92998-3874",
//    "geo": {
//        "lat": "-37.3159",
//        "lng": "81.1496"
//    }
//},
//"phone": "1-770-736-8031 x56442",
//"website": "hildegard.org",
//"company": {
//    "name": "Romaguera-Crona",
//    "catchPhrase": "Multi-layered client-server neural-net",
//    "bs": "harness real-time e-markets"
//}
