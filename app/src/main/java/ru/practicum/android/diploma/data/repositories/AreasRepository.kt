package ru.practicum.android.diploma.data.repositories


import ru.practicum.android.diploma.data.dto.Response
import ru.practicum.android.diploma.data.dto.ResponseError
import ru.practicum.android.diploma.data.dto.ResponseSuccess
import ru.practicum.android.diploma.data.dto.filterdto.FilterAreaDto
import ru.practicum.android.diploma.data.network.FilterAreaResponse
import ru.practicum.android.diploma.data.network.NetworkClient


class AreasRepository(private val networkClient: NetworkClient) {

    suspend fun getAllAreas(): List<FilterAreaDto>? {
        val response: Response = networkClient.getAreas()

        return when (response) {
            is ResponseSuccess<*> -> {
                val result = response.data
                if (result is FilterAreaResponse) {
                    result.areas
                } else null
            }
            is ResponseError -> null
            else -> null
        }
    }
}
