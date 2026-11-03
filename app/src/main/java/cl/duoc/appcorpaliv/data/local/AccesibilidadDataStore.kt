package cl.duoc.appcorpaliv.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "preferencias_accesibilidad")

class AccesibilidadDataStore(private val context: Context) {

    private val TAMANO_TEXTO = floatPreferencesKey("tamano_texto")

    suspend fun guardarTamanoTexto(tamanoTexto: Float) {
        context.dataStore.edit { preferencias ->
            preferencias[TAMANO_TEXTO] = tamanoTexto
        }
    }

    fun obtenerTamanoTexto(): Flow<Float?> {
        return context.dataStore.data.map { preferencias ->
            preferencias[TAMANO_TEXTO]
        }
    }
}
