package com.ipleiria.anaivojoao.mobilitybuttler.ui.base
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import com.ipleiria.anaivojoao.mobilitybuttler.data.VoiceRecognitionRepository
import com.ipleiria.anaivojoao.mobilitybuttler.data.entity.VoiceCommandEntity


abstract class VoiceManagedViewModel(
    private val voiceRecognizer: VoiceRecognitionRepository,
) : ViewModel() {
    val commandState: Flow<VoiceCommandEntity> = voiceRecognizer.commandsFlow

    fun startRecognition() {
        voiceRecognizer.startRecognition()
    }

    protected fun release() {
        voiceRecognizer.release()
    }
}