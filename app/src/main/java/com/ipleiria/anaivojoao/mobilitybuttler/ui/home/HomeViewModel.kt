package com.ipleiria.anaivojoao.mobilitybuttler.ui.home

import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "I'm your virtual butler, \n Ready for requests"
    }
    val text: LiveData<String> = _text
}