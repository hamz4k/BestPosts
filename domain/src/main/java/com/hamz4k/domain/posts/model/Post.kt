package com.hamz4k.domain.posts.model

data class Post(
    val id: Int,
    val user: User,
    val title: String,
    val body: String,
    val comments: List<Comment>
)

data class User(
    val id: Int,
    val name: String,
    val username: String,
    val avatarUrl: String,
    val email: String,
//    val address: Address,
    val phone: String,
    val website: String
//    ,
//    val company: Company
)

data class Comment(
    val id: Int,
    val postId: Int,
    val title: String,
    val email: String,
    val body: String
)

data class Address(
    val street: String,
    val suite: String,
    val city: String,
    val zipcode: String,
    val latlon: LatLonData
)

data class LatLonData(
    val lat: String,
    val lon: String
)

data class Company(
    val name: String,
    val catchPhrase: String,
    val description: String
)