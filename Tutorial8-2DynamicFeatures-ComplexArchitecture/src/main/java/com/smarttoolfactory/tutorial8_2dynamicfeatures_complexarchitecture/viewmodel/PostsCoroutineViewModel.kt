package com.smarttoolfactory.tutorial8_2dynamicfeatures_complexarchitecture.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smarttoolfactory.tutorial8_2dynamicfeatures_complexarchitecture.api.Post
import com.smarttoolfactory.tutorial8_2dynamicfeatures_complexarchitecture.api.RetrofitFactory
import com.smarttoolfactory.tutorial8_2dynamicfeatures_complexarchitecture.api.Status
import com.smarttoolfactory.tutorial8_2dynamicfeatures_complexarchitecture.api.Status.*
import com.smarttoolfactory.tutorial8_2dynamicfeatures_complexarchitecture.data.PostsRepository
import com.smarttoolfactory.tutorial8_2dynamicfeatures_complexarchitecture.data.PostsUseCase
import com.smarttoolfactory.tutorial8_2dynamicfeatures_complexarchitecture.data.ViewState
import com.smarttoolfactory.tutorial8_2dynamicfeatures_complexarchitecture.util.Event
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.launch

class PostsCoroutineViewModel : ViewModel() {

    private val _goToDetailScreen = MutableLiveData<Event<Post>>()
    val goToDetailScreen: LiveData<Event<Post>>
        get() = _goToDetailScreen

    private val _postViewState = MutableLiveData<ViewState<List<Post>>>()
    val postViewState: LiveData<ViewState<List<Post>>>
        get() = _postViewState

    private val postsUseCase by lazy {
        PostsUseCase(
            PostsRepository(
                RetrofitFactory.getPostApiCoroutines()
            )
        )
    }

    /**
     * Every thing in this function works in thread of [viewModelScope] other than network action
     * [viewModelScope] uses [MainCoroutineDispatcher.Main.immediate]
     */
    fun getPosts() {

        viewModelScope.launch {


            // Set current state to LOADING
            _postViewState.value =
                ViewState(
                    LOADING
                )

            // 🔥🔥 Get result from network, invoked in Retrofit's enque function thread
            val result = postsUseCase.getPosts()

            // Check and assign result to UI
            if (result.status == Status.SUCCESS) {
                _postViewState.value =
                    ViewState(
                        SUCCESS,
                        data = result.data
                    )
            } else {
                _postViewState.value =
                    ViewState(
                        ERROR,
                        error = result.error
                    )
            }
        }
    }

    fun onClick(post: Post) {
        _goToDetailScreen.value = Event(post)
    }


}