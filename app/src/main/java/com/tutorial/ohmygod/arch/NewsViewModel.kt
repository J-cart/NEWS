package com.tutorial.ohmygod.arch

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.tutorial.ohmygod.db.Article
import com.tutorial.ohmygod.db.JsonResponse
import com.tutorial.ohmygod.db.SavedArticle
import com.tutorial.ohmygod.utils.DispatcherProvider
import com.tutorial.ohmygod.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NewsViewModel @Inject constructor(
    private val repository: MainNewsRepository,
    val dispatcher: DispatcherProvider
) : ViewModel() {
    //TODO -- GET THE RESULT FROM THE REPOSITORY AND SETUP A EVENT TO PASS THE DATA TO THE UI
    private val _articlesEvent = Channel<Events>()
    val articlesEvent = _articlesEvent.receiveAsFlow()

    sealed class Events {
        object Successful : Events()
        object Failure : Events()
    }

    private val existingStatus = MutableLiveData<Int>()

    fun checkExisting(article: SavedArticle) {
        viewModelScope.launch {
            existingStatus.value = repository.checkIfSavedExist(article.url.toString())
            if (existingStatus.value!! > 0) {
                _articlesEvent.send(Events.Failure)
                return@launch
            }
            saveArticle(article)
            _articlesEvent.send(Events.Successful)
        }

    }

    private val _breakingNewsEvent = Channel<Events>()
    val breakingNewsEvent = _breakingNewsEvent.receiveAsFlow()

    fun checkSizeFromDB() {
        viewModelScope.launch {
           val count = repository.getAllItemsCount()
            if(count <= 0){
                _breakingNewsEvent.send(Events.Failure)
            }else{
                _breakingNewsEvent.send(Events.Successful)
            }
        }

    }


    //PAGING
    val pagingNews = repository.getPagingNews().cachedIn(viewModelScope)
    private val searchQuery = MutableLiveData("")

    fun querySearch(query: String) {
        searchQuery.value = query
    }

    val pagingSearchNews = searchQuery.switchMap { query ->
        repository.getPagingSearchNews(query).cachedIn(viewModelScope)
    }

    val mediatorPagingNews = repository.getMediatorPagingNews().cachedIn(viewModelScope)


    // region NORMAL REQUEST
    private val _breakingNews = MutableLiveData<Resource<JsonResponse>>()
    val breakingNews: LiveData<Resource<JsonResponse>> get() = _breakingNews

    private val _searchNews = MutableLiveData<Resource<JsonResponse>>(Resource.Empty())
    val searchNews: LiveData<Resource<JsonResponse>> get() = _searchNews

    private var breakingNewspages = 1
    private var searchNewspages = 1

    init {
//       handleResponse()
        getAllSavedNews()
    }


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

    private val _allSavedNews = MutableLiveData<List<SavedArticle>>()
    val allSavedNews :LiveData<List<SavedArticle>> get() = _allSavedNews

    fun getAllSavedNews() {
        viewModelScope.launch {
            repository.getAllSavedNews().collect {
                _allSavedNews.value = it
            }
        }

    }

    fun saveArticle(article: SavedArticle) {
        viewModelScope.launch {
            repository.saveArticle(article)
        }
    }

    fun deleteSavedArticle(article: SavedArticle) {
        viewModelScope.launch {
            repository.deleteSavedArticle(article)
            getAllSavedNews()
        }
    }

    fun deleteAllSavedNews() =
        viewModelScope.launch {
            repository.deleteAllSaved()
            
        }



    //endregion

}
