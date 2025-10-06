package com.example.quizwithfisheryates.adminActivities;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quizwithfisheryates.R;
import com.example.quizwithfisheryates._apiResources.ScoreResource;
import com.example.quizwithfisheryates._models.Score;
import com.example.quizwithfisheryates._utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RankingActivity extends AppCompatActivity {
    List<Score> scoreList = new ArrayList<>();

    SharedPreferences sharedPreferences;

    String scoreType = "leaderboard";

    String difficulty = "easy";

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

        // check user atau admin
        sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);
        String role = sharedPreferences.getString("role", "admin");

        if(role.equals("admin")){
            scoreType= "score";
        }

        getLeaderboard("easy", scoreType);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    public void changeButtonColor(){
        // Ambil tombol
        AppCompatButton easyButton = findViewById(R.id.easyScoreButton);
        AppCompatButton mediumButton = findViewById(R.id.mediumScoreButton);
        AppCompatButton hardButton = findViewById(R.id.hardScoreButton);

        // Reset semua tombol ke warna default
        easyButton.setBackgroundColor(Color.parseColor("#a4c9ff"));
        mediumButton.setBackgroundColor(Color.parseColor("#a4c9ff"));
        hardButton.setBackgroundColor(Color.parseColor("#a4c9ff"));

        // Set tombol aktif sesuai difficulty
        switch (difficulty) {
            case "easy":
                easyButton.setBackgroundColor(Color.parseColor("#2196F3"));
                break;
            case "medium":
                mediumButton.setBackgroundColor(Color.parseColor("#2196F3"));
                break;
            case "hard":
                hardButton.setBackgroundColor(Color.parseColor("#2196F3"));
                break;
        }
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
        this.difficulty = difficulty;
        changeButtonColor();

        String diff = "Mudah";
        if (difficulty.equals("medium")) {
            diff = "Sedang";
        } else if (difficulty.equals("hard")) {
            diff = "Sulit";
        }

        TextView tvTitle = findViewById(R.id.title);
        tvTitle.setText("Data Nilai " + StringUtils.capitalize(diff));

        ScoreResource.getScore(difficulty, scoreType, "admin", 1, new ScoreResource.ApiCallback() {
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
                            String account_nisn = obj.getString("account_nisn");

                            scoreList.add(new Score(account_name, account_nisn, difficulty, created_at, score));
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

    void renderListToView() {
        LinearLayout container = findViewById(R.id.container);
        container.removeAllViews();

        for (Score item : scoreList) {
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(20, 20, 20, 20);

            RelativeLayout topLayout = new RelativeLayout(this);

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
            topLayout.addView(tvName, nameParams);

            TextView tvDate = new TextView(this);
            tvDate.setText(item.getCreatedAt());
            tvDate.setTextSize(12);
            tvDate.setTextColor(Color.GRAY);

            RelativeLayout.LayoutParams dateParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            dateParams.addRule(RelativeLayout.ALIGN_PARENT_END);
            topLayout.addView(tvDate, dateParams);

            //
            String difficulty = "Mudah";
            if (item.getDifficulty().equals("medium")) {
                difficulty = "Sedang";
            } else if (item.getDifficulty().equals("hard")) {
                difficulty = "Sulit";
            }

            TextView tvDifficulty = new TextView(this);
            tvDifficulty.setText("Tingkat Soal: " + difficulty);

            TextView tvScore = new TextView(this);
            tvScore.setText("Skor: " + item.getScore());

            layout.addView(topLayout);
            layout.addView(tvDifficulty);
            layout.addView(tvScore);

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
