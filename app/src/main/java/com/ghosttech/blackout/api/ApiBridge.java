package com.ghosttech.blackout.api;

import android.util.Log;

import com.ghosttech.blackout.engine.BlackoutEngine;
import com.ghosttech.blackout.engine.EngineState;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * ApiBridge connects the Android BlackoutEngine to the GhostTech-API backend.
 * It signs payloads inside the engine boundary, sends them to the API,
 * validates responses, and enforces lifecycle rules.
 */
public class ApiBridge {

    private static final String TAG = "Blackout-ApiBridge";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client;
    private final String baseUrl;
    private final BlackoutEngine engine;

    public ApiBridge(String baseUrl, BlackoutEngine engine) {
        this.client = new OkHttpClient();
        this.baseUrl = baseUrl;
        this.engine = engine;
    }

    /**
     * Sends a signed payload to the GhostTech API.
     *
     * @param endpoint API route (e.g., "/command")
     * @param payload  Raw data to sign and send.
     * @return API response as string, or null on failure.
     */
    public String send(String endpoint, byte[] payload) {
        try {
            // Engine must be ACTIVE before sending
            if (engine.getState() != EngineState.ACTIVE) {
                Log.e(TAG, "Engine not ACTIVE — cannot send payload");
                return null;
            }

            // Sign payload inside engine boundary
            byte[] signature = engine.execute(payload);

            // Build JSON body
            JSONObject json = new JSONObject();
            json.put("payload", new String(payload));
            json.put("signature", bytesToHex(signature));
            json.put("timestamp", System.currentTimeMillis());
            json.put("device", engine.getDeviceId());

            RequestBody body = RequestBody.create(json.toString(), JSON);

            // Build request
            Request request = new Request.Builder()
                    .url(baseUrl + endpoint)
                    .post(body)
                    .build();

            // Execute request
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "API error: " + response.code());
                    return null;
                }

                String responseBody = response.body().string();
                Log.i(TAG, "API Response: " + responseBody);

                return responseBody;
            }

        } catch (Exception e) {
            Log.e(TAG, "API send error", e);
            return null;
        }
    }

    /**
     * Converts bytes to hex string.
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}
