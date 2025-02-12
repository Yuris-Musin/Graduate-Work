package ru.musindev.graduate_work.di.modules

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.musindev.graduate_work.views.about.AboutFragment
import ru.musindev.graduate_work.views.account.AccountFragment
import ru.musindev.graduate_work.views.calendar.CalendarDialogFragment
import ru.musindev.graduate_work.views.search.SearchFragment

@Module
interface MainModule {

    @ContributesAndroidInjector
    fun bindSearchFragment(): SearchFragment

    @ContributesAndroidInjector
    fun bindAccountFragment(): AccountFragment

    @ContributesAndroidInjector
    fun bindAboutFragment(): AboutFragment

    @ContributesAndroidInjector
    fun bindCalendarDialogFragment(): CalendarDialogFragment

}