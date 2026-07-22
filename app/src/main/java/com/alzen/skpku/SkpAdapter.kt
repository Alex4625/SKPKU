package com.alzen.skpku

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alzen.skpku.databinding.ItemSkpBinding

class SkpAdapter(
    private var skpList: List<Skp> = emptyList(),
    private val onItemClick: (Skp) -> Unit
) : RecyclerView.Adapter<SkpAdapter.SkpViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkpViewHolder {
        val binding = ItemSkpBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SkpViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SkpViewHolder, position: Int) {
        holder.bind(skpList[position])
    }

    override fun getItemCount(): Int = skpList.size

    fun setData(newList: List<Skp>) {
        this.skpList = newList
        notifyDataSetChanged()
    }

    inner class SkpViewHolder(private val binding: ItemSkpBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(skp: Skp) {
            binding.apply {
                tvItemNama.text = skp.namaKegiatan
                tvItemKategori.text = skp.kategoriBidang
                tvItemTanggal.text = skp.tanggalInput
                tvItemPoin.text = "${skp.poinSkp} Poin"
                
                root.setOnClickListener { onItemClick(skp) }
            }
        }
    }
}
