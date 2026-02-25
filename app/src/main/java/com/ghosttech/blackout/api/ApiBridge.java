package com.ghosttech.blackout.api;

import android.util.Log;
import com.ghosttech.blackout.engine.BlackoutEngine;
import com.ghosttech.blackout.engine.EngineState;
import org.json.JSONException;
import org.json.JSONObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
* ApiBridge connects the Android BlackoutEngine to
* the GhostTech-API backend.
* It signs payloads inside the engine boundary,
* sends them to the API,
* validates responses, and enforces lifecycle
* rules.
*/
public class ApiBridge {

private static final String TAG = "BlackoutApiBridge";
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
* @param payload Raw data to sign and send.
* @return API response as string, or null on failure.
*/
public String send(String endpoint, byte[] payload) {
try {
if (engine == null || !engine.isReady()) {
Log.e(TAG, "Engine not ready");
return null;
}

byte[] signed = engine.signPayload(payload);
String url = normalizeUrl(baseUrl, endpoint);
RequestBody body = RequestBody.create(signed, JSON);
Request request = new Request.Builder().url(url).post(body).build();
Response response = client.newCall(request).execute();

if (!response.isSuccessful()) {
Log.e(TAG, "API call failed: " + response.code());
return null;
}

String responseBody = response.body().string();
if (!isValidResponse(responseBody)) {
Log.e(TAG, "Invalid API response");
return null;
}

return responseBody;
} catch (Exception e) {
Log.e(TAG, "send() exception", e);
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
String status = status", "error");
return "ok".equalsIgnoreCase(status);
} catch (JSONException e) {
Log.e(TAG, "isValidResponse() parse error", e);
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
* Simple Base64 encoder for payload/signature
*/
private String bytesToBase64(byte[] data) {
if (data == null) return "";
return android.util.Base64.encodeToString(data, android.util.Base64.NO_WRAP);
}
}