package com.ipleiria.anaivojoao.mobilitybuttler

import android.content.Context
import android.os.Bundle
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
    }
    private lateinit var butlerGif: ButlerGif

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

        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        butlerGif = ButlerGif(this)
        butlerGif.butlerStopSpeakGif()
        // Start TextToSpeech TTS
        TTS = TextToSpeech(this,butlerGif);
        speakOnStartUp(this)
    }

    private fun speakOnStartUp(context: Context){
        TTS.handleIncomingString(context, "Dear Sir, How are you?")
    }

    private fun speak(context: Context, tts: TextToSpeech, phrase: String) {
        tts.handleIncomingString(context,phrase)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}