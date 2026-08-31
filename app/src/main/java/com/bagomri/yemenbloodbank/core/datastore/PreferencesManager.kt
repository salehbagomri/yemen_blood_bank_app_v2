package com.bagomri.yemenbloodbank.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "yemen_blood_bank_prefs")

class PreferencesManager(private val context: Context) {

    companion object {
        private val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        private val KEY_CACHED_USER_TYPE = stringPreferencesKey("cached_user_type")
        private val KEY_HOSPITAL_GOVERNORATE = stringPreferencesKey("hospital_governorate")
    }

    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_ONBOARDING_COMPLETED] ?: false
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_ONBOARDING_COMPLETED] = completed
        }
    }

    val cachedUserType: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[KEY_CACHED_USER_TYPE]
    }

    suspend fun setCachedUserType(type: String?) {
        context.dataStore.edit { preferences ->
            if (type != null) {
                preferences[KEY_CACHED_USER_TYPE] = type
            } else {
                preferences.remove(KEY_CACHED_USER_TYPE)
            }
        }
    }

    val cachedHospitalGovernorate: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[KEY_HOSPITAL_GOVERNORATE]
    }

    suspend fun setCachedHospitalGovernorate(gov: String?) {
        context.dataStore.edit { preferences ->
            if (gov != null) {
                preferences[KEY_HOSPITAL_GOVERNORATE] = gov
            } else {
                preferences.remove(KEY_HOSPITAL_GOVERNORATE)
            }
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.remove(KEY_CACHED_USER_TYPE)
            preferences.remove(KEY_HOSPITAL_GOVERNORATE)
        }
    }
}
