package com.example.android.musicplayerkt.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.example.android.musicplayerkt.R
import com.example.android.musicplayerkt.adapter.AddSongAdapter
import com.example.android.musicplayerkt.adapter.AddSongClickListener
import com.example.android.musicplayerkt.database.LocalSong

import com.example.android.musicplayerkt.databinding.FragmentAddSongsBinding
import com.example.android.musicplayerkt.viewmodel.AddSongViewModel
import com.example.android.musicplayerkt.viewmodel.AddSongViewModelFactory
import com.example.android.musicplayerkt.viewmodel.LocalViewModel
import com.example.android.musicplayerkt.viewmodel.LocalViewModelFactory

class AddSongsFragment : Fragment() {

    private val clickedItems = ArrayList<LocalSong>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAddSongsBinding.inflate(inflater)
        binding.lifecycleOwner = this

        val application = requireNotNull(activity).application

        val localViewModelFactory = LocalViewModelFactory(application)
        val addSongViewModelFactory = AddSongViewModelFactory(application)

        val localViewModel = activity?.run {
            ViewModelProviders.of(this, localViewModelFactory)[LocalViewModel::class.java]
        } ?: throw Exception("Invalid Activity")
        val addSongViewModel =
            ViewModelProviders.of(this, addSongViewModelFactory)[AddSongViewModel::class.java]

        val adapter = AddSongAdapter(AddSongClickListener {item ->
            when(clickedItems.any { it.id == item.id }) {
                true -> clickedItems.remove(item)
                else -> clickedItems.add(item)
            }
        })
        binding.addRecycler.adapter = adapter

        localViewModel.currentPlaylist.observe(this, Observer {
            it?.let { playlist ->
                addSongViewModel.getSongsToAdd(playlist.id)
            }
        })

        addSongViewModel.songsToAdd.observe(this, Observer {
            it?.let {array ->
                adapter.submitList(array.toList())
            }
        })

        addSongViewModel.addSongsCompleted.observe(this, Observer {
            it?.let {completed ->
                if (completed) {
                    requireNotNull(activity).findNavController(R.id.nav_host).popBackStack()
                    addSongViewModel.onDoneNavigating()
                }
            }
        })

        binding.addConfirmButton.setOnClickListener {
            when (clickedItems.any()) {
                true -> addSongViewModel.onConfirmButtonClick(localViewModel.currentPlaylist.value!!.id, clickedItems)
                false -> Toast.makeText(context, "You haven't selected anything", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

}
