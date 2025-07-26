package com.example.quizwithfisheryates.userActivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quizwithfisheryates.R;

public class SelectDifficultyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_select_difficulty);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.user_select_difficulty), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    public void onStartQuiz(View v){
        String selectedDifficulty = "";

        int id = v.getId();
        if (id == R.id.buttonEasy) {
            selectedDifficulty = "Easy";
        } else if (id == R.id.buttonMedium) {
            selectedDifficulty = "Medium";
        } else if (id == R.id.buttonHard) {
            selectedDifficulty = "Hard";
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("DIFFICULTY", selectedDifficulty);
        startActivity(intent);
    }
}