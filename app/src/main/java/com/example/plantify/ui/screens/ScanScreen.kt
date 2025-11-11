package com.example.plantify.ui.screens

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import coil.compose.AsyncImage // <-- IMPORT DARI COIL
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.activity.compose.BackHandler

private const val TAG = "ScanScreen"
private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"

@Composable
fun ScanScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val imageCapture = remember { ImageCapture.Builder().build() }
    val scope = rememberCoroutineScope() // Untuk gimmick flash

    // --- State Baru ---
    // 1. State untuk menyimpan URI gambar pratinjau
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    // 2. State untuk gimmick flash
    var showFlash by remember { mutableStateOf(false) }
    // 3. State untuk izin kamera
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasCameraPermission = isGranted
        }
    )

    LaunchedEffect(key1 = Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // --- UI UTAMA DENGAN LOGIKA BARU ---
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        if (hasCameraPermission) {

            // Cek apakah kita di mode Pratinjau atau mode Kamera
            if (capturedImageUri != null) {
                BackHandler(enabled = true) {
                    // Saat 'Back' ditekan, buang foto dan kembali ke kamera
                    discardImage(capturedImageUri!!)
                    capturedImageUri = null
                }
                // --- UI MODE PRATINJAU FOTO ---
                PreviewScreen(
                    uri = capturedImageUri!!,
                    onConfirm = {
                        // Simpan foto ke galeri
                        saveImageToGallery(context, capturedImageUri!!)
                        // TODO: Lakukan sesuatu dengan foto ini (kirim ke server, dll)
                        Toast.makeText(context, "Foto disimpan!", Toast.LENGTH_SHORT).show()
                        // Kembali ke mode kamera
                        capturedImageUri = null
                    },
                    onDiscard = {
                        // Hapus file sementara
                        discardImage(capturedImageUri!!)
                        // Kembali ke mode kamera
                        capturedImageUri = null
                    }
                )

            } else {
                // --- UI MODE KAMERA (DENGAN TOMBOL SHUTTER) ---
                CameraPreview(
                    modifier = Modifier.fillMaxSize(),
                    lifecycleOwner = lifecycleOwner,
                    imageCapture = imageCapture
                )

                // Tombol Shutter (Tombol ambil gambar)
                IconButton(
                    onClick = {
                        // 1. Jalankan gimmick flash
                        scope.launch {
                            showFlash = true
                            delay(100)
                            showFlash = false
                        }

                        // 2. Ambil foto
                        takePhoto(
                            context = context,
                            imageCapture = imageCapture,
                            onImageSaved = { uri ->
                                // 3. Pindah ke mode pratinjau
                                capturedImageUri = uri
                            },
                            onError = { exception ->
                                Log.e(TAG, "Gagal mengambil foto:", exception)
                                Toast.makeText(context, "Gagal mengambil foto.", Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 76.dp)
                        .size(72.dp)
                        .background(Color.White, CircleShape)
                        .border(2.dp, Color.Black, CircleShape)
                ) {
                    // Ikon di dalam tombol shutter
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color.White, CircleShape)
                    )
                }
            }
        } else {
            // UI Jika izin ditolak
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Izin kamera ditolak. Harap berikan izin di Setelan.")
            }
        }

        // --- GIMMICK FLASH ---
        if (showFlash) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.8f)) // Flash putih
            )
        }
    }
}

// --- FUNGSI UI BARU UNTUK PRATINJAU ---
@Composable
fun PreviewScreen(
    uri: Uri,
    onConfirm: () -> Unit,
    onDiscard: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Tampilkan gambar menggunakan Coil
        AsyncImage(
            model = uri,
            contentDescription = "Pratinjau Foto",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // Penuhi layar
        )

        // Tombol Silang (X)
        IconButton(
            onClick = onDiscard,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(32.dp)
                .size(56.dp)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(Icons.Default.Close, "Buang", tint = Color.White)
        }

        // Tombol Centang (V)
        IconButton(
            onClick = onConfirm,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(32.dp)
                .size(56.dp)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(Icons.Default.Check, "Simpan", tint = Color.White)
        }
    }
}


// --- FUNGSI HELPER BARU ---

// Fungsi ini sekarang menyimpan ke FILE SEMENTARA
private fun takePhoto(
    context: Context,
    imageCapture: ImageCapture,
    onImageSaved: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    // Buat file sementara di cache aplikasi
    val photoFile = File(
        context.cacheDir, // Simpan di cache
        SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                val uri = Uri.fromFile(photoFile)
                onImageSaved(uri)
            }

            override fun onError(exception: ImageCaptureException) {
                onError(exception)
            }
        }
    )
}

// Fungsi baru untuk menyimpan dari file sementara ke Galeri
private fun saveImageToGallery(context: Context, uri: Uri) {
    val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
        .format(System.currentTimeMillis())

    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Plantify")
        }
    }

    val resolver = context.contentResolver
    val newImageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    try {
        if (newImageUri != null) {
            resolver.openOutputStream(newImageUri).use { outputStream ->
                resolver.openInputStream(uri).use { inputStream ->
                    inputStream?.copyTo(outputStream!!)
                }
            }
            // Hapus file sementara setelah berhasil disalin
            File(uri.path!!).delete()
        }
    } catch (e: Exception) {
        Log.e(TAG, "Gagal menyimpan ke galeri", e)
    }
}

// Fungsi baru untuk membuang file sementara
private fun discardImage(uri: Uri) {
    try {
        File(uri.path!!).delete()
    } catch (e: Exception) {
        Log.e(TAG, "Gagal menghapus file sementara", e)
    }
}


// --- FUNGSI KAMERA (TIDAK BERUBAH DARI SEBELUMNYA) ---
@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    lifecycleOwner: LifecycleOwner,
    imageCapture: ImageCapture,
    cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
) {
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val executor = ContextCompat.getMainExecutor(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Gagal bind CameraX", e)
                }
            }, executor)

            previewView
        },
        modifier = modifier
    )
}