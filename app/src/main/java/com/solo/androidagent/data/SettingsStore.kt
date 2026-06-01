package com.solo.androidagent.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.solo.androidagent.ui.UiSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsStore(private val context: Context) {
    private val baseUrlKey = stringPreferencesKey("base_url")
    private val apiKeyKey = stringPreferencesKey("api_key")
    private val modelKey = stringPreferencesKey("model")
    private val systemPromptKey = stringPreferencesKey("system_prompt")

    val settings: Flow<UiSettings> =
        context.dataStore.data.map { prefs ->
            UiSettings(
                baseUrl = prefs[baseUrlKey] ?: UiSettings().baseUrl,
                apiKey = prefs[apiKeyKey] ?: UiSettings().apiKey,
                model = prefs[modelKey] ?: UiSettings().model,
                systemPrompt = prefs[systemPromptKey] ?: UiSettings().systemPrompt,
            )
        }

    suspend fun save(settings: UiSettings) {
        context.dataStore.edit { prefs ->
            prefs[baseUrlKey] = settings.baseUrl
            prefs[apiKeyKey] = settings.apiKey
            prefs[modelKey] = settings.model
            prefs[systemPromptKey] = settings.systemPrompt
        }
    }
}

