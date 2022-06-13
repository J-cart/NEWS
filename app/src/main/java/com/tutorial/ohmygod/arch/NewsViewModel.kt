package com.tutorial.ohmygod.arch

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.tutorial.ohmygod.db.Article
import com.tutorial.ohmygod.db.JsonResponse
import com.tutorial.ohmygod.utils.DispatcherProvider
import com.tutorial.ohmygod.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NewsViewModel @Inject constructor(
    private val repository: MainNewsRepository,
    val dispatcher: DispatcherProvider
) : ViewModel() {

    private val _eventsChannel = Channel<Events>()
    val eventsChannel = _eventsChannel.receiveAsFlow()

    sealed class Events {
        object Successful : Events()
        object Failure : Events()
    }

    private val existingStatus = MutableLiveData<Int>()

    fun checkExisting(article: Article) {
        viewModelScope.launch {
            existingStatus.value = repository.checkIfExist(article.url.toString())
            if (existingStatus.value!! > 0) {
                _eventsChannel.send(Events.Failure)
                return@launch
            }
            insertArticle(article)
            _eventsChannel.send(Events.Successful)
        }

    }

    //TODO -- GET THE RESULT FROM THE REPOSITORY AND SETUP A EVENT TO PASS THE DATA TO THE UI

    val pagingNews = repository.getPagingNews().cachedIn(viewModelScope)
    private val searchQuery = MutableLiveData<String>("")

    fun querySearch(query: String){
        searchQuery.value = query
}
    val pagingSearchNews = searchQuery.switchMap { query->
        repository.getPagingSearchNews(query).cachedIn(viewModelScope)
    }

//    val mediatorPagingNews = repository.getMediatorPagingNews().cachedIn(viewModelScope)


    // region NORMAL REQUEST
    private val _breakingNews = MutableLiveData<Resource<JsonResponse>>()
    val breakingNews: LiveData<Resource<JsonResponse>> get() = _breakingNews

    private val _searchNews = MutableLiveData<Resource<JsonResponse>>(Resource.Empty())
    val searchNews: LiveData<Resource<JsonResponse>> get() = _searchNews

    private var breakingNewspages = 1
    private var searchNewspages = 1

//    init {
//       handleResponse()
//    }


    fun handleResponse() {
        viewModelScope.launch {
            _breakingNews.value = Resource.Loading()
            when (val rates = repository.getBreakingNews("us", breakingNewspages)) {
                is Resource.Successful -> rates.data?.let {
                    breakingNewspages++
                    _breakingNews.value = Resource.Successful(it)
                }
                is Resource.Failure -> rates.msg?.let { _breakingNews.value = Resource.Failure(it) }
            }
        }
    }

    fun handleSearch(query: String) {
        viewModelScope.launch {
            _searchNews.value = Resource.Loading()
            when (val rates = repository.getSearchNews(query, searchNewspages)) {
                is Resource.Successful -> rates.data?.let {
                    searchNewspages++
                    _searchNews.value = Resource.Successful(it)
                }
                is Resource.Failure -> rates.msg?.let { _searchNews.value = Resource.Failure(it) }
            }

        }
    }

    //endregion


    //region ROOM-DB
    fun getLocalNews() = repository.getAllNews().asLiveData()

    fun insertArticle(article: Article) {
        viewModelScope.launch {
            repository.insertArticle(article)
        }
    }

    fun deleteArticle(article: Article) {
        viewModelScope.launch {
            repository.deleteArticle(article)
        }
    }
    //endregion

}