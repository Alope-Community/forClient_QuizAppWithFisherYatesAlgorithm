package com.example.quizwithfisheryates;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quizwithfisheryates.authActivities.LoginActivity;
import com.example.quizwithfisheryates.authActivities.RegisterActivity;
import com.example.quizwithfisheryates.userActivities.AboutActivity;
import com.example.quizwithfisheryates.userActivities.CourseIndexActivity;
import com.example.quizwithfisheryates.userActivities.CourseShowActivity;
import com.example.quizwithfisheryates.userActivities.LeaderboardActivity;
import com.example.quizwithfisheryates.userActivities.SelectDifficultyActivity;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        checkAuthenticate();
    }

    public void goToLogin(View view){
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void goToLeaderboard(View view){
        Intent intent = new Intent(MainActivity.this, LeaderboardActivity.class);
        startActivity(intent);
    }

    public void goToCourse(View view){
        Intent intent = new Intent(MainActivity.this, CourseIndexActivity.class);
        startActivity(intent);
    }

    public void goToAbout(View view){
        Intent intent = new Intent(MainActivity.this, AboutActivity.class);
        startActivity(intent);
    }

    public void goToStart(View view) {
        Intent intent = new Intent(
                MainActivity.this,
                SelectDifficultyActivity.class
        );
        startActivity(intent);
    }

    public void onLogout(View view){
        sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Toast.makeText(MainActivity.this, "Logout Berhasil", Toast.LENGTH_SHORT).show();


        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public boolean isAuthenticated() {
        sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);

        return sharedPreferences.getBoolean("isLoggedIn", false);
    }

    public void checkAuthenticate() {
        Button loginButton = findViewById(R.id.loginButton);
        Button logoutButton = findViewById(R.id.logoutButton);
        TextView welcomeText = findViewById(R.id.welcomeText);

        ImageView aboutButton = findViewById(R.id.aboutButton);
        ImageView footerImage = findViewById(R.id.footer);
        Button playButton = findViewById(R.id.playButton);
        Button leaderboardButton = findViewById(R.id.leaderboardButton);
        Button courseButton = findViewById(R.id.courseButton);

        ScrollView mainScrollView = findViewById(R.id.main);

        if (isAuthenticated()) {
            String authName = sharedPreferences.getString("name", "ALOPE");
            Log.d("AUTHNAME", authName);

            String authRole = sharedPreferences.getString("role", "user");

            loginButton.setVisibility(View.GONE);
            welcomeText.setText("Halo, " + authName);
            welcomeText.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.VISIBLE);

            playButton.setVisibility(View.VISIBLE);
            leaderboardButton.setVisibility(View.VISIBLE);
            courseButton.setVisibility(View.VISIBLE);
            aboutButton.setVisibility(View.VISIBLE);

            // Ganti background ke gradient
            mainScrollView.setBackgroundResource(R.drawable.gradient_background);

            footerImage.setVisibility(View.GONE);

            if (authRole.equals("admin")) {
                Intent intent = new Intent(this, com.example.quizwithfisheryates.adminActivities.MainActivity.class);
                startActivity(intent);
            }
        } else {
            loginButton.setVisibility(View.VISIBLE);
            welcomeText.setVisibility(View.GONE);
            logoutButton.setVisibility(View.GONE);

            playButton.setVisibility(View.GONE);
            leaderboardButton.setVisibility(View.GONE);
            courseButton.setVisibility(View.GONE);
            aboutButton.setVisibility(View.GONE);

            // Ganti background ke gambar
            mainScrollView.setBackgroundResource(R.drawable.bg_main_gradient);

            footerImage.setVisibility(View.VISIBLE);
        }
    }
}