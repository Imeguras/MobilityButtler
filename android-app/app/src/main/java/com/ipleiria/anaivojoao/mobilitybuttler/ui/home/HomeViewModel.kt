package com.ipleiria.anaivojoao.mobilitybuttler.ui.home

import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ipleiria.anaivojoao.mobilitybuttler.data.VoiceRecognitionRepository
import com.ipleiria.anaivojoao.mobilitybuttler.ui.base.VoiceManagedViewModel

class HomeViewModel(
    voiceRecognitionRepository: VoiceRecognitionRepository
): VoiceManagedViewModel(voiceRecognitionRepository) {

    private val _text = MutableLiveData<String>().apply {
        value = "I'm your virtual butler, \n Ready for requests"
    }
    val text: LiveData<String> = _text
}