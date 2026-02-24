package com.ghosttech.blackout.api;

import android.util.Log;

import com.ghosttech.blackout.engine.BlackoutEngine;
import com.ghosttech.blackout.engine.EngineState;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * ApiBridge connects the Android BlackoutEngine to
 * the GhostTech API backend.
 * It signs payloads inside the engine boundary,
 * sends them to the API,
 * validates responses, and enforces lifecycle rules.
 */
public class ApiBridge {

    private static final String TAG = "BlackoutApiBridge";
    private static final MediaType JSON =
            MediaType.get("application/json; charset=utf-8");

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
     * @param payload Raw data to sign and send.
     * @return API response as string, or null on failure.
     */
    public String send(String endpoint, byte[] payload) {
        // Enforce lifecycle: engine must be ACTIVE
        EngineState state = engine.getState();
        if (state != EngineState.ACTIVE) {
            Log.w(TAG, "send() blocked: engine not ACTIVE. State=" + state);
            return null;
        }

        try {
            // 1) Sign inside engine boundary
            byte[] signed = engine.sign(payload);

            // 2) Build JSON envelope
            JSONObject body = new JSONObject();
            body.put("payload", bytesToBase64(payload));
            body.put("signature", bytesToBase64(signed));
            body.put("engine_state", state.name());

            // 3) POST to API
            String url = normalizeUrl(baseUrl, endpoint);
            String responseBody = postJson(url, body.toString());

            if (responseBody == null) {
                Log.e(TAG, "send() failed: null response from API");
                return null;
            }

            // 4) Validate response
            if (!isValidResponse(responseBody)) {
                Log.e(TAG, "send() failed: invalid API response");
                return null;
            }

            return responseBody;

        } catch (JSONException e) {
            Log.e(TAG, "send() JSON error", e);
            return null;
        } catch (Exception e) {
            Log.e(TAG, "send() unexpected error", e);
            return null;
        }
    }

    /**
     * Low-level JSON POST helper.
     */
    private String postJson(String url, String jsonBody) {
        RequestBody body = RequestBody.create(jsonBody, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                Log.e(TAG, "postJson() HTTP error: " + response.code());
                return null;
            }
            if (response.body() == null) {
                Log.e(TAG, "postJson() empty body");
                return null;
            }
            return response.body().string();
        } catch (IOException e) {
            Log.e(TAG, "postJson() IO error", e);
            return null;
        }
    }

    /**
     * Basic response validation hook.
     * You can tighten this to enforce GhostTech rules.
     */
    private boolean isValidResponse(String responseBody) {
        try {
            JSONObject obj = new JSONObject(responseBody);
            if (!obj.has("status")) return false;
            String status = obj.optString("status", "error");
            return "ok".equalsIgnoreCase(status);
        } catch (JSONException e) {
            Log.e(TAG, "isValidResponse() JSON parse error", e);
            return false;
        }
    }

    /**
     * Normalizes base URL + endpoint into a single URL.
     */
    private String normalizeUrl(String base, String endpoint) {
        if (base.endsWith("/") && endpoint.startsWith("/")) {
            return base + endpoint.substring(1);
        } else if (!base.endsWith("/") && !endpoint.startsWith("/")) {
            return base + "/" + endpoint;
        } else {
            return base + endpoint;
        }
    }

    /**
     * Simple Base64 encoder for payload/signature.
     *
    private String bytesToBase64(byte[] data) {
        if (data == null) return "";
        return android.util.Base64.encodeToString(
                data,
                android.util.Base64.NO_WRAP
        );
    }
}