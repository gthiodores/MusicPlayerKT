package com.example.android.musicplayerkt.fragment


import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.example.android.musicplayerkt.database.LocalPlaylist

import com.example.android.musicplayerkt.databinding.FragmentNewPlaylistDialogBinding
import com.example.android.musicplayerkt.viewmodel.LocalViewModel
import com.example.android.musicplayerkt.viewmodel.LocalViewModelFactory
import java.lang.IllegalStateException

class NewPlaylistDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val inflater = requireActivity().layoutInflater

            val binding = FragmentNewPlaylistDialogBinding.inflate(inflater)
            binding.lifecycleOwner = this

            val viewModelFactory = LocalViewModelFactory(it.application)

            val viewModel = ViewModelProviders.of(this, viewModelFactory)[LocalViewModel::class.java]

            binding.dialogConfirmButton.setOnClickListener {
                val playlistName = binding.dialogInputPlaylist.text.toString()
                when (playlistName.trim()) {
                    "" -> Toast.makeText(context, "Playlist name cannot be empty!", Toast.LENGTH_SHORT).show()
                    else -> {
                        viewModel.insertPlaylist(LocalPlaylist(name = playlistName))
                        dismiss()
                    }
                }
            }

            val builder = AlertDialog.Builder(it)
            builder.setView(binding.root)
            builder.create()
        } ?: throw IllegalStateException("Activity Cannot Be Null")
    }

}
