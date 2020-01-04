package com.example.android.musicplayerkt.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.android.musicplayerkt.R
import com.example.android.musicplayerkt.adapter.PlaylistAdapter
import com.example.android.musicplayerkt.adapter.PlaylistAdapterClick

import com.example.android.musicplayerkt.databinding.FragmentSelectPlaylistDialogBinding
import com.example.android.musicplayerkt.viewmodel.LocalViewModel
import com.example.android.musicplayerkt.viewmodel.LocalViewModelFactory

class SelectPlaylistFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSelectPlaylistDialogBinding.inflate(
            inflater, container, false
        )
        binding.lifecycleOwner = this

        val navController = requireNotNull(activity).findNavController(R.id.nav_host)

        val application = requireNotNull(activity).application

        val factory = LocalViewModelFactory(application)

        val viewModel = activity?.run {
            ViewModelProviders.of(this, factory)[LocalViewModel::class.java]
        } ?: throw Exception("Invalid Activity")
        binding.viewModel = viewModel

        val adapter = PlaylistAdapter(PlaylistAdapterClick { playlistId ->
//            if (viewModel.isPlaying.value ?: false)
//                viewModel.stopPlayback()
            viewModel.onPlaylistSelected(playlistId)
            navController.popBackStack()
        })
        binding.selectPlaylistRecycler.adapter = adapter

        val touchCallback = object : ItemTouchHelper.SimpleCallback(0,  ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                viewModel.onPlaylistSwipe(position)
                Toast.makeText(context, "Playlist removed", Toast.LENGTH_SHORT).show()
            }
        }

        val itemTouchHelper = ItemTouchHelper(touchCallback)
        itemTouchHelper.attachToRecyclerView(binding.selectPlaylistRecycler)

        viewModel.allPlaylist.observe(this, Observer { playlists ->
            playlists?.let { list ->
                adapter.submitList(list)
            }
        })

        binding.selectAllMusic.setOnClickListener {
//            when (viewModel.currentPlaylist.value) {
//                null -> navController.popBackStack()
//                else -> {
//                    if (viewModel.isPlaying.value ?: false)
//                        viewModel.stopPlayback()
//                    viewModel.onPlaylistSelected(-1L)
//                    navController.popBackStack()
//                }
//            }
            viewModel.onPlaylistSelected(-1L)
            navController.popBackStack()
        }

        binding.selectPlaylistNew.setOnClickListener {
            navController.navigate(R.id.action_selectPlaylistFragment_to_newPlaylistDialogFragment)
        }

        return binding.root
    }

}
