package com.ipleiria.anaivojoao.mobilitybuttler.data

import kotlinx.coroutines.flow.Flow
import com.ipleiria.anaivojoao.mobilitybuttler.data.entity.VoiceCommandEntity

interface VoiceRecognitionRepository {

    val commandsFlow: Flow<VoiceCommandEntity>
    fun startRecognition()
    fun resume()
    fun pause()
    fun release()
}