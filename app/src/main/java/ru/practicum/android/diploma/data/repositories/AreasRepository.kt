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
                    result.areas.toList()
                } else {
                    null
                }
            }
            is ResponseError -> null
            else -> null
        }
    }

    private fun collectRegions(root: FilterAreaDto): List<FilterAreaDto> {
        val result = mutableListOf<FilterAreaDto>()

        for (child in root.areas) {
            // Если у узла нет детей — это конечный регион, добавляем
            if (child.areas.isEmpty()) {
                result.add(child)
            } else {
                // Иначе идём глубже
                result.addAll(collectRegions(child))
            }
        }
        return result
    }
    suspend fun getAllRegions(): List<FilterAreaDto>? {
        val allAreas = getAllAreas() ?: return null
        val regions = mutableListOf<FilterAreaDto>()

        allAreas.forEach { country ->
            regions.addAll(collectRegions(country))
        }

        return regions
    }

    suspend fun getRegionsByCountry(countryId: Int): List<FilterAreaDto>? {
        val allAreas = getAllAreas() ?: return null
        val country = allAreas.firstOrNull { it.id == countryId } ?: return null

        // собираем всех потомков (регионы)
        return collectRegions(country)
    }
}
