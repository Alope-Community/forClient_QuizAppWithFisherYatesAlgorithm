package com.example.quizwithfisheryates.adminActivities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quizwithfisheryates.R;
import com.example.quizwithfisheryates.adminActivities.courses.IndexCourse;
import com.example.quizwithfisheryates.adminActivities.quizzes.IndexQuiz;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.admin_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ambil TextView
        TextView welcomeText = findViewById(R.id.welcomeText);


        sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);
        String adminName = sharedPreferences.getString("name", "Admin");

        welcomeText.setText("Selamat datang " + adminName);
    }

    public void goToListUser(View v){
        Intent intent = new Intent(MainActivity.this, ListUserActivity.class);
        startActivity(intent);
    }

    public void goToListQuestion(View v){
        Intent intent = new Intent(MainActivity.this, IndexQuiz.class);
        startActivity(intent);
    }

    public void goToListCourse(View v){
        Intent intent = new Intent(MainActivity.this, IndexCourse.class);
        startActivity(intent);
    }

    public void goToRanking(View v){
        Intent intent = new Intent(MainActivity.this, RankingActivity.class);
        startActivity(intent);
    }

    public void logoutAdmin(View v){
        sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Toast.makeText(MainActivity.this, "Logout Berhasil", Toast.LENGTH_SHORT).show();


        Intent intent = new Intent(MainActivity.this, com.example.quizwithfisheryates.MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
