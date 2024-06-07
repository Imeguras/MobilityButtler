package com.ipleiria.anaivojoao.mobilitybuttler.ui.base
import android.os.Bundle
import android.view.View
import androidx.compose.ui.text.toLowerCase
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ipleiria.anaivojoao.mobilitybuttler.ApiClient
import com.ipleiria.anaivojoao.mobilitybuttler.MainActivity
import com.ipleiria.anaivojoao.mobilitybuttler.TextToSpeech
import com.ipleiria.anaivojoao.mobilitybuttler.api.Cin
import com.ipleiria.anaivojoao.mobilitybuttler.api.ContentInfoEnum
import com.ipleiria.anaivojoao.mobilitybuttler.api.M2MResponse
import com.ipleiria.anaivojoao.mobilitybuttler.data.entity.SayTriggers
import kotlinx.coroutines.launch
import org.koin.core.context.stopKoin
import com.ipleiria.anaivojoao.mobilitybuttler.data.entity.VoiceCommandEntity
import com.ipleiria.anaivojoao.mobilitybuttler.data.entity.isIn
import com.ipleiria.anaivojoao.mobilitybuttler.ui.utils.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale


abstract class VoiceManagedFragment<T : VoiceManagedViewModel>(
    fragmentId: Int,
) : Fragment(fragmentId) {
    companion object {
        private const val TAG = "VoiceManagedFragment"
    }

    abstract val viewModel: T

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        commandSubscription()
    }

    private fun commandSubscription() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.commandState.collect {
                        //println( it.toString())
                        commandProcessing(it)
                    }
                }
            }
        }
    }

    open fun commandProcessing(command: VoiceCommandEntity) {
        when (command) {
            VoiceCommandEntity.EXIT -> exit()
            //VoiceCommandEntity.NEXT -> println( command.toString())
            //VoiceCommandEntity.BACK -> println( command.toString())
            VoiceCommandEntity.SAY -> transcribe(command.params)
            else -> println("")
            
        }
    }
    open fun transcribe(params:String? = null ) {
        
        if(params != null && SayTriggers.TEMPERATURE.isIn(params)){
			println("Dispatching Temperature")

            val call = ApiClient.apiService.getLatestTemperature();

			// Dispatch the get request for temperature and return a OpenM2MResponse
			call.enqueue(object : Callback<M2MResponse> {
				override fun onResponse(call: Call<M2MResponse>, response: Response<M2MResponse>) {
                    println("Temperature Response")

                    val m2mResponse = response.body()
                    val ret: String = ContentInfoEnum.genericLogicResourceDecoder(m2mResponse?.cin)
                
                    // Respond with the temperature
                    MainActivity.TTS.handleIncomingString(context, ret);
                }

                override fun onFailure(call: Call<M2MResponse>, t: Throwable) {
                    requireContext().toast("Err: "+t.message.toString())
                }
            })
        }else if(params != null && SayTriggers.MAIL.isIn(params)){
            println("Dispatching Mail")

            val call = ApiClient.apiService.checkMail();

            // Dispatch the get request for mailbox and return a OpenM2MResponse
            call.enqueue(object : Callback<M2MResponse> {
                override fun onResponse(call: Call<M2MResponse>, response: Response<M2MResponse>) {
                    println("Mailbox Response")

                    val m2mResponse = response.body()
                    var ret: String = ContentInfoEnum.genericLogicResourceDecoder(m2mResponse?.cin)
                    ret = when (ret.trim().lowercase()){
                        "true" -> "You have new mail in the mailbox"
                        "false" -> "No mails in the mailbox"
                        else -> "The Mailbox seems unresponsive"
                    }

                    // Respond with the message about mailbox
                    MainActivity.TTS.handleIncomingString(context, ret);

                }

                override fun onFailure(call: Call<M2MResponse>, t: Throwable) {
                    requireContext().toast("Err: "+t.message.toString())
                }
            })
        } else if(params != null && SayTriggers.KITCHEN.isIn(params)) {
            MainActivity.lastSaidWord = "kitchen"
            MainActivity.updateButlerPresence()
        } else if(params != null && SayTriggers.BEDROOM.isIn(params)) {
            MainActivity.lastSaidWord = "bedroom"
            MainActivity.updateButlerPresence()
        }
    }
    open fun exit() {
        requireActivity().finishAffinity()
        stopKoin()
    }


}