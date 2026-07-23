package com.alzen.skpku.ui.screens

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alzen.skpku.FormSkpViewModel
import com.alzen.skpku.Skp
import com.alzen.skpku.SkpRule

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormSkpScreen(
    mode: String,
    editSkp: Skp?,
    viewModel: FormSkpViewModel,
    onBack: () -> Unit,
    onPickFile: () -> Unit,
    selectedFileUri: Uri?,
    selectedFileName: String,
    selectedMimeType: String,
    fileBytes: ByteArray?
) {
    val userId by viewModel.userId.collectAsState("")
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState("")

    var selectedKategori by remember { mutableStateOf(editSkp?.kategoriBidang ?: "") }
    var selectedKegiatan by remember { mutableStateOf(editSkp?.namaKegiatan ?: "") }
    var selectedTingkat by remember { mutableStateOf(editSkp?.tingkat ?: "") }
    var selectedPeran by remember { mutableStateOf(editSkp?.peran ?: "") }
    var selectedMode by remember { mutableStateOf(editSkp?.modeKegiatan ?: SkpRule.MODE_TIDAK_ADA) }

    val poin = SkpRule.calculatePoint(
        selectedKategori, selectedKegiatan, selectedTingkat, selectedPeran, selectedMode
    )

    LaunchedEffect(Unit) {
        viewModel.saveSuccess.collect {
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (mode == "edit") "Edit Data SKP" else "Tambah Data SKP") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Dropdown Menus
            ExposedDropdownMenu(
                label = "Kategori Bidang",
                options = SkpRule.getKategoriList(),
                selectedOption = selectedKategori,
                onOptionSelected = {
                    selectedKategori = it
                    selectedKegiatan = ""
                    selectedTingkat = ""
                    selectedPeran = ""
                    selectedMode = SkpRule.MODE_TIDAK_ADA
                }
            )

            ExposedDropdownMenu(
                label = "Nama Kegiatan",
                options = SkpRule.getKegiatanList(selectedKategori),
                selectedOption = selectedKegiatan,
                onOptionSelected = {
                    selectedKegiatan = it
                    selectedTingkat = ""
                    selectedPeran = ""
                    selectedMode = SkpRule.MODE_TIDAK_ADA
                }
            )

            ExposedDropdownMenu(
                label = "Tingkat",
                options = SkpRule.getTingkatList(selectedKategori, selectedKegiatan),
                selectedOption = selectedTingkat,
                onOptionSelected = {
                    selectedTingkat = it
                    selectedPeran = ""
                    selectedMode = SkpRule.MODE_TIDAK_ADA
                }
            )

            ExposedDropdownMenu(
                label = "Peran / Partisipasi",
                options = SkpRule.getPeranList(selectedKategori, selectedKegiatan, selectedTingkat),
                selectedOption = selectedPeran,
                onOptionSelected = {
                    selectedPeran = it
                    selectedMode = SkpRule.MODE_TIDAK_ADA
                }
            )

            val modeOptions = SkpRule.getModeList(selectedKategori, selectedKegiatan, selectedTingkat, selectedPeran)
            if (modeOptions.size > 1 || modeOptions.firstOrNull() != SkpRule.MODE_TIDAK_ADA) {
                ExposedDropdownMenu(
                    label = "Mode Kegiatan",
                    options = modeOptions,
                    selectedOption = selectedMode,
                    onOptionSelected = { selectedMode = it }
                )
            }

            // Poin Display
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Poin SKP Otomatis", fontSize = 14.sp, color = Color.Gray)
                    Text("$poin Poin", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3F51B5))
                }
            }

            // File Section
            Column {
                Text("Bukti Kegiatan", fontWeight = FontWeight.Bold)
                Button(
                    onClick = onPickFile,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Text("Pilih File Bukti")
                }
                Text(
                    text = if (selectedFileName.isNotEmpty()) "File dipilih: $selectedFileName" 
                           else if (editSkp?.fileName != null) "File saat ini: ${editSkp.fileName}"
                           else "Belum ada file dipilih",
                    modifier = Modifier.padding(top = 4.dp),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            Button(
                onClick = {
                    viewModel.saveData(
                        isEditMode = mode == "edit",
                        editSkp = editSkp,
                        userId = userId,
                        selectedKategori = selectedKategori,
                        selectedKegiatan = selectedKegiatan,
                        selectedTingkat = selectedTingkat,
                        selectedPeran = selectedPeran,
                        selectedMode = selectedMode,
                        selectedPoin = poin,
                        fileBytes = fileBytes,
                        fileName = selectedFileName,
                        mimeType = selectedMimeType,
                        existingData = mapOf(
                            "url" to (editSkp?.fileUrl ?: ""),
                            "name" to (editSkp?.fileName ?: ""),
                            "type" to (editSkp?.fileType ?: ""),
                            "path" to (editSkp?.storagePath ?: "")
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !isLoading && userId.isNotEmpty() && selectedKategori.isNotEmpty() && selectedKegiatan.isNotEmpty()
            ) {
                Text("Simpan Data", fontWeight = FontWeight.Bold)
            }

            if (errorMessage.isNotEmpty()) {
                Text(errorMessage, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownMenu(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
