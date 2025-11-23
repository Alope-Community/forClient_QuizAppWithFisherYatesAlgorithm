package com.example.quizwithfisheryates.adminActivities.quizzes;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.quizwithfisheryates.R;
import com.example.quizwithfisheryates._apiResources.QuizResource;
import com.example.quizwithfisheryates._models.Option;
import com.example.quizwithfisheryates._models.Question;
import com.example.quizwithfisheryates._utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class IndexQuiz extends AppCompatActivity {

    String difficulty = "easy";
    List<Question> quizList = new ArrayList<>();
    LinearLayout quizContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_quiz_index);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.admin_list_quiz), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        quizContainer = findViewById(R.id.quizContainer);

        // Ambil data soal
        getQuestions("easy");

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    public void changeButtonColor(){
        // Ambil tombol
        AppCompatButton easyButton = findViewById(R.id.easyQuizButton);
        AppCompatButton mediumButton = findViewById(R.id.mediumQuizButton);
        AppCompatButton hardButton = findViewById(R.id.hardQuizButton);

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

    public void getEasyQuiz(View v){getQuestions("easy");}

    public void getMediumQuiz(View v){
        getQuestions("medium");
    }

    public void getHardQuiz(View v){
        getQuestions("hard");
    }

    public void getQuestions(String difficulty) {

        String diff = "Mudah";
        if (difficulty.equals("medium")) {
            diff = "Sedang";
        } else if (difficulty.equals("hard")) {
            diff = "Sulit";
        }

        this.difficulty= difficulty;

        changeButtonColor();

        TextView tvTitle = findViewById(R.id.title);
        tvTitle.setText("Data Soal " + StringUtils.capitalize(diff));

        Question.getQuestion(difficulty, "admin", new Question.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    String status = json.getString("status");

                    Log.d("ONSUCCESS", response);

                    if (status.equals("success")) {
                        JSONArray dataArray = json.getJSONArray("data");

                        quizList.clear();

                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject obj = dataArray.getJSONObject(i);
                            int questionId = obj.getInt("question_id");
                            String questionText = obj.getString("question");
                            String difficulty = obj.getString("difficulty");
                            String image = obj.isNull("image") ? null : obj.getString("image");
                            String answer = obj.getString("answer");
                            int optionId = obj.getInt("option_id");

                            JSONArray valueArray = new JSONArray(obj.getString("value"));
                            List<String> values = new ArrayList<>();
                            for (int j = 0; j < valueArray.length(); j++) {
                                values.add(valueArray.getString(j));
                            }

                            quizList.add(new Question(questionId, questionText, difficulty, image, answer, optionId, values));
                        }

                        runOnUiThread(() -> showQuiz());

                    } else {
                        runOnUiThread(() -> Toast.makeText(
                                IndexQuiz.this,
                                "Gagal mengambil data soal",
                                Toast.LENGTH_SHORT
                        ).show());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(
                            IndexQuiz.this,
                            "Format data soal salah",
                            Toast.LENGTH_SHORT
                    ).show());
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(
                        IndexQuiz.this,
                        "Gagal terhubung ke server",
                        Toast.LENGTH_SHORT
                ).show());
            }
        });
    }

    private void showQuiz() {
        quizContainer.removeAllViews();

        for (Question quiz : quizList) {
            String question = quiz.getQuestion();
            List<String> options = quiz.getValue();
            String imageUrl = quiz.getImage();

            // Tambah pertanyaan
            TextView questionView = new TextView(this);
            questionView.setText(question);
            questionView.setTextSize(20);
            questionView.setTypeface(null, Typeface.BOLD);
            questionView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            questionView.setPadding(0, 24, 0, 12);
            quizContainer.addView(questionView);

            // Jika ada gambar, tampilkan
            if (imageUrl != null && !imageUrl.isEmpty()) {
                ImageView imageView = new ImageView(this);

                int minHeightDp = 300;
                float scale = getResources().getDisplayMetrics().density;
                int minHeightPx = (int) (minHeightDp * scale + 0.5f);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 0, 0, 16);
                imageView.setLayoutParams(params);
                imageView.setMinimumHeight(minHeightPx);
                imageView.setMaxHeight(minHeightPx);
                imageView.setAdjustViewBounds(true);

                Glide.with(this)
                        .load(imageUrl)
                        .into(imageView);

                quizContainer.addView(imageView);
            }

            char label = 'A';
            for (int i = 0; i < options.size(); i++) {
                String option = options.get(i);

                TextView optionView = new TextView(this);
                optionView.setText(label + ". " + option);
                optionView.setTextSize(16);
                optionView.setPadding(16, 4, 0, 4);

                quizContainer.addView(optionView);

                label++;
            }

            // Tambahkan TextView untuk Jawaban Benar
            int correctIndex = options.indexOf(quiz.getAnswer());
            char correctLabel = (char) ('A' + correctIndex);

            TextView answerView = new TextView(this);
//            answerView.setText("Jawaban Benar: " + correctLabel + ". " + quiz.getAnswer());
            answerView.setText("Jawaban Benar: " + correctLabel);
            answerView.setTextSize(16);
            answerView.setTypeface(null, Typeface.BOLD);
            answerView.setPadding(16, 16, 0, 0);

            quizContainer.addView(answerView);

            // Buat layout horizontal untuk tombol Update dan Delete
            LinearLayout buttonLayout = new LinearLayout(this);
            buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            buttonLayoutParams.setMargins(0, 16, 0, 16);
            buttonLayout.setLayoutParams(buttonLayoutParams);

            // Tombol Update
            Button btnUpdate = new Button(this);
            btnUpdate.setText("Edit");
            btnUpdate.setAllCaps(false);
            btnUpdate.setBackgroundColor(Color.parseColor("#2196F3"));
            btnUpdate.setTextColor(Color.WHITE);

            // Tombol Delete
            Button btnDelete = new Button(this);
            btnDelete.setText("Hapus");
            btnDelete.setAllCaps(false);
            btnDelete.setBackgroundColor(Color.parseColor("#F44336"));
            btnDelete.setTextColor(Color.WHITE);

            // Tambahkan tombol ke layout horizontal
            buttonLayout.addView(btnUpdate);
            buttonLayout.addView(btnDelete);

            quizContainer.addView(buttonLayout);

            // Set listener tombol Update
            btnUpdate.setOnClickListener(v -> {
                Intent intent = new Intent(IndexQuiz.this, UpdateQuiz.class);
                intent.putExtra("quiz_id", quiz.getId());
                intent.putExtra("question", quiz.getQuestion());
                intent.putStringArrayListExtra("options", new ArrayList<>(quiz.getValue()));
                intent.putExtra("answer", quiz.getAnswer());
                intent.putExtra("difficulty", quiz.getDifficulty());
                intent.putExtra("image", quiz.getImage());
                startActivity(intent);
            });

            btnDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setTitle("Hapus Soal")
                        .setMessage("Apakah Anda yakin ingin menghapus soal ini?")
                        .setPositiveButton("Ya", (dialog, which) -> {
                            // Panggil API deleteQuestion
                            Question.deleteQuestion(quiz.getId(), new Question.ApiCallback() {
                                @Override
                                public void onSuccess(String response) {
                                    runOnUiThread(() -> {
                                        Toast.makeText(IndexQuiz.this, "Soal berhasil dihapus", Toast.LENGTH_SHORT).show();
                                        // TODO: Refresh list quiz, misal panggil ulang showQuiz atau fetch ulang data
                                        getQuestions(quiz.getDifficulty()); // contoh nama fungsi untuk refresh
                                    });
                                }

                                @Override
                                public void onError(Exception e) {
                                    runOnUiThread(() -> {
                                        Toast.makeText(IndexQuiz.this, "Gagal hapus soal: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    });
                                }
                            });
                        })
                        .setNegativeButton("Batal", null)
                        .show();
            });

            // Divider
            View divider = new View(this);
            LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 1);
            dividerParams.setMargins(0, 20, 0, 20);
            divider.setLayoutParams(dividerParams);
            divider.setBackgroundColor(Color.BLACK);
            quizContainer.addView(divider);
        }
    }

    public void goToCreateQuestion(View v){
        Intent intent = new Intent(IndexQuiz.this, CreateQuiz.class);
        startActivity(intent);
    }
}
