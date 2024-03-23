package k11.superac11.wifitransfer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

public class Developer_info extends AppCompatActivity {
    private static final String TAG = "Dev:";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_info);
        ImageView emailTextView = findViewById(R.id.imgEmail);
        ImageView githubTextView = findViewById(R.id.imgGithub);
        ImageView twitterTextView = findViewById(R.id.imgTwitter);
        ImageView websiteTextView = findViewById(R.id.imgWeb);
        //TextView mottoTextView= findViewById(R.id.mottoTextView);

        emailTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtemail= "sagaracharya5@gmail.com";
                openEmail(new String[]{txtemail}, "Subject");
            }
        });

        githubTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrl("github.com/superac11");
            }
        });

        // Set onClickListener for Twitter
        twitterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrl("twitter.com/superac11");
            }
        });

        // Set onClickListener for Website
        websiteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrl("sagarac.com");
            }
        });




    }




    private void composeEmail(String[] addresses, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void openUrl(String url) {
        Log.d( TAG,"Actual  URL: "+ url);

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            // Prepend "https://www." to website URLs
            url = "https://www." + url;
        }
        Log.d( TAG,"First pass URL: "+ url);

        openWebsite(url);
    }

    private void openWebsite(String url) {
        Log.d( TAG,"Received URL: "+ url);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);

    }
    private void openEmail(String[] strings, String email) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + email));
        startActivity(intent);

       }



}