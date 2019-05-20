package com.hamz4k.bestposts.data.model

import com.google.common.truth.Truth.assertThat
import com.hamz4k.bestposts.data.toComment
import com.hamz4k.bestposts.data.toPost
import com.hamz4k.bestposts.data.toUser
import org.junit.Test

class MappingTest {
    private val fakes = Fakes()
    private val fakesData = fakes.data
    private val fakesDomain  = fakes.domain
    private val postData = fakesData.post1
    private val postOverview = fakesDomain.postOverview1
    private val user = fakesDomain.user
    private val userData = fakesData.user
    private val comment = fakesDomain.comment1
    private val commentData = fakesData.comment1

    @Test
    fun should_map_comment_data_to_comment() {
        assertThat(commentData.toComment()).isEqualTo(comment)
    }

    @Test
    fun should_compose_64p_avatar_url_from_user_id_when_mapping_user() {
        //given
        val userId = 15
        val userData = userData.copy(id = userId)
        val expectedAvatarUrl = "https://api.adorable.io/avatars/64/$userId@adorable"
        //when
        val userDomain = userData.toUser()
        //then
        assertThat(userDomain.avatarUrl).isEqualTo(expectedAvatarUrl)
    }
    @Test
    fun should_map_user_data_to_user() {
        assertThat(userData.toUser()).isEqualTo(user)
    }

    @Test
    fun should_compose_112p_avatar_url_from_user_id_when_mapping_post() {
        //given
        val userId = 36
        val postData = postData.copy(userId = userId)
        val expectedAvatarUrl = "https://api.adorable.io/avatars/112/$userId@adorable"
        //when
        val postOverview = postData.toPost()
        //then
        assertThat(postOverview.avatarUrl).isEqualTo(expectedAvatarUrl)
    }

    @Test
    fun should_map_post_data_to_post_overview() {
        assertThat(postData.toPost()).isEqualTo(postOverview)
    }
}