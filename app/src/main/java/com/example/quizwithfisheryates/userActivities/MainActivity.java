package com.example.quizwithfisheryates.userActivities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quizwithfisheryates.R;
import com.example.quizwithfisheryates._apiResources.AuthResource;
import com.example.quizwithfisheryates._apiResources.QuestionResource;
import com.example.quizwithfisheryates._models.Question;
import com.example.quizwithfisheryates.authActivities.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //
    TextView currentQuestionNumber;
    private TextView textQuestion;
    private List<Question> questions = new ArrayList<>();
    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.user_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Text untuk nomor soal
        currentQuestionNumber = findViewById(R.id.currentQuestionNumber);
        updateQuestionNumber();

        // Text untuk menampilkan soal
        textQuestion = findViewById(R.id.question);

        // Ambil soal dari database lewat resource
        getQuestionResource();

        // Untuk proses pilih jawaban
        Button btnA = findViewById(R.id.btnOptionA);
        Button btnB = findViewById(R.id.btnOptionB);
        Button btnC = findViewById(R.id.btnOptionC);
        Button btnD = findViewById(R.id.btnOptionD);

        View.OnClickListener optionClickListener = v -> {
            Button clickedButton = (Button) v;
            String selectedAnswer = clickedButton.getText().toString();

            // Simpan ke database
//            saveAnswerToDatabase(selectedAnswer);

            // Lanjut ke soal berikutnya
            nextQuestion();
        };

        btnA.setOnClickListener(optionClickListener);
        btnB.setOnClickListener(optionClickListener);
        btnC.setOnClickListener(optionClickListener);
        btnD.setOnClickListener(optionClickListener);

    }

    private void showQuestion(int index) {
        Question question = questions.get(index);

        textQuestion.setText(question.getQuestion());
    }

    private void nextQuestion() {
        currentIndex++;
        if (currentIndex < questions.size()) {
            showQuestion(currentIndex); // update UI untuk soal baru
            updateQuestionNumber();
        } else {
            Toast.makeText(this, "Semua soal selesai!", Toast.LENGTH_SHORT).show();
            // Bisa navigasi ke hasil, atau simpan total skor
        }
    }

    private void updateQuestionNumber(){
        currentQuestionNumber.setText("Soal No. " + (currentIndex + 1));
    }

    public void getQuestionResource(){
        QuestionResource.getQuestion("easy", new QuestionResource.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    String status = json.getString("status");

                    if (status.equals("success")) {
                        JSONArray dataArray = json.getJSONArray("data");

                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject obj = dataArray.getJSONObject(i);
                            int id = obj.getInt("id");
                            String questionText = obj.getString("question");
                            String image = obj.isNull("image") ? null : obj.getString("image");
                            String difficulty = obj.getString("difficulty");

                            questions.add(new Question(id, questionText, image, difficulty));
                        }

                        // Update UI di thread utama
                        runOnUiThread(() -> showQuestion(currentIndex));

                    } else {
                        runOnUiThread(() -> Toast.makeText(
                                MainActivity.this,
                                "Gagal mengambil data soal",
                                Toast.LENGTH_SHORT
                        ).show());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(
                            MainActivity.this,
                            "Format data soal salah",
                            Toast.LENGTH_SHORT
                    ).show());
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(
                        MainActivity.this,
                        "Gagal terhubung ke server",
                        Toast.LENGTH_SHORT
                ).show());
            }
        });
    }
}
