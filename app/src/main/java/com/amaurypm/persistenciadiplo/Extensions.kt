package com.amaurypm.persistenciadiplo

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

object Extensions {
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = "settings"
    )
}