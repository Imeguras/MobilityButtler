package com.ipleiria.anaivojoao.mobilitybuttler;

import android.content.Context;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;

import java.util.Locale;
import java.util.Set;

public class TextToSpeech {
    private android.speech.tts.TextToSpeech tts;
    private boolean isTtsInitialized = false;
    private final String desiredVoiceName = "en-gb-x-gbb-network";

    public TextToSpeech() {}

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
        Locale desiredLocale = Locale.UK;
        tts.setLanguage(desiredLocale);

        Set<Voice> voices = tts.getVoices();
        Voice selectedVoice = null;
        for (Voice voice : voices) {
            if (voice.getName().equals(desiredVoiceName)) {
                selectedVoice = voice;
                break;
            }
        }

        if (selectedVoice != null) {
            tts.setVoice(selectedVoice);
        } else {
            // Handle the case where no US voice was found
            System.out.println("The exact US voice was not found.");
        }
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
            }
        });

        tts.speak(text, android.speech.tts.TextToSpeech.QUEUE_ADD, null, "UniqueID");
    }
}
