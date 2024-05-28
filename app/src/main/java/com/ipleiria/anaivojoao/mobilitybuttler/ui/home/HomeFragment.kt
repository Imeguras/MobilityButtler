package com.ipleiria.anaivojoao.mobilitybuttler.ui.home

import android.Manifest.permission.RECORD_AUDIO
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.ipleiria.anaivojoao.mobilitybuttler.R
import com.ipleiria.anaivojoao.mobilitybuttler.data.entity.VoiceCommandEntity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.ipleiria.anaivojoao.mobilitybuttler.R
import com.ipleiria.anaivojoao.mobilitybuttler.databinding.FragmentHomeBinding
import com.ipleiria.anaivojoao.mobilitybuttler.ui.base.VoiceManagedFragment
import com.ipleiria.anaivojoao.mobilitybuttler.ui.utils.checkAndRequestPermissions
import java.io.IOException

import com.ipleiria.anaivojoao.mobilitybuttler.ui.utils.viewBinding


class HomeFragment :
    VoiceManagedFragment<HomeViewModel>(R.layout.fragment_home) {
    companion object {
        private const val TAG = "HomeFragment"
    }

    private val binding by viewBinding { FragmentHomeBinding.bind(it) }
    override val viewModel: HomeViewModel by viewModel()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permission ->
        Log.d(TAG, "RECORD_AUDIO granted=${permission[RECORD_AUDIO]}")
        if (permission[RECORD_AUDIO] == true) viewModel.startRecognition()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkAndRequestPermissions(
            listOf(RECORD_AUDIO),
            requestPermissionLauncher,
            onGranted = { viewModel.startRecognition() }
        )
    }

    override fun commandProcessing(command: VoiceCommandEntity) {
        when (command) {

            /*VoiceCommandEntity.SETTINGS ->
            navigateTo(HomeScreenFragmentDirections.actionHomeScreenFragmentToSettingsScreenFragment())*/

            else -> super.commandProcessing(command)
        }
    }
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val gifImageView: ImageView = root.findViewById(R.id.gifImageView)

        // Load the GIF
        Glide.with(this).load("file:///android_asset/butler_speaking.gif").into(gifImageView)

        return root
    }


    /*override fun navigateNext() {
        navigateTo(HomeScreenFragmentDirections.actionHomeScreenFragmentToContentScreenFragment())
    }*/
}