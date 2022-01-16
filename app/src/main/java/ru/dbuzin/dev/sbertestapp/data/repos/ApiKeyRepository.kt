package ru.dbuzin.dev.sbertestapp.data.repos

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import javax.inject.Inject

class ApiKeyRepository @Inject constructor(context: Context) {
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    private val preferences = EncryptedSharedPreferences.create(
        SECURE_API_PREFS,
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    //Хранить ключ в коде не самая лучая идея, но апи не предлагает вариантов аутентофикации для получения ключа с сервиса
    init {
        setApiKey("ad837ec14152db53379a8648430b9930")
    }

    fun setApiKey(key: String) = preferences.edit().putString(API_KEY, key).apply()

    fun getApiKey() = preferences.getString(API_KEY, "") ?: "ad837ec14152db53379a8648430b9930"

    companion object {
        const val SECURE_API_PREFS = "SECURE_API_PREFS"
        private const val API_KEY = "API_KEY"
    }
}