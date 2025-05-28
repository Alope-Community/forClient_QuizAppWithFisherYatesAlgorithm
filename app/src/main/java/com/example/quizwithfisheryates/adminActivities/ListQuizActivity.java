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
import com.example.quizwithfisheryates._apiResources.QuizResource;
import com.example.quizwithfisheryates._models.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListQuizActivity extends AppCompatActivity {

    List<Question> quizList = new ArrayList<>();
    LinearLayout quizContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_list_quiz);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.admin_list_quiz), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        quizContainer = findViewById(R.id.quizContainer);

        // Ambil data soal
        getQuestions();
    }

    public void getQuestions() {
        QuizResource.getQuestion("easy", "admin", new QuizResource.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    String status = json.getString("status");

                    Log.d("ONSUCCESS", response);

                    if (status.equals("success")) {
                        JSONArray dataArray = json.getJSONArray("data");

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
                                ListQuizActivity.this,
                                "Gagal mengambil data soal",
                                Toast.LENGTH_SHORT
                        ).show());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(
                            ListQuizActivity.this,
                            "Format data soal salah",
                            Toast.LENGTH_SHORT
                    ).show());
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(
                        ListQuizActivity.this,
                        "Gagal terhubung ke server",
                        Toast.LENGTH_SHORT
                ).show());
            }
        });
    }

    private void showQuiz() {
        for (Question quiz : quizList) {
            String question = quiz.getQuestion();
            List<String> options = quiz.getValue();

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

            char label = 'A';
            for (int i = 0; i < options.size(); i++) {
                String option = options.get(i);

                TextView optionView = new TextView(this);

                optionView.setText(label + ". " + option);
                optionView.setTextSize(16);
                optionView.setPadding(16, 4, 0, 4);

                // Kalau option sama dengan answer, bikin bold
                if (option.equals(quiz.getAnswer())) {
                    optionView.setTypeface(null, Typeface.BOLD);
                }

                quizContainer.addView(optionView);
                label++;
            }

            View divider = new View(this);
            LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 1);
            dividerParams.setMargins(0, 20, 0, 20);
            divider.setLayoutParams(dividerParams);
            divider.setBackgroundColor(Color.BLACK);
            quizContainer.addView(divider);
        }
    }
}
