package ru.musindev.graduate_work.views.settings

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat.recreate
import dagger.android.support.AndroidSupportInjection
import ru.musindev.graduate_work.databinding.FragmentSettingsBinding
import ru.musindev.graduate_work.views.search.SearchFragment

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var viewModel: SettingsViewModel

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализация SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)

        // Установка начального состояния переключателя
        setInitialSwitchState()

        // Обработка изменения состояния переключателя
        binding.switchThemes.setOnCheckedChangeListener { _, isChecked ->
            toggleTheme(isChecked)
            saveThemeState(isChecked)
        }

    }

    private fun setInitialSwitchState() {
        // Загрузка сохраненного состояния темы
        val isDarkTheme = sharedPreferences.getBoolean("isDarkTheme", false)

        // Установка состояния переключателя и текста
        binding.switchThemes.isChecked = isDarkTheme
        binding.switchThemes.text = if (isDarkTheme) "Темная тема" else "Светлая тема"

        // Применение темы при запуске
        applyTheme(isDarkTheme)
    }

    private fun toggleTheme(isDarkTheme: Boolean) {
        // Переключение темы
        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            binding.switchThemes.text = "Темная тема"
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            binding.switchThemes.text = "Светлая тема"
        }

        // Перезапуск активности для применения новой темы
        requireActivity().recreate()
    }

    private fun saveThemeState(isDarkTheme: Boolean) {
        // Сохранение состояния темы в SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putBoolean("isDarkTheme", isDarkTheme)
        editor.apply()
    }

    private fun applyTheme(isDarkTheme: Boolean) {
        // Применение темы без перезапуска активности
        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }


}