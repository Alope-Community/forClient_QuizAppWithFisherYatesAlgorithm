package com.example.quizwithfisheryates.userActivities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quizwithfisheryates.R;

public class AboutActivity  extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_about);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.user_about), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void openInstagramUrl(View view) {
        String url = "https://www.instagram.com/rizkypurnama__s?igsh=Z2dram56cGozM2d2";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setPackage("com.instagram.android");

        // Coba buka dengan aplikasi Instagram
        try {
            view.getContext().startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Jika aplikasi Instagram tidak tersedia, buka di browser
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            view.getContext().startActivity(browserIntent);
        }
    }

    public void openTwitterUrl(View view) {
        String url = "https://x.com/Mpurrss?s=09";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setPackage("com.instagram.android");

        try {
            view.getContext().startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            view.getContext().startActivity(browserIntent);
        }
    }

    public void openFacebookUrl(View view) {
        String url = "https://www.facebook.com/share/15mv4zcPpH/";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setPackage("com.instagram.android");

        try {
            view.getContext().startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            view.getContext().startActivity(browserIntent);
        }
    }

}
