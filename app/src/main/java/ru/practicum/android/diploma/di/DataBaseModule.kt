package ru.practicum.android.diploma.di

import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module
import ru.practicum.android.diploma.data.db.AppDataBase
import ru.practicum.android.diploma.data.db.Mappers

val databaseModule: Module = module {
    single<AppDataBase> {
        AppDataBase.getDb(androidContext())
    }

    single { get<AppDataBase>().favoritesDao() }
    single { Mappers() }
}
