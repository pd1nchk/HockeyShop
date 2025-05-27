package com.podolyanchik.hockeyshop.util

import android.util.Log
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Утилитный класс для работы с URL изображений
 */
object ImageUtil {

    private const val TAG = "ImageUtil"

    /**
     * Обрабатывает URL изображения для безопасного использования в Coil
     *
     * @param url исходный URL изображения
     * @return обработанный URL
     */
    fun processImageUrl(url: String): String {
        return normalizeImageUrl(url)
    }

    /**
     * Нормализует URL изображения для безопасного использования в Coil
     *
     * @param url исходный URL изображения
     * @return нормализованный URL
     */
    fun normalizeImageUrl(url: String): String {
        if (url.isBlank()) return ""

        // Удаляем префикс @ если он присутствует
        val cleanUrl = if (url.startsWith("@")) url.substring(1) else url

        return try {
            // Сохраняем параметры запроса в URL
            if (cleanUrl.contains("?") || cleanUrl.contains("&") || cleanUrl.contains("=")) {
                Log.d(TAG, "URL с параметрами: $cleanUrl")
                // URL с параметрами запроса, возвращаем как есть
                cleanUrl
            } else {
                cleanUrl
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при обработке URL: $e")
            cleanUrl
        }
    }

    /**
     * Проверяет, содержит ли URL параметры запроса
     *
     * @param url URL для проверки
     * @return true если URL содержит параметры запроса, иначе false
     */
    fun hasQueryParameters(url: String): Boolean {
        return url.contains("?") && (url.contains("=") || url.contains("&"))
    }
} 