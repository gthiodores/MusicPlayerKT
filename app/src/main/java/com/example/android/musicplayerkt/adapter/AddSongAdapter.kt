package com.example.android.musicplayerkt.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.musicplayerkt.database.LocalSong
import com.example.android.musicplayerkt.databinding.AddSongItemBinding

class AddSongAdapter(private val clickListener : AddSongClickListener)
    : ListAdapter<LocalSong, AddSongAdapter.ViewHolder>(AddSongDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }


    class ViewHolder private constructor(val binding : AddSongItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item : LocalSong, clickListener: AddSongClickListener) {
            binding.localSong = item
            binding.root.setOnClickListener {
                clickListener.onClick(item)
                binding.addCheck.isChecked = binding.addCheck.isChecked.not()
            }
            binding.executePendingBindings()
        }

        companion object {
            fun from (parent : ViewGroup) : ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = AddSongItemBinding.inflate(
                    inflater, parent, false
                )
                return ViewHolder(binding)
            }
        }

    }

}

class AddSongClickListener(val clickListener : (item : LocalSong) -> Unit) {
    fun onClick(item : LocalSong) = clickListener(item)
}

class AddSongDiffCallback : DiffUtil.ItemCallback<LocalSong>() {
    override fun areItemsTheSame(oldItem: LocalSong, newItem: LocalSong): Boolean {
        return oldItem.uri == newItem.uri
    }

    override fun areContentsTheSame(oldItem: LocalSong, newItem: LocalSong): Boolean {
        return oldItem == newItem
    }
}