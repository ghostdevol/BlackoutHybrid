package com.ghosttech.blackout;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.ghosttech.blackout.engine.BlackoutEngine;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.PrivateKey;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Blackout-Main";
    private BlackoutEngine engine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            // 1. Generate a temporary RSA keypair for testing
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KeyPair pair = generator.generateKeyPair();

            PublicKey publicKey = pair.getPublic();
            PrivateKey privateKey = pair.getPrivate();

            // 2. Initialize engine with public key
            engine = new BlackoutEngine(publicKey);

            // 3. Run UNLOCK → EXECUTE → ERASE
            engine.unlock(privateKey);

            byte[] input = "Blackout Test Payload".getBytes();
            byte[] output = engine.execute(input);

            Log.i(TAG, "Execution output (signature length): " + output.length);

            engine.erase();

            Log.i(TAG, "Engine state after teardown: " + engine.getState());

        } catch (Exception e) {
            Log.e(TAG, "Engine error", e);
        }
    }
}
