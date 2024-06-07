package com.ipleiria.anaivojoao.mobilitybuttler;

import android.content.Context;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;

import com.ipleiria.anaivojoao.mobilitybuttler.ui.home.HomeFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class TextToSpeech {
    private android.speech.tts.TextToSpeech tts;
    private boolean isTtsInitialized = false;
//    private ButlerGif butlerGif;

    public TextToSpeech(Context context, ButlerGif butlerGif) {
//        this.butlerGif = butlerGif;
    }

    public void handleIncomingString(Context context, String text) {
        if(MainActivity.ButlerGif.getPresent()) {
            if (isTtsInitialized) {
                convertTextToSpeech(text);
            } else {
                tts = new android.speech.tts.TextToSpeech(context, status -> {
                    if (status == android.speech.tts.TextToSpeech.SUCCESS) {
                        setLanguageAndVoice();
                        isTtsInitialized = true;
                        convertTextToSpeech(text);
                    }
                });
            }
        }
    }

    private void setLanguageAndVoice() {
        Locale desiredLocale = Locale.US;
        tts.setLanguage(desiredLocale);

        // TODO
        Set<Voice> voices = tts.getVoices();
        List<Voice> voiceList = new ArrayList<>(voices);
        Voice selectedVoice = voiceList.get(22);
        tts.setVoice(selectedVoice);
    }


    private void convertTextToSpeech(String text) {
        if (tts == null) {
            return;
        }

        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                MainActivity.ButlerGif.butlerSpeakGif();
            }

            @Override
            public void onDone(String utteranceId) {
                MainActivity.ButlerGif.butlerStopSpeakGif();
            }

            @Override
            public void onError(String utteranceId) {
                // Handle error if needed
            }
        });

        tts.speak(text, android.speech.tts.TextToSpeech.QUEUE_ADD, null, "UniqueID");
    }
}
