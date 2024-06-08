package com.ipleiria.anaivojoao.mobilitybuttler;

import android.content.Context;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class WebSocketManager {

    private static final String TAG = "WebSocketManager";
    private static final String SERVER_URL = "ws://10.20.140.120:8765";
    private final OkHttpClient client;

    protected Context context;
    private WebSocket webSocket;

    public WebSocketManager(Context context)
    {
        this.context = context;
        client = new OkHttpClient();
    }

    public void start() {
        Request request = new Request.Builder().url(SERVER_URL).build();
        webSocket = client.newWebSocket(request, new EchoWebSocketListener(this.context));
        client.dispatcher().executorService().shutdown();
    }

    public void stop() {
        if (webSocket != null) {
            webSocket.close(1000, "Goodbye!");
        }
    }

    private static final class EchoWebSocketListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;
        private final Context context;

        private boolean saidMessage;

        public EchoWebSocketListener(Context context) {
            this.context = context.getApplicationContext();
            this.saidMessage = false;
        }

        @Override
        public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
            Log.d(TAG, "Connected to server");
        }

        @Override
        public void onMessage(@NonNull WebSocket webSocket, @NonNull String jsonString) {
            if(!this.saidMessage) {
                Log.d(TAG, "Receiving : " + jsonString);
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    JSONObject sgn = jsonObject.getJSONObject("m2m:sgn");
                    JSONObject nev = sgn.getJSONObject("nev");
                    JSONObject rep = nev.getJSONObject("rep");
                    JSONObject cin = rep.getJSONObject("m2m:cin");
                    String con = cin.getString("con");

                    // The value of "con" is a JSON string itself, so parse it again
                    JSONObject conObject = new JSONObject(con);
                    String message = conObject.getString("message");

                    // Say message
                    Log.d("Message: ", message);
                    MainActivity.TTS.handleIncomingString(this.context, message);

                    saidMessage = true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                this.saidMessage = false;
            }
        }

        @Override
        public void onMessage(@NonNull WebSocket webSocket, ByteString bytes) {
            Log.d(TAG, "Receiving bytes : " + bytes.hex());
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, @NonNull String reason) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null);
            Log.d(TAG, "Closing : " + code + " / " + reason);
        }

        @Override
        public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, Response response) {
            Log.e(TAG, "Error : " + t.getMessage(), t);
        }
    }
}
