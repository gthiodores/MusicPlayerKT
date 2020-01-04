package com.example.android.musicplayerkt.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

import com.example.android.musicplayerkt.R
import com.example.android.musicplayerkt.adapter.SongAdapter
import com.example.android.musicplayerkt.adapter.SongClickListener
import com.example.android.musicplayerkt.databinding.FragmentLocalBinding
import com.example.android.musicplayerkt.viewmodel.LocalViewModel
import com.example.android.musicplayerkt.viewmodel.LocalViewModelFactory

class LocalFragment : Fragment() {

    companion object {
        const val NEW_MUSIC_REQUEST_CODE = 1
    }

    private lateinit var viewModel: LocalViewModel
    private lateinit var binding: FragmentLocalBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_local, container, false
        )
        binding.lifecycleOwner = this

        val application = requireNotNull(activity).application

        val viewModelFactory = LocalViewModelFactory(application)

        viewModel = activity?.run {
            ViewModelProviders.of(this, viewModelFactory)[LocalViewModel::class.java]
        } ?: throw Exception("Invalid Activity")
        binding.viewModel = viewModel

        val adapter =
            SongAdapter(
                SongClickListener (
                    { item -> viewModel.onSongSelected(item) },
                    { item -> viewModel.onFavouriteClicked(item)}
                )
            )
        binding.localSongRecycler.adapter = adapter

        val touchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                viewModel.removeSong(position)
                Toast.makeText(context, "Item removed", Toast.LENGTH_SHORT).show()
            }
        }

        val itemTouchHelper = ItemTouchHelper(touchCallback)
        itemTouchHelper.attachToRecyclerView(binding.localSongRecycler)

        viewModel.allSongs.observe(this, Observer {
            it?.let { list ->
                if (viewModel.currentPlaylist.value == null)
                    viewModel.testPlaylistChanged(list)
            }
        })

        viewModel.playlistSong.observe(this, Observer {
            it?.let { list ->
                adapter.submitList(list)
            }
        })

        viewModel.currentPlaylist.observe(this, Observer {
            when (it) {
                null -> viewModel.testPlaylistChanged(viewModel.allSongs.value ?: emptyList())
                else -> {
                    viewModel.onPlaylistChanged()
                }
            }
        })

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.overflow_menu_local, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.addSongMenu -> {
                addSongClicked()
                true
            }
            else -> {
                NavigationUI.onNavDestinationSelected(
                    item, this.view!!.findNavController()
                ) || super.onOptionsItemSelected(item)
            }
        }
    }

    private fun addSongClicked() {
        when (viewModel.currentPlaylist.value) {
            null -> {
                val intent: Intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply { type = "audio/*" }
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
                startActivityForResult(
                    Intent.createChooser(intent, "Complete Action Using"), NEW_MUSIC_REQUEST_CODE
                )
            }
            else -> {
                val navController = requireNotNull(activity).findNavController(R.id.nav_host)
                navController.navigate(R.id.action_localFragment_to_addSongsFragment)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == NEW_MUSIC_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.let {
                val audioUri = it.data
                audioUri?.let { uri ->
                    when (viewModel.allSongs.value?.any {item -> item.uri.equals(uri.toString()) } ?: false) {
                        true -> Toast.makeText(context, "Song already exists", Toast.LENGTH_SHORT).show()
                        else -> {
                            val flag =
                                it.flags and (Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            activity!!.contentResolver.takePersistableUriPermission(uri, flag)
                            viewModel.insertSong(uri)
                        }
                    }
                }
            }
        }
    }

}
