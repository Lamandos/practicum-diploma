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
import kotlin.coroutines.cancellation.CancellationException

class VacancyViewModel(
    private val interactor: VacancyInteractor
) : ViewModel() {

    companion object {
        private const val DEFAULT_ERROR_CODE = 500
    }

    private val _vacancyDetails = MutableLiveData<VacancyResult<VacancyDetails>>()
    val vacancyDetails: LiveData<VacancyResult<VacancyDetails>> get() = _vacancyDetails

    fun loadVacancyDetails(id: String) {
        viewModelScope.launch {
            try {
                val result = interactor.getVacancyDetails(id)

                if (result.isSuccess) {
                    val data = result.getOrNull()
                    if (data != null) {
                        _vacancyDetails.postValue(VacancyResult.Success(data))
                    } else {
                        _vacancyDetails.postValue(VacancyResult.Error(DEFAULT_ERROR_CODE))
                    }
                } else {
                    handleResultException(result.exceptionOrNull())
                }
            } catch (e: IOException) {
                _vacancyDetails.postValue(VacancyResult.Error(DEFAULT_ERROR_CODE))
                e.printStackTrace()
            } catch (e: HttpException) {
                _vacancyDetails.postValue(VacancyResult.Error(e.code()))
                e.printStackTrace()
            } catch (e: CancellationException) {
                throw e
            }
        }
    }

    private fun handleResultException(exception: Throwable?) {
        val code = when (exception) {
            is HttpException -> exception.code()
            is IOException -> DEFAULT_ERROR_CODE
            else -> DEFAULT_ERROR_CODE
        }
        _vacancyDetails.postValue(VacancyResult.Error(code))
        exception?.printStackTrace()
    }
}
