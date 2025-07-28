package com.example.quizwithfisheryates.userActivities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quizwithfisheryates.MainActivity;
import com.example.quizwithfisheryates.R;
import com.example.quizwithfisheryates._apiResources.QuizResource;
import com.example.quizwithfisheryates._apiResources.ScoreResource;
import com.example.quizwithfisheryates._models.Question;
import com.example.quizwithfisheryates._models.Score;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {
    List<Score> scoreList = new ArrayList<>();

    SharedPreferences sharedPreferences;

    String scoreType = "score";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_leaderboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.user_leaderboard), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getLeaderboard("easy", scoreType);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    public void getEasyScore(View v){
        getLeaderboard("easy", scoreType);
    }

    public void getMediumScore(View v){
        getLeaderboard("medium", scoreType);
    }

    public void getHardScore(View v){
        getLeaderboard("hard", scoreType);
    }

    private void getLeaderboard(String difficulty, String scoreType) {
        // check ID di SESSION
        sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);
        int accountId = sharedPreferences.getInt("id", 1);

        ScoreResource.getScore(difficulty, scoreType, "user", accountId, new ScoreResource.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d("LEADERBOARD_RESPONSE", response);
                try {
                    JSONObject json = new JSONObject(response);
                    String status = json.getString("status");

                    if (status.equals("success")) {
                        JSONArray dataArray = json.getJSONArray("data");

                        scoreList.clear();

                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject obj = dataArray.getJSONObject(i);

                            int id = obj.getInt("id");
                            int account_id = obj.getInt("account_id");
                            int score = obj.getInt("score");
                            String difficulty = obj.getString("difficulty");
                            String created_at = obj.getString("created_at");
                            String account_name = obj.getString("account_name");

                            scoreList.add(new Score(account_name, difficulty, created_at, score));
                        }

                        runOnUiThread(() -> renderListToView());

                    } else {
                        runOnUiThread(() -> Toast.makeText(
                                LeaderboardActivity.this,
                                "Gagal mengambil data Leaderboard",
                                Toast.LENGTH_SHORT
                        ).show());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(
                            LeaderboardActivity.this,
                            "Format data soal salah",
                            Toast.LENGTH_SHORT
                    ).show());
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("LEADERBOARD_ERROR", "Error saat mengambil leaderboard: " + e.getMessage());
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(
                        LeaderboardActivity.this,
                        "Gagal terhubung ke server",
                        Toast.LENGTH_SHORT
                ).show());
            }
        });
    }

    void renderListToView() {
        LinearLayout container = findViewById(R.id.container);
        container.removeAllViews();

        for (Score item : scoreList) {
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(20, 20, 20, 20);

            // Buat RelativeLayout untuk menampung nama & tanggal di baris atas
            RelativeLayout topRow = new RelativeLayout(this);
            topRow.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            // TextView untuk Nama
            TextView tvName = new TextView(this);
            tvName.setText(item.getName());
            tvName.setTextSize(16);
            tvName.setTypeface(null, Typeface.BOLD);
            tvName.setId(View.generateViewId());

            RelativeLayout.LayoutParams nameParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            nameParams.addRule(RelativeLayout.ALIGN_PARENT_START);
            topRow.addView(tvName, nameParams);

            // TextView untuk Tanggal di pojok kanan
            TextView tvDate = new TextView(this);
            tvDate.setText(item.getCreatedAt());
            tvDate.setTextSize(12);
            tvDate.setTextColor(Color.GRAY);

            RelativeLayout.LayoutParams dateParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            dateParams.addRule(RelativeLayout.ALIGN_PARENT_END);
            topRow.addView(tvDate, dateParams);

            // TextView untuk Difficulty dan Score
            TextView tvDifficulty = new TextView(this);
            tvDifficulty.setText("Difficulty: " + item.getDifficulty());

            TextView tvScore = new TextView(this);
            tvScore.setText("Score: " + item.getScore());

            // Tambahkan semua view ke layout utama
            layout.addView(topRow);
            layout.addView(tvDifficulty);
            layout.addView(tvScore);

            // Optional: garis pemisah antar item
            View line = new View(this);
            line.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 2));
            line.setBackgroundColor(Color.LTGRAY);

            container.addView(layout);
            container.addView(line);
        }
    }

    public void goToUserMain(View view){
        Intent intent = new Intent(LeaderboardActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
