package com.alzen.skpku

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.alzen.skpku.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels { ViewModelFactory(this) }
    private lateinit var adapter: SkpAdapter
    
    private var currentUserKey: String = ""
    private var selectedFilter: String = "Semua"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        adapter = SkpAdapter { skp ->
            val intent = Intent(this, DetailSkpActivity::class.java)
            intent.putExtra("skp", skp)
            startActivity(intent)
        }

        binding.rvSkp.layoutManager = LinearLayoutManager(this)
        binding.rvSkp.adapter = adapter

        setupFilterSpinner()
        setupClickActions()
    }

    private fun setupFilterSpinner() {
        val filterList = mutableListOf("Semua")
        filterList.addAll(SkpRule.getKategoriList())

        val filterAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, filterList)
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spFilterKategori.adapter = filterAdapter

        binding.spFilterKategori.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            @Override
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedFilter = filterList[position]
                applyFilter(viewModel.allSkpList.value)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupClickActions() {
        binding.navDashboard.setOnClickListener { showDashboardMenu() }
        binding.navSkp.setOnClickListener { showSkpMenu() }
        binding.btnTambahDashboard.setOnClickListener { openFormActivity() }
        binding.btnTambahSkp.setOnClickListener { openFormActivity() }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.userKey.collectLatest { key ->
                        if (key.isEmpty()) {
                            currentUserKey = viewModel.getOrCreateUserKey()
                        } else {
                            currentUserKey = key
                            viewModel.loadData(key)
                        }
                    }
                }
                
                launch {
                    viewModel.studentName.collectLatest { name ->
                        if (name.isEmpty()) {
                            showInputNameDialog()
                        } else {
                            binding.tvGreeting.text = "Halo, $name"
                        }
                    }
                }

                launch {
                    viewModel.allSkpList.collectLatest { list ->
                        updateDashboard(list)
                        applyFilter(list)
                    }
                }

                launch {
                    viewModel.isLoading.collectLatest { loading ->
                        // Show/hide progress if needed
                    }
                }

                launch {
                    viewModel.errorMessage.collect { message ->
                        Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun applyFilter(list: List<Skp>) {
        val filtered = if (selectedFilter == "Semua") {
            list
        } else {
            list.filter { it.kategoriBidang == selectedFilter }
        }
        
        adapter.setData(filtered)
        binding.tvEmpty.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
        binding.rvSkp.visibility = if (filtered.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun updateDashboard(list: List<Skp>) {
        val target = 200
        val totalPoin = list.sumOf { it.poinSkp }
        val totalWajib = list.count { it.jenisKegiatan.equals("Wajib", true) }
        val totalPilihan = list.size - totalWajib
        val sisa = (target - totalPoin).coerceAtLeast(0)

        binding.tvTotalPoin.text = "$totalPoin Poin"
        binding.tvTargetPoin.text = "Target: $target Poin"
        binding.tvSisaPoin.text = "Sisa: $sisa Poin"
        binding.tvRingkasan.text = "Total kegiatan: ${list.size}\nKegiatan wajib: $totalWajib\nKegiatan pilihan: $totalPilihan"

        val sudahAda = list.mapNotNull { it.namaKegiatan }.toSet()
        val belumLengkap = SkpRule.getWajibActivities().filter { it !in sudahAda }

        if (belumLengkap.isEmpty()) {
            binding.tvReminderWajib.text = "Semua SKP wajib sudah lengkap ✓"
            binding.tvReminderWajib.setTextColor(ContextCompat.getColor(this, R.color.accent_green))
        } else {
            binding.tvReminderWajib.text = "Masih ada SKP wajib yang belum lengkap.\nBelum: ${belumLengkap.joinToString(", ")}"
            binding.tvReminderWajib.setTextColor(ContextCompat.getColor(this, R.color.warning_orange))
        }
    }

    private fun showDashboardMenu() {
        binding.layoutDashboard.visibility = View.VISIBLE
        binding.layoutSkp.visibility = View.GONE
        binding.navDashboard.setTextColor(ContextCompat.getColor(this, R.color.primary_blue))
        binding.navSkp.setTextColor(ContextCompat.getColor(this, R.color.text_gray))
    }

    private fun showSkpMenu() {
        binding.layoutDashboard.visibility = View.GONE
        binding.layoutSkp.visibility = View.VISIBLE
        binding.navDashboard.setTextColor(ContextCompat.getColor(this, R.color.text_gray))
        binding.navSkp.setTextColor(ContextCompat.getColor(this, R.color.primary_blue))
    }

    private fun openFormActivity() {
        val intent = Intent(this, FormSkpActivity::class.java)
        intent.putExtra("mode", "create")
        intent.putExtra("user_key", currentUserKey)
        startActivity(intent)
    }

    private fun showInputNameDialog() {
        val inputNama = EditText(this).apply {
            hint = "Nama Mahasiswa"
            setSingleLine(true)
            setPadding(40, 20, 40, 20)
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Selamat Datang di SKPKU")
            .setMessage("Masukkan nama kamu terlebih dahulu.")
            .setView(inputNama)
            .setCancelable(false)
            .setPositiveButton("Mulai", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val nama = inputNama.text.toString().trim()
                if (nama.isEmpty()) {
                    inputNama.error = "Nama tidak boleh kosong"
                } else {
                    viewModel.saveStudentName(nama)
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        if (currentUserKey.isNotEmpty()) {
            viewModel.loadData(currentUserKey)
        }
    }
}
