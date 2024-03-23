package k11.superac11.wifitransfer;

import static android.app.Service.START_STICKY;
import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.IOException;



public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int PORT = 8080;
    private static final String ROOT_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath();
    private WebServer webServer;
    Button btnstart, btnstop ;
    TextView txtdebug, txtip;
    ImageView qrimageview;
    ImageButton btnToggleMode;
    static TextView textViewLog;
    static ScrollView scrollViewLog;

    private static final String PREF_FILE_NAME = "MyPrefs"; // Use your desired file name
    private int tapCount = 0;
    String linkUrl = null;

    private NetworkSpeedMonitor networkSpeedMonitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        // Check if it's the first app launch
        SharedPreferences preferences = getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        boolean isFirstLaunch = preferences.getBoolean("isFirstLaunch", true);

        if (isFirstLaunch) {
            // Show the permission explanation screen
            Intent intent = new Intent(this, SplashActivity.class);
            startActivity(intent);

            // Mark that it's not the first launch anymore
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isFirstLaunch", false);
            editor.apply();

            // Finish the current activity (MainActivity) to prevent going back
         finish();

        } else {

            setContentView(R.layout.activity_main);

            btnstart = findViewById(R.id.btnstart);
            btnstop = findViewById(R.id.btnstop);
            txtip = findViewById(R.id.txtip);
            txtdebug = findViewById(R.id.txtdebug);
            qrimageview = findViewById(R.id.qrimageview);
            qrimageview = findViewById(R.id.qrimageview);
            TextView textViewSpeed = findViewById(R.id.txtnetspeed);


            String linkText = "Open web Browser";
            textViewLog = findViewById(R.id.textViewLog);
            scrollViewLog = findViewById(R.id.scrollViewLog);
            btnToggleMode = findViewById(R.id.btnToggleMode);

            btnToggleMode.setVisibility(View.INVISIBLE);
            qrimageview.setVisibility(View.INVISIBLE);
            String StorageMount = Environment.getExternalStorageState().toString();
            // writeToLog("Checking the storage status: "+ Environment.getExternalStorageState().toString());
            if (StorageMount.equals("mounted")) {
                writeToLog("Checking the storage status: Mounted");
            } else writeToLog("No mounted Storage Found");
            btnstop.setVisibility(View.INVISIBLE);
            txtdebug.setVisibility(View.INVISIBLE);
            if (textViewSpeed != null) {
                networkSpeedMonitor = new NetworkSpeedMonitor(textViewSpeed);
            }
            getSupportActionBar().hide();


            btnstart.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    btnstop.setVisibility(View.VISIBLE);
                    txtdebug.setVisibility(View.VISIBLE);
                    //networkSpeedMonitor = new NetworkSpeedMonitor();
                    TextView textViewSpeed = findViewById(R.id.txtnetspeed);
                    networkSpeedMonitor = new NetworkSpeedMonitor(textViewSpeed);

                    networkSpeedMonitor.startNetworkMonitoring();

                    textViewSpeed.setVisibility(View.VISIBLE);
                    qrimageview.setVisibility(View.VISIBLE);
                    scrollViewLog.setVisibility(View.VISIBLE);
                    // Call ip.java to get the IP address of the phone



                    Context context = getApplicationContext(); //

                    String connectionResult = wifiordata.info(context);
                    txtdebug.setText("Connection:" + connectionResult);


                    switch (connectionResult) {
                        case "Wifi":
                            String txtipaddress = ip.getWifiIpAddress(context);
                            String qrdata = "http://" + txtipaddress + ":8080";
                            txtip.setText(qrdata);


                            textViewLog.setText("  ");
                            scrollViewLog.post(new Runnable() {
                                @Override
                                public void run() {
                                    scrollViewLog.fullScroll(View.FOCUS_UP);
                                }
                            });

                            qrcode.generateQRCode(qrdata, qrimageview);

                            // Pass IP address to server.java and start web server
                            // WebServer server;
                            //                            File rootDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath());

                            linkUrl = qrdata;

                            txtdebug.setVisibility(View.GONE);
                            txtip.setVisibility(View.VISIBLE);


                            Linkify.addLinks(txtdebug, Linkify.WEB_URLS);


                            try {
                                webServer = new WebServer(txtipaddress, MainActivity.this);

                                btnstart.setVisibility(View.INVISIBLE);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }


                            break;
                        case "Data":
                            txtdebug.setText("Please connect to wifi");
                            txtdebug.setVisibility(View.VISIBLE);
                            txtip.setVisibility(View.GONE);

                            break;
                        case "NoInternet":
                            txtdebug.setText("No wifi connection or Airplane mode");
                            txtdebug.setVisibility(View.VISIBLE);
                            txtip.setVisibility(View.GONE);
                            break;
                        case "NoConnection":
                            txtdebug.setText("Something went wrong with connection manager please rebooot and try again");
                            txtdebug.setVisibility(View.VISIBLE);
                            txtip.setVisibility(View.GONE);

                            break;
                        default:
                            txtdebug.setText("Something went wrong");
                            txtdebug.setVisibility(View.VISIBLE);
                            txtip.setVisibility(View.GONE);

                            break;
                    }


                    // Load web page with list of files and folders

                }

            });


            txtdebug.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkUrl));
                    startActivity(intent);
                }
            });

            textViewSpeed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tapCount++;
                    // Check if tap count is 5
                    if (tapCount == 5) {
                        // Show developer info

                        showDeveloperInfoLayout();
                        tapCount = 0;

                    }
                }
            });

            txtip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkUrl));
                    startActivity(intent);
                }
            });



            btnstop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Stop web server

                    if (webServer != null) {
                        webServer.stop();
                        // Log the server stopped message
                        writeToLog("\n\n  ***  Server stopped   ***");
                        btnstart.setVisibility(View.VISIBLE);
                        btnstop.setVisibility(View.INVISIBLE);
                        txtdebug.setVisibility(View.INVISIBLE);

                        textViewSpeed.setVisibility(View.INVISIBLE);
                        qrimageview.setVisibility(View.INVISIBLE);
                        networkSpeedMonitor.stop();
                    }

                    txtip.setText("");
                    // WebServer.stop();
                }
            });


        }
    }

    private void showDeveloperInfoLayout() {
        Intent intent = new Intent(this, Developer_info.class);
        startActivity(intent);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (networkSpeedMonitor != null) {
            networkSpeedMonitor.stop();
        }

    }





    public static void writeToLog (String message){
        if (textViewLog != null && scrollViewLog != null) {
            // Append the message to the log text view
            textViewLog.append("\n" + message);


            // Scroll the log scroll view to the bottom
            scrollViewLog.post(new Runnable() {
                @Override
                public void run() {
                    scrollViewLog.fullScroll(View.FOCUS_DOWN);
                }
            });
        }
    }

}