package com.example.quizwithfisheryates.userActivities;

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

import com.example.quizwithfisheryates.MainActivity;
import com.example.quizwithfisheryates.R;
import com.example.quizwithfisheryates._models.Score;

public class ScoreActivity extends AppCompatActivity {

    int score;
    String difficulty;


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

        // ambil pilihan difficulty
        difficulty = getIntent().getStringExtra("DIFFICULTY");

        // ambil hasil Score
        score = getIntent().getIntExtra("SCORE", 0);

        TextView scoreText = findViewById(R.id.score);
        scoreText.setText(Integer.toString(score));

        // Get ID From Session
        SharedPreferences sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);
        int accountID = sharedPreferences.getInt("id", 1);

        submitScore(accountID, score);
    }

    void submitScore(int accountID, int score){
        Score.postScore(accountID, difficulty, score, new Score.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d("CREATE_SUCCESS", response);
            }

            @Override
            public void onError(Exception e) {
                Log.e("CREATE_ERROR", "Tambah Gagal", e);
                runOnUiThread(() -> {
                    Toast.makeText(ScoreActivity.this, "Tambah gagal. Coba lagi.", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    public void goToHome(View v){
        Intent intent = new Intent(ScoreActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
