package com.example.quizwithfisheryates.userActivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quizwithfisheryates.MainActivity;
import com.example.quizwithfisheryates.R;

public class ScoreActivity extends AppCompatActivity {

    int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_score);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.user_score), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ambil hasil Score
        score = getIntent().getIntExtra("SCORE", 0);

        TextView scoreText = findViewById(R.id.score);
        scoreText.setText(Integer.toString(score));
    }

    public void goToHome(View v){
        Intent intent = new Intent(ScoreActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
