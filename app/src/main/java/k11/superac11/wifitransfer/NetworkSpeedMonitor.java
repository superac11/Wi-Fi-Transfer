package k11.superac11.wifitransfer;

import android.net.TrafficStats;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

public class NetworkSpeedMonitor {

    private TextView textView;
    private long lastTotalRxBytes;
    private long lastTotalTxBytes;
    private long lastTimeStamp;
    private Handler handler;
    private HandlerThread handlerThread;
    private Timer timer;
    private DecimalFormat decimalFormat;

    public NetworkSpeedMonitor(TextView textView) {
        this.textView = textView;
        this.lastTotalRxBytes = 0;
        this.lastTotalTxBytes = 0;
        this.lastTimeStamp = 0;
        this.handlerThread = new HandlerThread("NetworkSpeedThread");
        this.handlerThread.start();
        this.handler = new Handler(handlerThread.getLooper());
        this.timer = new Timer();
        this.decimalFormat = new DecimalFormat("#.##");
        start(); // Start the timer immediately upon construction
    }

    public void startNetworkMonitoring() {
        // If you want to start monitoring separately from the constructor,
        // you can call the start method directly.
        start();
    }

    public void start() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        monitorNetworkSpeed();
                    }
                });
            }
        }, 0, 3000);
    }


    private void monitorNetworkSpeed() {
        long totalRxBytes = TrafficStats.getTotalRxBytes();
        long totalTxBytes = TrafficStats.getTotalTxBytes();
        long currentTimeStamp = System.currentTimeMillis();

        if (currentTimeStamp >= lastTimeStamp) {
            long timeInterval = currentTimeStamp - lastTimeStamp;

            if (timeInterval > 0) {
                long downloadSpeed = (totalRxBytes - lastTotalRxBytes) * 1000 / timeInterval;
                long uploadSpeed = (totalTxBytes - lastTotalTxBytes) * 1000 / timeInterval;
                String downloadSpeedStr = formatSpeed(downloadSpeed);
                String uploadSpeedStr = formatSpeed(uploadSpeed);
                final String speedStr = "Download Speed: " + downloadSpeedStr + "\nUpload Speed: " + uploadSpeedStr;

                // Update UI on the main thread
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(speedStr);
                    }
                });
            }
            lastTimeStamp = currentTimeStamp;
            lastTotalRxBytes = totalRxBytes;
            lastTotalTxBytes = totalTxBytes;
        }
    }

    private String formatSpeed(long speed) {
        if (speed > 1024 * 1024) {
            return decimalFormat.format(speed / (1024f * 1024f)) + " MB/s";
        } else {
            return decimalFormat.format(speed / 1024f) + " KB/s";
        }
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (handlerThread != null) {
            handlerThread.quitSafely();
            handlerThread = null;
        }
    }
}
