package com.tutorial.ohmygod.arch

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.tutorial.ohmygod.db.SavedArticle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NewsViewModel @Inject constructor(
    private val repository: MainNewsRepository
) : ViewModel() {
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
    private val searchQuery = MutableLiveData("")

    fun querySearch(query: String) {
        searchQuery.value = query
    }

    val pagingSearchNews = searchQuery.switchMap { query ->
        repository.getPagingSearchNews(query).cachedIn(viewModelScope)
    }

    val mediatorPagingNews = repository.getMediatorPagingNews().cachedIn(viewModelScope)



    init {
        getAllSavedNews()
    }



    //region ROOM-DB

    private val _allSavedNews = MutableLiveData<List<SavedArticle>>()
    val allSavedNews :LiveData<List<SavedArticle>> get() = _allSavedNews

   private fun getAllSavedNews() {
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
        }
    }

    fun deleteAllSavedNews() =
        viewModelScope.launch {
            repository.deleteAllSaved()
        }

    //endregion

}