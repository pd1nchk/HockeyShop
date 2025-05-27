package com.podolyanchik.hockeyshop

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HockeyShopApp : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()
        // Инициализация других компонентов приложения при необходимости
    }

    // Настройка Coil для обработки различных типов изображений, включая URL с параметрами
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .crossfade(true) // Плавный переход при загрузке
            .respectCacheHeaders(false) // Игнорировать заголовки кэша, позволяя кэшировать все изображения
            .components {
                // Поддержка SVG изображений
                add(SvgDecoder.Factory())
                // Поддержка GIF изображений
                if (android.os.Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }
} 