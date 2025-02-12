package ru.musindev.graduate_work.di.modules

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.musindev.graduate_work.views.about.AboutFragment
import ru.musindev.graduate_work.views.calendar.CalendarDialogFragment
import ru.musindev.graduate_work.views.search.SearchFragment
import ru.musindev.graduate_work.views.schedule_list.ScheduleListFragment
import ru.musindev.graduate_work.views.settings.SettingsFragment

@Module
interface MainModule {

    @ContributesAndroidInjector
    fun bindSearchFragment(): SearchFragment

    @ContributesAndroidInjector
    fun bindSettingsFragment(): SettingsFragment

    @ContributesAndroidInjector
    fun bindAboutFragment(): AboutFragment

    @ContributesAndroidInjector
    fun bindCalendarDialogFragment(): CalendarDialogFragment

    @ContributesAndroidInjector
    fun bindScheduleListFragment(): ScheduleListFragment

}