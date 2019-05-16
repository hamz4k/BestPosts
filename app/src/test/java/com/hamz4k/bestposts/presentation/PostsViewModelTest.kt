package com.hamz4k.bestposts.presentation

import com.hamz4k.domain.RxSchedulers
import com.hamz4k.domain.initForTests
import com.hamz4k.domain.posts.PostsRepository
import com.hamz4k.domain.posts.model.PostLight
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class PostsViewModelTest {

    @Mock
    lateinit var repositoryMock: PostsRepository

    private lateinit var postsViewModel: PostsViewModel

    private val initialState = PostsViewState()
    private val loadingState = PostsViewState(isLoading = true)
    private val errorState = PostsViewState(error = "an error occurred")
    private val post = PostLight(userId = 1,
                                 avatarUrl = "avatarUrl",
                                 id = 2,
                                 body = "body",
                                 title = "title")

    @Before
    fun setUp() {
        RxSchedulers.initForTests()
        MockitoAnnotations.initMocks(this)
        postsViewModel = PostsViewModel(repositoryMock)
    }

    @Test
    fun should_view_state_be_in_initial_state_on_instantiation() {
        //when  PostsViewModel instantiated
        val observer = postsViewModel.observeViewState().test()
        //then
        observer.assertValueCount(1)
            .assertValue { event -> event == initialState }
    }

    @Test
    fun should_not_trigger_post_fetching_before_screen_load_event() {
        //when PostsViewModel instantiated
        //then
        verify(repositoryMock, times(0)).fetchPosts()
    }

    @Test
    fun should_trigger_post_fetching_on_screen_load_event() {
        //given
        given(repositoryMock.fetchPosts()).willReturn(Observable.empty())
        val screenLoadSubject: PublishSubject<PostsEvents.ScreenLoad> = PublishSubject.create()
        postsViewModel.registerToInputs(screenLoadSubject)
        //when
        screenLoadSubject.onNext(PostsEvents.ScreenLoad)

        //then
        verify(repositoryMock, times(1)).fetchPosts()
    }

    @Test
    fun should_update_view_with_loading_state_on_screen_load_event() {
        //given
        given(repositoryMock.fetchPosts()).willReturn(Observable.empty())
        val screenLoadSubject: PublishSubject<PostsEvents.ScreenLoad> = PublishSubject.create()
        val observer = postsViewModel.observeViewState().test()
        postsViewModel.registerToInputs(screenLoadSubject)
        //when
        screenLoadSubject.onNext(PostsEvents.ScreenLoad)

        //then
        observer.assertValueCount(2)
            .assertValueAt(0) { event -> event == initialState }
            .assertValueAt(1) { event -> event == loadingState }
    }

    @Test
    fun should_update_view_with_post_list_on_successful_post_fetching() {
        //given
        given(repositoryMock.fetchPosts()).willReturn(Observable.just(listOf(post)))
        val screenLoadSubject: PublishSubject<PostsEvents.ScreenLoad> = PublishSubject.create()
        val observer = postsViewModel.observeViewState().test()
        postsViewModel.registerToInputs(screenLoadSubject)
        //when
        screenLoadSubject.onNext(PostsEvents.ScreenLoad)

        //then
        observer.assertValueCount(3)
            .assertValueAt(0) { event -> event == initialState }
            .assertValueAt(1) { event -> event == loadingState }
            .assertValueAt(2) { event -> event.posts[0] == post }
    }

    @Test
    fun should_trigger_post_fetching_only_once_after_multiple_consecutive_screen_load_events() {
        //given
        given(repositoryMock.fetchPosts()).willReturn(Observable.just(listOf(post)))
        val screenLoadSubject: PublishSubject<PostsEvents.ScreenLoad> = PublishSubject.create()

        postsViewModel.observeViewState().test()
        postsViewModel.registerToInputs(screenLoadSubject)

        //when
        screenLoadSubject.onNext(PostsEvents.ScreenLoad)
        screenLoadSubject.onNext(PostsEvents.ScreenLoad)
        screenLoadSubject.onNext(PostsEvents.ScreenLoad)
        //then
        verify(repositoryMock, times(1)).fetchPosts()
    }

    @Test
    fun should_update_view_state_only_once_after_multiple_consecutive_screen_load_events() {
        //given
        given(repositoryMock.fetchPosts()).willReturn(Observable.just(listOf(post)))
        val screenLoadSubject: PublishSubject<PostsEvents.ScreenLoad> = PublishSubject.create()

        val observer = postsViewModel.observeViewState().test()
        postsViewModel.registerToInputs(screenLoadSubject)
        //when
        screenLoadSubject.onNext(PostsEvents.ScreenLoad)
        screenLoadSubject.onNext(PostsEvents.ScreenLoad)
        screenLoadSubject.onNext(PostsEvents.ScreenLoad)
        //then
        observer.assertValueCount(3)
            .assertValueAt(0) { event -> event == initialState }
            .assertValueAt(1) { event -> event == loadingState }
            .assertValueAt(2) { event -> event.posts[0] == post }
    }

    @Test
    fun should_update_view_with_error_state_on_fetch_error() {
        //given
        val exception = Exception("an error occurred")
        given(repositoryMock.fetchPosts()).willReturn(Observable.error(exception))
        val screenLoadSubject: PublishSubject<PostsEvents.ScreenLoad> = PublishSubject.create()

        postsViewModel.registerToInputs(screenLoadSubject)

        val observer = postsViewModel.observeViewState().test()

        //when
        screenLoadSubject.onNext(PostsEvents.ScreenLoad)
        //then
        observer.assertValueCount(3)
            .assertValueAt(0) { event -> event == initialState }
            .assertValueAt(1) { event -> event == loadingState }
            .assertValueAt(2) { event -> event == errorState }
    }

    @Test
    fun should_trigger_post_fetching_after_retry() {
        //given
        given(repositoryMock.fetchPosts()).willReturn(Observable.error(Exception()))
        val retrySubject: PublishSubject<PostsEvents.Retry> = PublishSubject.create()
        val screenLoadSubject: PublishSubject<PostsEvents.ScreenLoad> = PublishSubject.create()

        postsViewModel.registerToInputs(retrySubject, screenLoadSubject)

        postsViewModel.observeViewState().test()
        screenLoadSubject.onNext(PostsEvents.ScreenLoad)

        //when
        retrySubject.onNext(PostsEvents.Retry)
        //then
        verify(repositoryMock, times(2)).fetchPosts()
    }

    @Test
    fun should_update_view_with_loading_state_after_retry() {
        //given
        val exception = Exception("an error occurred")
        given(repositoryMock.fetchPosts())
            .willReturn(Observable.error(exception))//Fails first
            .willReturn(Observable.empty())        //Empty on second call

        val retrySubject: PublishSubject<PostsEvents.Retry> = PublishSubject.create()
        val screenLoadSubject: PublishSubject<PostsEvents.ScreenLoad> = PublishSubject.create()

        postsViewModel.registerToInputs(retrySubject, screenLoadSubject)

        val observer = postsViewModel.observeViewState().test()

        //when
        screenLoadSubject.onNext(PostsEvents.ScreenLoad)
        retrySubject.onNext(PostsEvents.Retry)
        //then
        observer.assertValueCount(4)
            .assertValueAt(0) { event -> event == initialState }
            .assertValueAt(1) { event -> event == loadingState }
            .assertValueAt(2) { event -> event == errorState }
            .assertValueAt(3) { event -> event == loadingState }
    }

    @Test
    fun should_update_view_with_post_list_on_successful_retry() {
        //given
        val exception = Exception("an error occurred")
        given(repositoryMock.fetchPosts())
            .willReturn(Observable.error(exception))  //Fails first
            .willReturn(Observable.just(listOf(post))) //Succeeds on second call
        val retrySubject: PublishSubject<PostsEvents.Retry> = PublishSubject.create()
        val screenLoadSubject: PublishSubject<PostsEvents.ScreenLoad> = PublishSubject.create()

        postsViewModel.registerToInputs(retrySubject, screenLoadSubject)

        val observer = postsViewModel.observeViewState().test()

        screenLoadSubject.onNext(PostsEvents.ScreenLoad) // First call => Failure

        //when
        retrySubject.onNext(PostsEvents.Retry) // Second call => Succeeds
        //then
        observer.assertValueCount(5)
            .assertValueAt(0) { event -> event == initialState }
            .assertValueAt(1) { event -> event == loadingState }
            .assertValueAt(2) { event -> event == errorState }
            .assertValueAt(3) { event -> event == loadingState }
            .assertValueAt(4) { event -> event.posts[0] == post }
    }

    @Test
    fun should_on_post_click_event_not_affect_view_state() {
        //given
        val screenLoadSubject: PublishSubject<PostsEvents.ScreenLoad> = PublishSubject.create()
        val postClickedSubject: PublishSubject<PostsEvents.PostClicked> = PublishSubject.create()

        given(repositoryMock.fetchPosts()).willReturn(Observable.just(listOf(post)))

        val observer = postsViewModel.observeViewState().test()
        postsViewModel.registerToInputs(screenLoadSubject, postClickedSubject)
        screenLoadSubject.onNext(PostsEvents.ScreenLoad)
        observer.assertValueCount(3)
            .assertValueAt(0) { event -> event == initialState }
            .assertValueAt(1) { event -> event == loadingState }
            .assertValueAt(2) { event -> event.posts[0] == post }
        //when
        postClickedSubject.onNext(PostsEvents.PostClicked(post))
        //then
        observer.assertValueCount(3)
            .assertValueAt(0) { event -> event == initialState }
            .assertValueAt(1) { event -> event == loadingState }
            .assertValueAt(2) { event -> event.posts[0] == post }
    }

    @Test
    fun should_on_post_click_event_result_in_navigation_view_effect() {
        //given
        val postClickedSubject: PublishSubject<PostsEvents.PostClicked> = PublishSubject.create()

        val viewEffectObserver = postsViewModel.observeViewEffects().test()
        postsViewModel.registerToInputs(postClickedSubject)
        //when
        postClickedSubject.onNext(PostsEvents.PostClicked(post))
        //then
        viewEffectObserver.assertValueCount(1)
            .assertValueAt(0) { event -> event == PostsViewEffect.NavigateToPostDetail(post) }
    }

}