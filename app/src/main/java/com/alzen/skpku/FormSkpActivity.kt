package com.alzen.skpku

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.alzen.skpku.ui.screens.FormSkpScreen
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream

@AndroidEntryPoint
class FormSkpActivity : ComponentActivity() {

    private val viewModel: FormSkpViewModel by viewModels()

    private var selectedFileUri by mutableStateOf<Uri?>(null)
    private var selectedFileName by mutableStateOf("")
    private var selectedMimeType by mutableStateOf("")
    private var fileBytes by mutableStateOf<ByteArray?>(null)

    private var isEditMode = false
    private var editSkp: Skp? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mode = intent.getStringExtra("mode") ?: "create"
        isEditMode = mode == "edit"
        editSkp = intent.getSerializableExtra("skp") as? Skp

        setContent {
            FormSkpScreen(
                mode = mode,
                editSkp = editSkp,
                viewModel = viewModel,
                onBack = { finish() },
                onPickFile = { openFilePicker() },
                selectedFileUri = selectedFileUri,
                selectedFileName = selectedFileName,
                selectedMimeType = selectedMimeType,
                fileBytes = fileBytes
            )
        }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png", "application/pdf"))
        }
        startActivityForResult(intent, 101)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            data.data?.let { uri ->
                val fileName = getFileName(uri)
                val mimeType = contentResolver.getType(uri) ?: getMimeTypeFromFileName(fileName)
                
                if (mimeType !in listOf("image/jpeg", "image/png", "application/pdf")) {
                    Toast.makeText(this, "Format file harus JPG, PNG, atau PDF", Toast.LENGTH_LONG).show()
                    return
                }
                
                val size = getFileSize(uri)
                if (size > 5 * 1024 * 1024) {
                    Toast.makeText(this, "Ukuran file maksimal 5 MB", Toast.LENGTH_LONG).show()
                    return
                }

                selectedFileUri = uri
                selectedFileName = fileName
                selectedMimeType = mimeType
                fileBytes = readBytesFromUri(uri)
            }
        }
    }

    private fun readBytesFromUri(uri: Uri): ByteArray? {
        return contentResolver.openInputStream(uri)?.use { inputStream ->
            val byteBuffer = ByteArrayOutputStream()
            val bufferSize = 1024
            val buffer = ByteArray(bufferSize)
            var len: Int
            while (inputStream.read(buffer).also { len = it } != -1) {
                byteBuffer.write(buffer, 0, len)
            }
            byteBuffer.toByteArray()
        }
    }

    private fun getFileName(uri: Uri): String {
        var result = "bukti_skp"
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1 && cursor.moveToFirst()) {
                result = cursor.getString(nameIndex)
            }
        }
        return result
    }

    private fun getFileSize(uri: Uri): Long {
        var size = 0L
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (sizeIndex != -1 && cursor.moveToFirst()) {
                size = cursor.getLong(sizeIndex)
            }
        }
        return size
    }

    private fun getMimeTypeFromFileName(fileName: String): String {
        val extension = fileName.substringAfterLast('.', "").lowercase()
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: ""
    }
}
