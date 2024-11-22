package com.logolauncher.test;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.logolauncher.R;
import androidx.appcompat.app.AppCompatActivity;



public class MainActivity extends AppCompatActivity {

    private static final String TAG = "WiFiManager";

    // Configurer ici votre réseau Wi-Fi par défaut
    private static final String DEFAULT_SSID = "SSID_NAME";
    private static final String DEFAULT_PASSWORD = "SSID_PASSWORD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectToDefaultWifi();
    }


    private void connectToDefaultWifi() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (wifiManager == null) {
            Toast.makeText(this, "Erreur : Impossible d'accéder au Wi-Fi Manager.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "WiFiManager est null.");
            finish(); // Fermer l'application en cas d'erreur critique
            return;
        }

        // Activer le Wi-Fi si désactivé
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
            Log.d(TAG, "Wi-Fi activé automatiquement.");
        }

        // Configurer le réseau Wi-Fi
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = "\"" + DEFAULT_SSID + "\"";
        wifiConfig.preSharedKey = "\"" + DEFAULT_PASSWORD + "\"";

        // Ajouter et activer le réseau
        int netId = wifiManager.addNetwork(wifiConfig);
        if (netId != -1) {
            wifiManager.disconnect();
            wifiManager.enableNetwork(netId, true);
            wifiManager.reconnect();
            Toast.makeText(this, "Connexion réussie au Wi-Fi : " + DEFAULT_SSID, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Connexion réussie au réseau : " + DEFAULT_SSID);
        } else {
            Toast.makeText(this, "Échec de la connexion au Wi-Fi.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Impossible de connecter au réseau : " + DEFAULT_SSID);
        }

        finish();
    }
}
