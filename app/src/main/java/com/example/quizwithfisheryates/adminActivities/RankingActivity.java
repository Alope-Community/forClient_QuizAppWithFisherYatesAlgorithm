package com.example.quizwithfisheryates.adminActivities;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quizwithfisheryates.R;
import com.example.quizwithfisheryates._apiResources.ScoreResource;
import com.example.quizwithfisheryates._models.Score;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RankingActivity extends AppCompatActivity {
    List<Score> scoreList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_ranking);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.admin_ranking), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getLeaderboard("easy");

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    public void getEasyScore(View v){
        getLeaderboard("easy");
    }

    public void getMediumScore(View v){
        getLeaderboard("medium");
    }

    public void getHardScore(View v){
        getLeaderboard("hard");
    }

    private void getLeaderboard(String difficulty) {
        ScoreResource.getScore(difficulty, new ScoreResource.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d("LEADERBOARD_RESPONSE", response); // <-- Cetak response di Logcat
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

                            scoreList.add(new Score(account_name, difficulty, score));
                        }

                        runOnUiThread(() -> renderListToView());

                    } else {
                        runOnUiThread(() -> Toast.makeText(
                                RankingActivity.this,
                                "Gagal mengambil data Leaderboard",
                                Toast.LENGTH_SHORT
                        ).show());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(
                            RankingActivity.this,
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
                        RankingActivity.this,
                        "Gagal terhubung ke server",
                        Toast.LENGTH_SHORT
                ).show());
            }
        });
    }

    void renderListToView(){
        LinearLayout container = findViewById(R.id.container);
        container.removeAllViews();
        for (Score item : scoreList) {
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(20, 20, 20, 20);

            TextView tvName = new TextView(this);
            tvName.setText(item.getName());
            tvName.setTextSize(16);
            tvName.setTypeface(null, Typeface.BOLD);

            TextView tvDifficulty = new TextView(this);
            tvDifficulty.setText("Difficulty: " + item.getDifficulty());

            TextView tvScore = new TextView(this);
            tvScore.setText("Score: " + item.getScore());

            layout.addView(tvName);
            layout.addView(tvDifficulty);
            layout.addView(tvScore);

            // Optional: separator
            View line = new View(this);
            line.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 2));
            line.setBackgroundColor(Color.LTGRAY);

            container.addView(layout);
            container.addView(line);
        }
    }

    public void exportRanking(View v){
        ScoreResource.exportScore(RankingActivity.this, new ScoreResource.FileCallback() {
            public void onSuccess(String filePath) {
                runOnUiThread(() -> Toast.makeText(RankingActivity.this,
                        "Export berhasil disimpan: " + filePath,
                        Toast.LENGTH_LONG).show());
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(RankingActivity.this,
                        "Gagal mendownload file",
                        Toast.LENGTH_SHORT).show());
            }
        });
    }
}
