package ru.practicum.android.diploma.data.mappers

import ru.practicum.android.diploma.data.dto.vacancydetailsdto.AddressDto
import ru.practicum.android.diploma.domain.models.vacancydetails.Address

fun AddressDto.toDomain(): Address = Address(
    city = city,
    street = street,
    building = building,
    fullAddress = fullAddress
)
