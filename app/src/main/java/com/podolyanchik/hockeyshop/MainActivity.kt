package com.podolyanchik.hockeyshop

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.LocaleList
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.podolyanchik.hockeyshop.data.local.DemoDataProvider
import com.podolyanchik.hockeyshop.presentation.screen.HockeyShopApp
import com.podolyanchik.hockeyshop.ui.theme.HockeyShopTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var demoDataProvider: DemoDataProvider
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Установка русского языка по умолчанию
        setLocale(this, "ru")
        
        // Инициализация демо-данных
        lifecycleScope.launch {
            demoDataProvider.initializeDemoData()
        }
        
        setContent {
            HockeyShopTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HockeyShopApp()
                }
            }
        }
    }
    
    // Обновленная функция для установки локали приложения (без deprecated методов)
    private fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        
        val config = Configuration(context.resources.configuration)
        val localeList = LocaleList(locale)
        config.setLocales(localeList)
        context.createConfigurationContext(config)
    }
} 