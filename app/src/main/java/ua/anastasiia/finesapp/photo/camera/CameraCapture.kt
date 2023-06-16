package ua.anastasiia.finesapp.photo.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.provider.Telephony.Mms.Part.FILENAME
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY
import androidx.camera.core.ImageCapture.OutputFileOptions
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import ua.anastasiia.finesapp.R
import ua.anastasiia.finesapp.util.Permission
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@SuppressLint("RestrictedApi")
@Suppress("DEPRECATION")
@ExperimentalPermissionsApi
@ExperimentalCoroutinesApi
@Composable
fun CameraCapture(
    modifier: Modifier = Modifier,
    cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
    onImageFile: (File) -> Unit = { }
//    onImageFile: (Uri) -> Unit = { }
) {
    val context = LocalContext.current
    Permission(permission = Manifest.permission.CAMERA,
        rationale = stringResource(R.string.camera_permission),
        permissionNotAvailableContent = {
            Column(modifier) {
                Text(stringResource(R.string.no_camera))
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    })
                }) {
                    Text(stringResource(R.string.open_settings))
                }
            }
        }) {
        Box(modifier = modifier) {
            val lifecycleOwner = LocalLifecycleOwner.current
            val coroutineScope = rememberCoroutineScope()
            var previewUseCase by remember { mutableStateOf<UseCase>(Preview.Builder().build()) }
            val imageCaptureUseCase by remember {
                mutableStateOf(
                    ImageCapture.Builder().setCaptureMode(CAPTURE_MODE_MAXIMIZE_QUALITY).build()
                )
            }
            Box {
                CameraPreview(modifier = Modifier.fillMaxSize(), onUseCase = {
                    previewUseCase = it
                })
                CapturePictureButton(modifier = Modifier
                    .size(100.dp)
                    .padding(16.dp)
                    .align(Alignment.BottomCenter),
                    onClick = {
                        coroutineScope.launch {

//                            // Create time stamped name and MediaStore entry.
//                            val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
//                                .format(System.currentTimeMillis())
//                            val contentValues = ContentValues().apply {
//                                put(MediaStore.MediaColumns.DISPLAY_NAME, name)
//                                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
//                                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
//                                    put(
//                                        MediaStore.Images.Media.RELATIVE_PATH,
//                                        "Pictures/ParkingFines"
//                                    )
//                                }
//                            }
//                            Log.e("contentValues", contentValues.toString())
//
//                            // Create output options object which contains file + metadata
//                            val externalContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//                            val outputFileOptions = ImageCapture.OutputFileOptions
//                                .Builder(
//                                    context.contentResolver,
//                                    externalContentUri,
//                                    contentValues
//                                )
//                                .build()

                            val file = File(
                                context.externalMediaDirs.first(),
                                "${System.currentTimeMillis()}.jpg"
                            )

                            val outputFileOptions = OutputFileOptions.Builder(file).build()

//                            Log.e("externalContentUri", externalContentUri.toString())
                            Log.e("outputFileOptions", outputFileOptions.file.toString())
                            imageCaptureUseCase.takePicture(outputFileOptions,
                                context.executor,
                                object : ImageCapture.OnImageSavedCallback {
                                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
//                                        val savedUri = outputFileResults.savedUri
//                                        val savedFile = outputFileOptions.file
//                                        savedUri?.let { onImageFile(File(it.path)) }
                                        onImageFile(file)
//                                        savedFile?.let { onImageFile(it) }
//                                        onImageFile(file)
                                        val savedUri = Uri.fromFile(file)

                                        val msg =
                                            "${context.resources.getString(R.string.photoCaptureSucceeded)}: $savedUri"
//                                        val msg = "Photo capture succeeded: $savedFile"
                                        Log.d("imageuri", savedUri.toString())
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    }

                                    override fun onError(exception: ImageCaptureException) {
                                        exception.message?.let {
                                            Log.e(
                                                "ImageCaptureException",
                                                it
                                            )
                                        }
                                        Toast.makeText(
                                            context,
                                            context.resources.getString(R.string.somethingWentWrong),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                })
                        }
                    })
            }
            LaunchedEffect(previewUseCase) {
                val cameraProvider = context.getCameraProvider()
                try {
                    // Must unbind the use-cases before rebinding them.
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner, cameraSelector, previewUseCase, imageCaptureUseCase
                    )
                } catch (ex: Exception) {
                    Log.e("CameraCapture", "Failed to bind camera use cases", ex)
                }
            }
        }
    }
}


suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { future ->
        future.addListener(
            {
                continuation.resume(future.get())
            }, executor
        )
    }
}

val Context.executor: Executor
    get() = ContextCompat.getMainExecutor(this)