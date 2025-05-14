package com.example.quizwithfisheryates;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

    public void goToRegister(View view){
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void goToLogin(View view){
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void goToStart(View view) {
        // Tampilkan popup pilihan "difficulty"
        String[] difficulties = {"Easy", "Medium", "Hard"};

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Pilih Difficulty");

        builder.setItems(difficulties, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedDifficulty = difficulties[which];

                if (isAuthenticated()) {
                    // Kirim nilai difficulty ke aktivitas berikutnya
                    Intent intent = new Intent(
                            MainActivity.this,
                            com.example.quizwithfisheryates.userActivities.MainActivity.class
                    );
                    intent.putExtra("DIFFICULTY", selectedDifficulty);
                    startActivity(intent);
                } else {
                    Toast.makeText(
                            MainActivity.this,
                            "Silahkan Login Terlebih Dulu",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });

        builder.setCancelable(true);
        builder.show();
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
        Button registerButton = findViewById(R.id.registerButton);
        Button logoutButton = findViewById(R.id.logoutButton);
        TextView welcomeText = findViewById(R.id.welcomeText);

        if (isAuthenticated()) {
            String authUsername = sharedPreferences.getString("username", "ALOPE");
            String authName = sharedPreferences.getString("username", "ALOPE");
            Log.d("AUTH", authUsername);

            loginButton.setVisibility(View.GONE);
            registerButton.setVisibility(View.GONE);

            welcomeText.setText("Halo, " + authName);
        } else{
            logoutButton.setVisibility(View.GONE);
            welcomeText.setVisibility(View.GONE);
        }
    }
}