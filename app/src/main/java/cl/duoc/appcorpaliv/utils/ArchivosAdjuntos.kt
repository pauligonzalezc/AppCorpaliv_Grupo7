package cl.duoc.appcorpaliv.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.graphics.createBitmap
import cl.duoc.appcorpaliv.model.Adjunto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

//archivos adjuntos del voluntariado -> copia a la carpeta privada de la app (filesDir/adjuntos)
object ArchivosAdjuntos {

    private fun carpeta(context: Context): File =
        File(context.filesDir, "adjuntos").apply { mkdirs() }

    // archivo vacio
    fun nuevoArchivoFoto(context: Context): File =
        File(carpeta(context), "foto_${System.currentTimeMillis()}.jpg")

    fun adjuntoDesdeFoto(archivo: File): Adjunto =
        Adjunto(uri = Uri.fromFile(archivo).toString(), tipo = "image/jpeg", nombre = archivo.name)

    // copia archivo elegido dentro de la app
    suspend fun copiarDocumento(context: Context, uri: Uri): Adjunto? = withContext(Dispatchers.IO) {
        try {
            val resolver = context.contentResolver
            val tipo = resolver.getType(uri) ?: "application/octet-stream"
            var nombre = "documento"
            resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) nombre = cursor.getString(0) ?: nombre
            }
            val destino = File(carpeta(context), "${System.currentTimeMillis()}_$nombre")
            resolver.openInputStream(uri)?.use { entrada ->
                destino.outputStream().use { salida -> entrada.copyTo(salida) }
            } ?: return@withContext null
            Adjunto(uri = Uri.fromFile(destino).toString(), tipo = tipo, nombre = nombre)
        } catch (e: Exception) {
            null
        }
    }

    private fun archivoDe(adjunto: Adjunto): File? =
        Uri.parse(adjunto.uri).path?.let { File(it) }?.takeIf { it.exists() }

    // abre el adjunto con la app del teléfono
    fun abrir(context: Context, adjunto: Adjunto) {
        val archivo = archivoDe(adjunto)
        if (archivo == null) {
            Toast.makeText(context, "El archivo ya no está disponible", Toast.LENGTH_SHORT).show()
            return
        }
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, adjunto.tipo)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "No hay una app para abrir este archivo", Toast.LENGTH_SHORT).show()
        }
    }

    // vista previa
    suspend fun primeraPaginaPdf(adjunto: Adjunto, ancho: Int = 1080): Bitmap? = withContext(Dispatchers.IO) {
        val archivo = archivoDe(adjunto) ?: return@withContext null
        try {
            ParcelFileDescriptor.open(archivo, ParcelFileDescriptor.MODE_READ_ONLY).use { descriptor ->
                PdfRenderer(descriptor).use { renderer ->
                    if (renderer.pageCount == 0) return@withContext null
                    renderer.openPage(0).use { pagina ->
                        val alto = (ancho.toFloat() / pagina.width * pagina.height).toInt()
                        val bitmap = createBitmap(ancho, alto)
                        bitmap.eraseColor(Color.WHITE)
                        pagina.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        bitmap
                    }
                }
            }
        } catch (e: Exception) {
            null
        }
    }
}
