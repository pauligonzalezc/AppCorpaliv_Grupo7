package cl.duoc.appcorpaliv.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "preferencias_accesibilidad")

class AccesibilidadDataStore(private val context: Context) {

    private val TAMANO_TEXTO = floatPreferencesKey("tamano_texto")
    private val ALTO_CONTRASTE = booleanPreferencesKey("alto_contraste")
    private val LEER_EN_VOZ_ALTA = booleanPreferencesKey("leer_en_voz_alta")
    private val REDUCIR_ANIMACIONES = booleanPreferencesKey("reducir_animaciones")

    suspend fun guardarPreferencias(tamanoTexto: Float, altoContraste: Boolean, leerEnVozAlta: Boolean, reducirAnimaciones: Boolean) {
        context.dataStore.edit { preferencias ->
            preferencias[TAMANO_TEXTO] = tamanoTexto
            preferencias[ALTO_CONTRASTE] = altoContraste
            preferencias[LEER_EN_VOZ_ALTA] = leerEnVozAlta
            preferencias[REDUCIR_ANIMACIONES] = reducirAnimaciones
        }
    }

    fun obtenerTamanoTexto(): Flow<Float?> {
        return context.dataStore.data.map { preferencias ->
            preferencias[TAMANO_TEXTO]
        }
    }

    fun obtenerAltoContraste(): Flow<Boolean?> {
        return context.dataStore.data.map { preferencias ->
            preferencias[ALTO_CONTRASTE]
        }
    }

    fun obtenerLeerEnVozAlta(): Flow<Boolean?> {
        return context.dataStore.data.map { preferencias ->
            preferencias[LEER_EN_VOZ_ALTA]
        }
    }

    fun obtenerReducirAnimaciones(): Flow<Boolean?> {
        return context.dataStore.data.map { preferencias ->
            preferencias[REDUCIR_ANIMACIONES]
        }
    }
}
