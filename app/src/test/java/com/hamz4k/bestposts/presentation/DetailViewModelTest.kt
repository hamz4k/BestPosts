package com.hamz4k.bestposts.presentation

import com.google.common.truth.Truth
import com.hamz4k.bestposts.R
import com.hamz4k.bestposts.domain.RxSchedulers
import com.hamz4k.bestposts.domain.initForTests
import com.hamz4k.bestposts.domain.posts.detail.PostDetailUseCase
import com.hamz4k.bestposts.model.*
import com.hamz4k.bestposts.presentation.posts.detail.*
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class DetailViewModelTest {

    @Mock
    lateinit var useCaseMock: PostDetailUseCase

    private lateinit var detailViewModel: DetailViewModel

    private val initialState = DetailViewState()
    private val loadingState = DetailViewState(isLoading = true)
    private val errorState = DetailViewState(error = R.string.error_message)

    private val fakes = Fakes()

    private val postUi = fakes.ui.post
    private val postOverview = fakes.domain.postOverview1
    private val post = fakes.domain.post
    private val detailList = fakes.ui.detailList
    private val detailItem = fakes.ui.detailItem
    private val commentItems = fakes.ui.commentItems
    private val commentHeaderItem = fakes.ui.commentHeaderItem
    private val details = fakes.ui.detailList

    @Before
    fun setUp() {
        RxSchedulers.initForTests()
        MockitoAnnotations.initMocks(this)
        detailViewModel = DetailViewModel(useCaseMock)
    }

    @Test
    fun should_view_state_be_in_initial_state_on_instantiation() {
        //when  PostsViewModel instantiated
        val observer = detailViewModel.observeViewState().test()
        //then
        observer.assertValueCount(1)
            .assertValue { viewState -> viewState == initialState }
    }

    @Test
    fun should_not_trigger_post_fetching_before_screen_load_event() {
        //when PostsViewModel instantiated
        //then
        verify(useCaseMock, times(0)).postDetail(postOverview)
    }

    @Test
    fun should_trigger_post_fetching_on_screen_load_event() {
        //given
        given(useCaseMock.postDetail(postOverview)).willReturn(Observable.empty())
        val screenLoadSubject: PublishSubject<DetailEvents.ScreenLoad> = PublishSubject.create()
        detailViewModel.registerToInputs(screenLoadSubject)
        //when
        screenLoadSubject.onNext(DetailEvents.ScreenLoad(postUi))

        //then
        verify(useCaseMock, times(1)).postDetail(postOverview)
    }

    @Test
    fun should_update_view_with_loading_state_on_screen_load_event() {
        //given
        given(useCaseMock.postDetail(postOverview)).willReturn(Observable.empty())
        val screenLoadSubject: PublishSubject<DetailEvents.ScreenLoad> = PublishSubject.create()
        val observer = detailViewModel.observeViewState().test()
        detailViewModel.registerToInputs(screenLoadSubject)
        //when
        screenLoadSubject.onNext(DetailEvents.ScreenLoad(postUi))

        //then
        observer.assertValueCount(2)
            .assertValueAt(0) { viewState -> viewState == initialState }
            .assertValueAt(1) { viewState -> viewState == loadingState }
    }

    @Test
    fun should_insert_detail_item_at_first_position_on_successful_post_fetching() {
        //given
        given(useCaseMock.postDetail(postOverview)).willReturn(Observable.just(post))
        val screenLoadSubject: PublishSubject<DetailEvents.ScreenLoad> = PublishSubject.create()
        val observer = detailViewModel.observeViewState().test()
        detailViewModel.registerToInputs(screenLoadSubject)
        //when
        screenLoadSubject.onNext(DetailEvents.ScreenLoad(postUi))

        //then
        observer.assertValueAt(2) { viewState -> viewState.detail[0] == detailItem }
    }

    @Test
    fun should_insert_comment_header_item_at_second_position_on_successful_post_fetching() {
        //given
        given(useCaseMock.postDetail(postOverview)).willReturn(Observable.just(post))
        val screenLoadSubject: PublishSubject<DetailEvents.ScreenLoad> = PublishSubject.create()
        val observer = detailViewModel.observeViewState().test()
        detailViewModel.registerToInputs(screenLoadSubject)
        //when
        screenLoadSubject.onNext(DetailEvents.ScreenLoad(postUi))

        //then
        observer.assertValueAt(2) { viewState -> viewState.detail[1] == commentHeaderItem }
    }

    @Test
    fun should_insert_comment_items_starting_from_third_position_on_successful_post_fetching() {
        //given
        given(useCaseMock.postDetail(postOverview)).willReturn(Observable.just(post))
        val screenLoadSubject: PublishSubject<DetailEvents.ScreenLoad> = PublishSubject.create()
        val observer = detailViewModel.observeViewState().test()
        detailViewModel.registerToInputs(screenLoadSubject)
        //when
        screenLoadSubject.onNext(DetailEvents.ScreenLoad(postUi))

        //then
        observer.assertValueAt(2) { viewState ->
            viewState.detail[2] == commentItems[0]
                    && viewState.detail[3] == commentItems[1]
                    && viewState.detail[4] == commentItems[2]
                    && viewState.detail[5] == commentItems[3]
                    && viewState.detail[6] == commentItems[4]
        }
    }

    @Test
    fun should_update_view_with_post_detail_on_successful_post_fetching() {
        //given
        given(useCaseMock.postDetail(postOverview)).willReturn(Observable.just(post))
        val screenLoadSubject: PublishSubject<DetailEvents.ScreenLoad> = PublishSubject.create()
        val observer = detailViewModel.observeViewState().test()
        detailViewModel.registerToInputs(screenLoadSubject)
        //when
        screenLoadSubject.onNext(DetailEvents.ScreenLoad(postUi))

        //then
        observer.assertValueCount(3)
            .assertValueAt(0) { viewState -> viewState == initialState }
            .assertValueAt(1) { viewState -> viewState == loadingState }
            .assertValueAt(2) { viewState ->
                viewState.detail.size == 7
                        && viewState.detail[0] == detailItem
                        && viewState.detail[1] == commentHeaderItem
                        && viewState.detail[2] == commentItems[0]
                        && viewState.detail[3] == commentItems[1]
                        && viewState.detail[4] == commentItems[2]
                        && viewState.detail[5] == commentItems[3]
                        && viewState.detail[6] == commentItems[4]
            }
    }

    @Test
    fun should_trigger_post_fetching_only_once_after_multiple_consecutive_screen_load_events() {
        //given
        given(useCaseMock.postDetail(postOverview)).willReturn(Observable.just(post))
        val screenLoadSubject: PublishSubject<DetailEvents.ScreenLoad> = PublishSubject.create()

        detailViewModel.observeViewState().test()
        detailViewModel.registerToInputs(screenLoadSubject)

        //when
        screenLoadSubject.onNext(DetailEvents.ScreenLoad(postUi))
        screenLoadSubject.onNext(DetailEvents.ScreenLoad(postUi))
        screenLoadSubject.onNext(DetailEvents.ScreenLoad(postUi))
        //then
        verify(useCaseMock, times(1)).postDetail(postOverview)
    }

    @Test
    fun should_update_view_state_only_once_after_multiple_consecutive_screen_load_events() {
        //given
        given(useCaseMock.postDetail(postOverview)).willReturn(Observable.just(post))
        val screenLoadSubject: PublishSubject<DetailEvents.ScreenLoad> = PublishSubject.create()

        val observer = detailViewModel.observeViewState().test()
        detailViewModel.registerToInputs(screenLoadSubject)
        //when
        screenLoadSubject.onNext(DetailEvents.ScreenLoad(postUi))
        screenLoadSubject.onNext(DetailEvents.ScreenLoad(postUi))
        screenLoadSubject.onNext(DetailEvents.ScreenLoad(postUi))
        //then
        observer.assertValueCount(3)
            .assertValueAt(0) { viewState -> viewState == initialState }
            .assertValueAt(1) { viewState -> viewState == loadingState }
            .assertValueAt(2) { viewState -> viewState.detail == detailList }
    }

    @Test
    fun should_update_view_with_error_state_on_fetch_error() {
        //given
        val exception = Exception("an error occurred")
        given(useCaseMock.postDetail(postOverview)).willReturn(Observable.error(exception))
        val screenLoadSubject: PublishSubject<DetailEvents.ScreenLoad> = PublishSubject.create()

        detailViewModel.registerToInputs(screenLoadSubject)

        val observer = detailViewModel.observeViewState().test()

        //when
        screenLoadSubject.onNext(DetailEvents.ScreenLoad(postUi))
        //then
        observer.assertValueCount(3)
            .assertValueAt(0) { viewState -> viewState == initialState }
            .assertValueAt(1) { viewState -> viewState == loadingState }
            .assertValueAt(2) { viewState -> viewState == errorState }
    }

    @Test
    fun should_trigger_post_fetching_after_retry() {
        //given
        given(useCaseMock.postDetail(postOverview)).willReturn(Observable.error(Exception()))
        val retrySubject: PublishSubject<DetailEvents.Retry> = PublishSubject.create()
        val screenLoadSubject: PublishSubject<DetailEvents.ScreenLoad> = PublishSubject.create()

        detailViewModel.registerToInputs(retrySubject, screenLoadSubject)

        detailViewModel.observeViewState().test()
        screenLoadSubject.onNext(DetailEvents.ScreenLoad(postUi))

        //when
        retrySubject.onNext(DetailEvents.Retry(postUi))
        //then
        verify(useCaseMock, times(2)).postDetail(postOverview)
    }

    @Test
    fun should_update_view_with_loading_state_after_retry() {
        //given
        val exception = Exception("an error occurred")
        given(useCaseMock.postDetail(postOverview))
            .willReturn(Observable.error(exception))//Fails first
            .willReturn(Observable.empty())        //Empty on second call

        val retrySubject: PublishSubject<DetailEvents.Retry> = PublishSubject.create()
        val screenLoadSubject: PublishSubject<DetailEvents.ScreenLoad> = PublishSubject.create()

        detailViewModel.registerToInputs(retrySubject, screenLoadSubject)

        val observer = detailViewModel.observeViewState().test()

        //when
        screenLoadSubject.onNext(DetailEvents.ScreenLoad(postUi))
        retrySubject.onNext(DetailEvents.Retry(postUi))
        //then
        observer.assertValueCount(4)
            .assertValueAt(0) { viewState -> viewState == initialState }
            .assertValueAt(1) { viewState -> viewState == loadingState }
            .assertValueAt(2) { viewState -> viewState == errorState }
            .assertValueAt(3) { viewState -> viewState == loadingState }
    }

    @Test
    fun should_update_view_with_post_detail_on_successful_retry() {
        //given
        val exception = Exception("an error occurred")
        given(useCaseMock.postDetail(postOverview))
            .willReturn(Observable.error(exception))  //Fails first
            .willReturn(Observable.just(post)) //Succeeds on second call
        val retrySubject: PublishSubject<DetailEvents.Retry> = PublishSubject.create()
        val screenLoadSubject: PublishSubject<DetailEvents.ScreenLoad> = PublishSubject.create()

        detailViewModel.registerToInputs(retrySubject, screenLoadSubject)

        val observer = detailViewModel.observeViewState().test()

        screenLoadSubject.onNext(DetailEvents.ScreenLoad(postUi)) // First call => Failure

        //when
        retrySubject.onNext(DetailEvents.Retry(postUi)) // Second call => Succeeds
        //then
        observer.assertValueCount(5)
            .assertValueAt(0) { viewState -> viewState == initialState }
            .assertValueAt(1) { viewState -> viewState == loadingState }
            .assertValueAt(2) { viewState -> viewState == errorState }
            .assertValueAt(3) { viewState -> viewState == loadingState }
            .assertValueAt(4) { viewState -> viewState.detail == detailList }
    }

    @Test
    fun should_map_post_to_detail_items_and_wrap_it_into_result_and_state() {
        val expected = State.Success(ResultDetailEvent.DetailLoadedResult(details))
        Truth.assertThat(post.toSuccessResult()).isEqualTo(expected)
    }

    @Test
    fun should_wrap_detail_items_into_result_and_state() {
        val expected = State.Success(ResultDetailEvent.DetailLoadedResult(details))
        Truth.assertThat(details.toSuccessResult()).isEqualTo(expected)
    }

    @Test
    fun should_wrap_throwable_items_into_result_and_state() {
        val throwable = Throwable()
        val expected = State.Error(ResultDetailEvent.LoadingFailedResult(throwable))
        Truth.assertThat(throwable.toDetailEventResult()).isEqualTo(expected)
    }
}