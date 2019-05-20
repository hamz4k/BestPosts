package com.hamz4k.bestposts.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MappingTest {
    private val fakes = Fakes()
    private val postUi = fakes.ui.post
    private val postOverview = fakes.domain.postOverview1
    private val post = fakes.domain.post
    private val detailItem = fakes.ui.detailItem
    private val comment = fakes.domain.comment1
    private val commentItem = fakes.ui.comment1

    @Test
    fun should_map_postui_to_post_overview() {
        assertThat(postUi.toPost()).isEqualTo(postOverview)
    }

    @Test
    fun should_map_post_overview_to_ui() {
        assertThat(postOverview.toUi()).isEqualTo(postUi)
    }

    @Test
    fun should_map_post_to_detail() {
        assertThat(post.toDetail()).isEqualTo(detailItem)
    }

    @Test
    fun should_map_comment_to_comment_item() {
        assertThat(comment.toCommentItem()).isEqualTo(commentItem)
    }
}