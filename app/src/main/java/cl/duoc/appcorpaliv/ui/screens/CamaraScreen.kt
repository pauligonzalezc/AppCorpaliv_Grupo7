package cl.duoc.appcorpaliv.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cameraswitch
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.NoPhotography
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cl.duoc.appcorpaliv.utils.ArchivosAdjuntos
import cl.duoc.appcorpaliv.viewmodel.ColaboraViewModel
import cl.duoc.appcorpaliv.viewmodel.MainViewModel

//camara propia, la foto se guarda en filesDir/adjuntos
@Composable
fun CamaraScreen(
    navController : NavController,
    viewModel : MainViewModel = viewModel(),
    colaboraViewModel : ColaboraViewModel
) {
    val context = LocalContext.current

    var tienePermiso by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }
    var permisoRechazado by remember { mutableStateOf(false) }

    val permisoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        tienePermiso = concedido
        permisoRechazado = !concedido
    }

    // pide el permiso se abre la pantalla
    LaunchedEffect(Unit) {
        if (!tienePermiso) permisoLauncher.launch(Manifest.permission.CAMERA)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (tienePermiso) {
            VistaCamara(
                onFotoTomada = { adjunto ->
                    colaboraViewModel.onAdjuntoChange(adjunto)
                    viewModel.navigateBack()
                },
                onCerrar = { viewModel.navigateBack() }
            )
        } else {
            SinPermiso(
                permisoRechazado = permisoRechazado,
                onPedirPermiso = { permisoLauncher.launch(Manifest.permission.CAMERA) },
                onAbrirAjustes = {
                    context.startActivity(
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null))
                    )
                },
                onCerrar = { viewModel.navigateBack() }
            )
        }
    }
}

@Composable
private fun VistaCamara(
    onFotoTomada: (cl.duoc.appcorpaliv.model.Adjunto) -> Unit,
    onCerrar: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var capturando by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val controlador = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(LifecycleCameraController.IMAGE_CAPTURE)
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        }
    }

    DisposableEffect(lifecycleOwner) {
        controlador.bindToLifecycle(lifecycleOwner)
        onDispose { controlador.unbind() }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    controller = controlador
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        IconButton(
            onClick = onCerrar,
            modifier = Modifier
                .statusBarsPadding()
                .padding(8.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(Icons.Rounded.Close, contentDescription = "Cerrar cámara", tint = Color.White)
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.4f))
                .navigationBarsPadding()
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            error?.let {
                Text(text = it, color = Color.White, modifier = Modifier.padding(bottom = 12.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.size(48.dp))

                // boton de captura
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .border(4.dp, Color.White, CircleShape)
                        .padding(8.dp)
                        .clip(CircleShape)
                        .background(if (capturando) Color.Gray else Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    if (capturando) {
                        CircularProgressIndicator(modifier = Modifier.size(28.dp), color = Color.White)
                    } else {
                        IconButton(
                            onClick = {
                                capturando = true
                                error = null
                                val archivo = ArchivosAdjuntos.nuevoArchivoFoto(context)
                                controlador.takePicture(
                                    ImageCapture.OutputFileOptions.Builder(archivo).build(),
                                    ContextCompat.getMainExecutor(context),
                                    object : ImageCapture.OnImageSavedCallback {
                                        override fun onImageSaved(resultado: ImageCapture.OutputFileResults) {
                                            capturando = false
                                            onFotoTomada(ArchivosAdjuntos.adjuntoDesdeFoto(archivo))
                                        }

                                        override fun onError(excepcion: ImageCaptureException) {
                                            capturando = false
                                            error = "No se pudo tomar la foto, intenta de nuevo"
                                        }
                                    }
                                )
                            },
                            modifier = Modifier.fillMaxSize()
                        ) {}
                    }
                }

                IconButton(
                    onClick = {
                        controlador.cameraSelector =
                            if (controlador.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
                            else CameraSelector.DEFAULT_BACK_CAMERA
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Rounded.Cameraswitch, contentDescription = "Cambiar cámara", tint = Color.White)
                }
            }
        }
    }
}

@Composable
private fun SinPermiso(
    permisoRechazado: Boolean,
    onPedirPermiso: () -> Unit,
    onAbrirAjustes: () -> Unit,
    onCerrar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Rounded.NoPhotography, contentDescription = null, modifier = Modifier.size(64.dp))
        Spacer(Modifier.height(16.dp))
        Text(
            text = if (permisoRechazado) "Para tomar la foto necesitamos permiso para usar la cámara." else "Solicitando permiso de cámara…",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        if (permisoRechazado) {
            Spacer(Modifier.height(24.dp))
            Button(onClick = onPedirPermiso, modifier = Modifier.fillMaxWidth()) { Text("Dar permiso") }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = onAbrirAjustes, modifier = Modifier.fillMaxWidth()) { Text("Abrir ajustes") }
        }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = onCerrar, modifier = Modifier.fillMaxWidth()) { Text("Volver") }
    }
}
