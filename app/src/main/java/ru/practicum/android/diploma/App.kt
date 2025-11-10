package ru.practicum.android.diploma

import android.app.Application
import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ru.practicum.android.diploma.di.dataModule
import ru.practicum.android.diploma.di.databaseModule
import ru.practicum.android.diploma.di.filterModule
import ru.practicum.android.diploma.di.industryModule
import ru.practicum.android.diploma.di.interactorModule
import ru.practicum.android.diploma.di.repositoryModule
import ru.practicum.android.diploma.di.viewModelModule

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this

        startKoin {
            androidContext(this@App)
            modules(
                dataModule,
                databaseModule,
                interactorModule,
                repositoryModule,
                filterModule,
                industryModule,
                viewModelModule
            )
        }
    }

    companion object {
        private var instance: App? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }
}
