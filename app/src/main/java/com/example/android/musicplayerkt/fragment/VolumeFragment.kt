package com.example.android.musicplayerkt.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders

import com.example.android.musicplayerkt.R
import com.example.android.musicplayerkt.databinding.FragmentVolumeBinding
import com.example.android.musicplayerkt.viewmodel.LocalViewModel
import com.example.android.musicplayerkt.viewmodel.LocalViewModelFactory

/**
 * A simple [Fragment] subclass.
 */
class VolumeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentVolumeBinding>(
            inflater, R.layout.fragment_volume, container, false
        )
        binding.lifecycleOwner = this

        val application = requireNotNull(activity).application

        val viewModelFactory = LocalViewModelFactory(application)

        val viewModel = activity?.run {
            ViewModelProviders.of(this, viewModelFactory)[LocalViewModel::class.java]
        } ?: throw Exception("Invalid Activity")
        binding.viewModel = viewModel

        binding.volumeSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.onVolumeControlPositionChanged(position = progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Do Nothing
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Do Nothing
            }

        })

        return binding.root
    }


}
