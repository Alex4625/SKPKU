package com.alzen.skpku.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alzen.skpku.DetailSkpViewModel
import com.alzen.skpku.Skp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailSkpScreen(
    skp: Skp,
    viewModel: DetailSkpViewModel,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onViewProof: () -> Unit,
    onDownloadProof: () -> Unit
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState("")
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.deleteSuccess.collect {
            onBack()
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Data SKP") },
            text = { Text("Apakah kamu yakin ingin menghapus data ini? File bukti juga akan dihapus dari storage.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteSkp(skp)
                    showDeleteDialog = false
                }) {
                    Text("Hapus", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail SKP") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(skp.namaKegiatan ?: "-", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("${skp.poinSkp} Poin", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3F51B5))
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    
                    DetailRow("Kategori", skp.kategoriBidang)
                    DetailRow("Jenis", skp.jenisKegiatan)
                    DetailRow("Tingkat", skp.tingkat)
                    DetailRow("Peran", skp.peran)
                    DetailRow("Mode", skp.modeKegiatan)
                    DetailRow("Tanggal", skp.tanggalInput)
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Bukti Kegiatan", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(skp.fileName ?: "Tidak ada file bukti", color = Color.Gray)
                    
                    if (!skp.fileUrl.isNullOrBlank()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(onClick = onViewProof, modifier = Modifier.weight(1f)) {
                                Text("Lihat Bukti")
                            }
                            OutlinedButton(onClick = onDownloadProof, modifier = Modifier.weight(1f)) {
                                Text("Download")
                            }
                        }
                    }
                }
            }

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            if (errorMessage.isNotEmpty()) {
                Text(errorMessage, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String?) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text(value ?: "-", fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}
