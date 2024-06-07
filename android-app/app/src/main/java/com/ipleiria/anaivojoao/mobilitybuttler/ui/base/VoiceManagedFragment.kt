package com.ipleiria.anaivojoao.mobilitybuttler.ui.base
import android.os.Bundle
import android.view.View
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
            VoiceCommandEntity.NEXT -> println( command.toString())
            VoiceCommandEntity.BACK -> println( command.toString())
            VoiceCommandEntity.SAY -> transcribe(command.params)
            else -> requireContext().toast( command.toString())
        }
    }
    open fun transcribe(params:String? = null ) {
        println("TRANSCRIBE");
        if(params != null && SayTriggers.TEMPERATURE.isIn(params)){
			println("Dispatching Temperature")

            var call = ApiClient.apiService.getLatestTemperature();
			/////Dispatch the get request and return a OpenM2Mresponse
			call.enqueue(object : Callback<M2MResponse> {
				override fun onResponse(call: Call<M2MResponse>, response: Response<M2MResponse>) {
                    println("Response!!")
                    val m2mResponse = response.body()
                    //genericLogicResourceDecoder()
					//requireContext().toast("Temperature: ${m2mResponse?.cin?.containerValue.toString()}")
                    var ret: String = ContentInfoEnum.genericLogicResourceDecoder(m2mResponse?.cin)
                    println(ret)
                    //find a way to access this
                    MainActivity.TTS.handleIncomingString(context, ret);

                }

                override fun onFailure(call: Call<M2MResponse>, t: Throwable) {
                    requireContext().toast("Err: "+t.message.toString())
                }


            })




        }
    }
    open fun exit() {
        requireActivity().finishAffinity()
        stopKoin()
    }


}