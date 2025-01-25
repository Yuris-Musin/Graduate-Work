package ru.musindev.graduate_work.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import ru.musindev.graduate_work.App
import ru.musindev.graduate_work.di.modules.MainModule
import ru.musindev.graduate_work.di.modules.NetworkModule
import ru.musindev.graduate_work.di.modules.ViewModelModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        MainModule::class,
        ViewModelModule::class,
        NetworkModule::class,
    ]
)

interface AppComponent : AndroidInjector<App> {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun withContext(context: Context): Builder
        fun build(): AppComponent
    }
}