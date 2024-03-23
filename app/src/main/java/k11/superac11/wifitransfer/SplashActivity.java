package k11.superac11.wifitransfer;
import static android.widget.Toast.makeText;

import android.content.SharedPreferences;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class SplashActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 123;
    private static final int MANAGE_STORAGE_REQUEST_CODE = 456;

    private Button doneButton;
    private Button grantLocationButton;
    private Button grantStorageButton;
    private Button grantWifiButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        setContentView(R.layout.activity_permission_explanation);

        grantLocationButton = findViewById(R.id.btnGrantLocation);
        grantStorageButton = findViewById(R.id.btnGrantStorage);
        grantWifiButton = findViewById(R.id.btnGrantWifi);
        doneButton = findViewById(R.id.btnDone);

        // Disable the Done button initially
        doneButton.setEnabled(false);

        grantLocationButton.setOnClickListener(v -> checkAndRequestLocationPermission());
        grantStorageButton.setOnClickListener(v -> checkAndRequestStoragePermission());
        grantWifiButton.setOnClickListener(v -> checkAndRequestWifiPermission());

        doneButton.setOnClickListener(v -> startMainActivity());
    }

    private void checkAndRequestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            grantLocationButton.setEnabled(false);
            checkIfAllPermissionsAreGranted();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
    }





    private void checkAndRequestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            grantStorageButton.setEnabled(false);
            checkIfAllPermissionsAreGranted();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // If running on Android 10 (API level 29) or above, request MANAGE_EXTERNAL_STORAGE using an intent
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));

                if (intent.resolveActivity(getPackageManager()) != null) {
                    // The intent can be handled, so start it
                    try {
                        startActivityForResult(intent, MANAGE_STORAGE_REQUEST_CODE);
                    } catch (Exception e) {
                        // Handle the exception, e.g., log or show a message
                        e.printStackTrace();
                        // Fallback to the old permission dialog
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                    }
                } else {
                    // The intent cannot be handled, fallback to the old permission dialog
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                }
            } else {
                // On older devices, request WRITE_EXTERNAL_STORAGE
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MANAGE_STORAGE_REQUEST_CODE) {
            // Check if the permission was granted on Android 10 and above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (isStoragePermissionGranted()) {
                    grantStorageButton.setEnabled(false);
                    checkIfAllPermissionsAreGranted();
                } else {
                    // Permission not granted
                    // Handle accordingly
                }
            }
        }
    }

    // Check if storage permission is granted on Android 10 and above
    private boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }




    private void checkAndRequestWifiPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED) {
            grantWifiButton.setEnabled(false);
            checkIfAllPermissionsAreGranted();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_WIFI_STATE}, PERMISSION_REQUEST_CODE);
        }
    }

    private void checkIfAllPermissionsAreGranted() {
        if (!grantLocationButton.isEnabled() && !grantStorageButton.isEnabled() && !grantWifiButton.isEnabled()) {
            doneButton.setEnabled(true);
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int i = 0; i < permissions.length; i++) {
            if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION) || permissions[i].equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    grantLocationButton.setEnabled(false);
                }
            } else if (permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) || permissions[i].equals(Manifest.permission.MANAGE_EXTERNAL_STORAGE)) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    grantStorageButton.setEnabled(false);
                }
            } else if (permissions[i].equals(Manifest.permission.ACCESS_WIFI_STATE)) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    grantWifiButton.setEnabled(false);
                }
            }
        }

        checkIfAllPermissionsAreGranted();
    }
}