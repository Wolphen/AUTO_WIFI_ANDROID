package com.logolauncher.test;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.logolauncher.test.R;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "WiFiManager";
    private static final String DEFAULT_SSID = "ONsim,XV2";
    private static final String DEFAULT_PASSWORD = "onsim121";
    private static final int PERMISSION_REQUEST_CODE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        } else {
            connectToConfiguredWifi();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission accordée, continuer
                connectToConfiguredWifi();
            } else {
                // Permission refusée
                Toast.makeText(this, "Permission d'accès au stockage refusée.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


    private void connectToConfiguredWifi() {
        String ssid = DEFAULT_SSID;
        String password = DEFAULT_PASSWORD;

        TextView statusTextView = findViewById(R.id.tv_status);
        statusTextView.setText("Tentative de connexion...");

        try {
            File file = new File("/sdcard/wifi_config.json");

            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                byte[] data = new byte[(int) file.length()];
                fis.read(data);
                fis.close();

                String jsonString = new String(data, "UTF-8");
                JSONObject jsonObject = new JSONObject(jsonString);

                ssid = jsonObject.getString("ssid");
                password = jsonObject.getString("password");

                Log.d(TAG, "SSID lu depuis le fichier : " + ssid);
                Log.d(TAG, "Password lu depuis le fichier : " + password);

                // Mise à jour du TextView avec le SSID
                statusTextView.setText("Connexion à : " + ssid);
            } else {
                Log.w(TAG, "Fichier de configuration introuvable. Utilisation des valeurs par défaut.");
                statusTextView.setText("Fichier de configuration introuvable. Connexion avec les valeurs par défaut.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de la lecture du fichier de configuration.", e);
            statusTextView.setText("Erreur de lecture du fichier de configuration.");
        }

        connectToWifi(ssid, password, statusTextView);
    }

    private void connectToWifi(String ssid, String password, TextView statusTextView) {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (wifiManager == null) {
            Toast.makeText(this, "Erreur : Impossible d'accéder au Wi-Fi Manager.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "WiFiManager est null.");
            statusTextView.setText("Erreur : Wi-Fi Manager inaccessible.");
            finish();
            return;
        }

        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
            Log.d(TAG, "Wi-Fi activé automatiquement.");
            statusTextView.setText("Activation du Wi-Fi...");
        }

        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = "\"" + ssid + "\"";
        wifiConfig.preSharedKey = "\"" + password + "\"";

        int netId = wifiManager.addNetwork(wifiConfig);
        if (netId != -1) {
            wifiManager.disconnect();
            wifiManager.enableNetwork(netId, true);
            wifiManager.reconnect();
            Toast.makeText(this, "Connexion réussie au Wi-Fi : " + ssid, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Connexion réussie au réseau : " + ssid);
            statusTextView.setText("Connecté à : " + ssid);
        } else {
            Toast.makeText(this, "Échec de la connexion au Wi-Fi.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Impossible de connecter au réseau : " + ssid);
            statusTextView.setText("Échec de la connexion au réseau : " + ssid);
        }

        finish();
    }
}
