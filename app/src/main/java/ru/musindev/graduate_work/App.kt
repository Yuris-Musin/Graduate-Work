package ru.musindev.graduate_work

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import ru.musindev.graduate_work.di.DaggerAppComponent

class App : DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<App> =
        DaggerAppComponent.builder().withContext(applicationContext).build()
}