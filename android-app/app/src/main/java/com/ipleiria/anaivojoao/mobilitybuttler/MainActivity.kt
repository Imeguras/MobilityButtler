package com.ipleiria.anaivojoao.mobilitybuttler

import android.content.Context
import android.os.Bundle
import android.text.style.UpdateAppearance
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.ipleiria.anaivojoao.mobilitybuttler.data.dataModule
import com.ipleiria.anaivojoao.mobilitybuttler.databinding.ActivityMainBinding
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    companion object {
        lateinit var TTS: TextToSpeech
        lateinit var ButlerGif: ButlerGif

        var location: String? = "kitchen"
        var lastSaidWord: String? = "kitchen"

        fun updateButlerPresence(){
            if (location == lastSaidWord){
                // Appear in the UI
                ButlerGif.butlerStopSpeakGif()
            }
            else
            {
                // Disappear from the UI
                ButlerGif.butlerDisappear()
            }
        }
    }

    private var webSocketManager: WebSocketManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@MainActivity)
            modules(
                dataModule,
                uiModule
            )
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)

        // Buttons kitchen and bedroom
        binding.appBarMain.kitchen.setOnClickListener { view ->
            location = "kitchen"
            updateButlerPresence()
            Snackbar.make(view, "Set to kitchen", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        binding.appBarMain.bedroom.setOnClickListener { view ->
            location = "bedroom"
            updateButlerPresence()
            Snackbar.make(view, "Set to bedroom", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        // Initialize ButlerGif
        ButlerGif = ButlerGif(this)
        ButlerGif.butlerStopSpeakGif()

        // Start TextToSpeech TTS
        TTS = TextToSpeech();
        speakOnStartUp(this)

        // Initialize websockets
        webSocketManager = WebSocketManager(this)
        webSocketManager!!.start()
    }

    private fun speakOnStartUp(context: Context){
        TTS.handleIncomingString(context, "Dear Sir, How are you?")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (webSocketManager != null) {
            webSocketManager!!.stop()
        }
    }
}