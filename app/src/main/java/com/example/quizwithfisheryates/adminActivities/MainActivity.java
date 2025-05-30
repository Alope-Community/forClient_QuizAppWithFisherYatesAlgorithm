package com.example.quizwithfisheryates.adminActivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quizwithfisheryates.R;

public class MainActivity extends AppCompatActivity {
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
    }

    public void goToCreateQuestion(View v){
        Intent intent = new Intent(MainActivity.this, CreateQuizActivity.class);
        startActivity(intent);
    }
    public void goToListQuestion(View v){
        Intent intent = new Intent(MainActivity.this, ListQuizActivity.class);
        startActivity(intent);
    }

    public void goToRanking(View v){
        Intent intent = new Intent(MainActivity.this, RankingActivity.class);
        startActivity(intent);
    }
}
