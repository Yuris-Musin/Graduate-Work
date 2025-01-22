package ru.musindev.graduate_work.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import ru.musindev.graduate_work.views.search.SearchViewModel
import ru.musindev.graduate_work.views.search.ViewModelFactory

@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    abstract fun bindSearchViewModel(viewModel: SearchViewModel): ViewModel
}