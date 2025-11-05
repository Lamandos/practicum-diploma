package ru.practicum.android.diploma.presentation.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import ru.practicum.android.diploma.domain.interactor.VacancyInteractor
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails
import java.io.IOException

class VacancyViewModel(
    private val interactor: VacancyInteractor
) : ViewModel() {

    private val _vacancyDetails = MutableLiveData<VacancyResult<VacancyDetails>>()
    val vacancyDetails: LiveData<VacancyResult<VacancyDetails>> get() = _vacancyDetails

    fun loadVacancyDetails(id: String) {
        viewModelScope.launch {
            try {
                val result = interactor.getVacancyDetails(id)

                if (result.isSuccess) {
                    result.getOrNull()?.let {
                        _vacancyDetails.postValue(VacancyResult.Success(it))
                    } ?: run {
                        _vacancyDetails.postValue(VacancyResult.Error(500))
                    }
                } else {
                    val exception = result.exceptionOrNull()
                    val code = if (exception is HttpException) exception.code() else 500
                    _vacancyDetails.postValue(VacancyResult.Error(code))
                }
            } catch (e: IOException) {
                _vacancyDetails.postValue(VacancyResult.Error(500))
            } catch (e: Exception) {
                _vacancyDetails.postValue(VacancyResult.Error(500))
            }
        }
    }
}
