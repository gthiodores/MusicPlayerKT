package com.example.android.musicplayerkt.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.example.android.musicplayerkt.R
import com.example.android.musicplayerkt.adapter.SongAdapter
import com.example.android.musicplayerkt.adapter.SongClickListener
import com.example.android.musicplayerkt.database.LocalSong
import com.example.android.musicplayerkt.databinding.FragmentFavoritesBinding
import com.example.android.musicplayerkt.viewmodel.FavoritesViewModel
import com.example.android.musicplayerkt.viewmodel.LocalViewModel
import com.example.android.musicplayerkt.viewmodel.LocalViewModelFactory

class FavoritesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentFavoritesBinding>(
            inflater, R.layout.fragment_favorites, container, false
        )
        binding.lifecycleOwner = this

        val application = requireNotNull(activity).application

        val viewModelFactory = LocalViewModelFactory(application)

        val localViewModel = activity?.run {
            ViewModelProviders.of(this, viewModelFactory)[LocalViewModel::class.java]
        } ?: throw Exception("Invalid Activity")
        binding.viewModel = localViewModel

        val favoritesViewModel =
            ViewModelProviders.of(this)[FavoritesViewModel::class.java]

        val adapter =
            SongAdapter(
                SongClickListener(
                    { item -> localViewModel.onSongSelected(item) },
                    { item ->
                        localViewModel.onFavouriteClicked(item)
                        if (favoritesViewModel.updateFavorite(item))
                            Toast.makeText(
                                context,
                                "Item removed from favorites",
                                Toast.LENGTH_SHORT
                            ).show()
                        else
                            Toast.makeText(
                                context,
                                "Failed to remove item from favorites",
                                Toast.LENGTH_SHORT
                            ).show()
                    }
                )
            )
        binding.favoriteSongRecycler.adapter = adapter

        localViewModel.allSongs.observe(this, Observer { list ->
            list?.let {
                val favoriteList = mutableListOf<LocalSong>()
                it.forEach { item ->
                    if (item.favourite) favoriteList.add(item)
                }
                favoritesViewModel.refreshList(favoriteList.toList())
            }
        })

        favoritesViewModel.favoritesPlaylist.observe(this, Observer {
            adapter.submitList(it.toList())
            localViewModel.testPlaylistChanged(it.toList())
        })

        return binding.root
    }


}
