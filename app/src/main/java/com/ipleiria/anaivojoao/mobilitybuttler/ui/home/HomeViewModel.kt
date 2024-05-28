package com.ipleiria.anaivojoao.mobilitybuttler.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ipleiria.anaivojoao.mobilitybuttler.data.VoiceRecognitionRepository
import com.ipleiria.anaivojoao.mobilitybuttler.ui.base.VoiceManagedViewModel

class HomeViewModel(
    voiceRecognitionRepository: VoiceRecognitionRepository
): VoiceManagedViewModel(voiceRecognitionRepository)
