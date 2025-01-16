package ru.musindev.graduate_work.views.about

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.AndroidSupportInjection
import ru.musindev.graduate_work.R
import ru.musindev.graduate_work.databinding.FragmentAboutBinding
import ru.musindev.graduate_work.databinding.FragmentSearchBinding
import ru.musindev.graduate_work.views.search.SearchViewModel

class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AboutViewModel

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

}