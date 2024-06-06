package com.ipleiria.anaivojoao.mobilitybuttler;

import android.content.Context;
import android.speech.tts.Voice;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class TextToSpeech {
    private android.speech.tts.TextToSpeech tts;
    private boolean isTtsInitialized = false;

    public TextToSpeech(Context context) {
        handleIncomingString(context,"Dear Sir, How are you?");
    }

    public void handleIncomingString(Context context, String text) {
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

    private void setLanguageAndVoice() {
        Locale desiredLocale = Locale.US;
        tts.setLanguage(desiredLocale);

        Set<Voice> voices = tts.getVoices();
        List<Voice> voiceList = new ArrayList<>(voices);
        Voice selectedVoice = voiceList.get(22);
        tts.setVoice(selectedVoice);
    }


    private void convertTextToSpeech(String text) {
        if (tts != null) {
            tts.speak(text, android.speech.tts.TextToSpeech.QUEUE_ADD, null, "UniqueID");
        }
    }
}
