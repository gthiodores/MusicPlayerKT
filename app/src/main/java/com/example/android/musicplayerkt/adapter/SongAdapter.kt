package com.example.android.musicplayerkt.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.musicplayerkt.R
import com.example.android.musicplayerkt.database.LocalSong
import com.example.android.musicplayerkt.databinding.LocalSongItemBinding

/**
 * TODO: Make ViewHolder a lifecycle owner to add item highlight support
 * */
class SongAdapter(private val clickListener : SongClickListener) :
    ListAdapter <LocalSong, SongAdapter.ViewHolder>(SongDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class ViewHolder private constructor(val binding : LocalSongItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var favorite : Boolean = false

        fun bind(item : LocalSong, clickListener: SongClickListener) {
            binding.localSong = item
            binding.clickListener = clickListener
            favorite = item.favourite
            binding.itemSongFavourite.setOnClickListener {
                clickListener.onFavoutriteClick(item)
                favorite = !favorite
                if (favorite)
                    binding.itemSongFavourite.setImageResource(R.drawable.ic_favorite_16dp)
                else
                    binding.itemSongFavourite.setImageResource(R.drawable.ic_favorite_border_16)
            }
            binding.executePendingBindings()
        }

        companion object {
            fun from (parent : ViewGroup) : ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = LocalSongItemBinding.inflate(
                    inflater, parent, false
                )
                return ViewHolder(binding)
            }
        }

    }

}

class SongClickListener(val clickListener : (item : LocalSong) -> Unit,
                        val favouriteClick : (item : LocalSong) -> Unit) {
    fun onClick(item : LocalSong) = clickListener(item)
    fun onFavoutriteClick(item : LocalSong) = favouriteClick(item)
}

class SongDiffCallback : ItemCallback<LocalSong>() {
    override fun areItemsTheSame(oldItem: LocalSong, newItem: LocalSong): Boolean {
        return oldItem.uri == newItem.uri
    }

    override fun areContentsTheSame(oldItem: LocalSong, newItem: LocalSong): Boolean {
        return oldItem == newItem
    }
}