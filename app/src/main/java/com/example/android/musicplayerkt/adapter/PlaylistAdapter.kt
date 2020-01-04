package com.example.android.musicplayerkt.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.musicplayerkt.database.LocalPlaylist
import com.example.android.musicplayerkt.databinding.LocalPlaylistItemBinding

class PlaylistAdapter(private val clickListener : PlaylistAdapterClick)
    : ListAdapter<LocalPlaylist, PlaylistAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class ViewHolder private constructor(private val binding : LocalPlaylistItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item : LocalPlaylist, clickListener: PlaylistAdapterClick) {
            binding.localPlaylist = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent : ViewGroup) : ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = LocalPlaylistItemBinding.inflate(
                    inflater, parent, false
                )
                return ViewHolder(binding)
            }
        }
    }

}

class PlaylistAdapterClick(val itemClick : (playlistId : Long) -> Unit ) {
    fun onClick(item : LocalPlaylist) = itemClick(item.id)
}

class DiffCallback : ItemCallback<LocalPlaylist>() {
    override fun areItemsTheSame(oldItem: LocalPlaylist, newItem: LocalPlaylist): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: LocalPlaylist, newItem: LocalPlaylist): Boolean {
        return oldItem == newItem
    }
}