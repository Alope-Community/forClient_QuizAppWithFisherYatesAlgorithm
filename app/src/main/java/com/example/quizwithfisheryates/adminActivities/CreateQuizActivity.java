package com.example.quizwithfisheryates.adminActivities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quizwithfisheryates.R;
import com.example.quizwithfisheryates._apiResources.QuizResource;

import org.json.JSONException;
import org.json.JSONObject;

public class CreateQuizActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_create_quiz);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.admin_create_quiz), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void onSubmit(View view){
        EditText editTextQuestion = findViewById(R.id.editTextQuestion);
        EditText editTextOptionA = findViewById(R.id.editTextOptionA);
        EditText editTextOptionB = findViewById(R.id.editTextOptionB);
        EditText editTextOptionC = findViewById(R.id.editTextOptionC);
        EditText editTextOptionD = findViewById(R.id.editTextOptionD);
        RadioGroup radioGroupAnswer = findViewById(R.id.radioGroupAnswer);
        RadioGroup radioGroupDifficulty = findViewById(R.id.radioGroupDifficulty);

        String question = editTextQuestion.getText().toString().trim();
        String optionA = editTextOptionA.getText().toString().trim();
        String optionB = editTextOptionB.getText().toString().trim();
        String optionC = editTextOptionC.getText().toString().trim();
        String optionD = editTextOptionD.getText().toString().trim();

        // Ambil Answer
        int selectedRadioId = radioGroupAnswer.getCheckedRadioButtonId();
        RadioButton selectedRadio = findViewById(selectedRadioId);
        String correctAnswer = selectedRadio.getText().toString().trim();

        //
        String finalAnswer = optionA;

        if(correctAnswer.equals("B")){
            finalAnswer= optionB;
        } else if(correctAnswer.equals("C")){
            finalAnswer= optionC;
        } else if(correctAnswer.equals("D")){
            finalAnswer= optionD;
        }

        // Ambil Difficulty
        int selectedRadioDifficultyId = radioGroupDifficulty.getCheckedRadioButtonId();
        RadioButton selectedDifficulty = findViewById(selectedRadioDifficultyId);
        String finalDifficulty = selectedDifficulty.getText().toString().trim();

        QuizResource.postQuestion(question, "", finalDifficulty, finalAnswer, new QuizResource.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d("CREATE_SUCCESS", response);

                try {
                    JSONObject json = new JSONObject(response);
                    String status = json.getString("status");
                    String message = json.getString("message");
                    JSONObject data = json.getJSONObject("data");

                    int question_id = data.getInt("question_id");

                    // Insert Options
                    insertOption(question_id, optionA);
                    insertOption(question_id, optionB);
                    insertOption(question_id, optionC);
                    insertOption(question_id, optionD);

                    runOnUiThread(() -> {
                        Toast.makeText(CreateQuizActivity.this, message, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(CreateQuizActivity.this, MainActivity.class));
                    });
                    finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(
                            CreateQuizActivity.this,
                            "Format response JSON salah",
                            Toast.LENGTH_SHORT
                    ).show());
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("CREATE_ERROR", "Tambah Gagal", e);
                runOnUiThread(() -> {
                    Toast.makeText(CreateQuizActivity.this, "Tambah gagal. Coba lagi.", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void insertOption(int questionId, String option) {
        QuizResource.postOption(questionId, option, new QuizResource.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d("CREATE_OPTION_SUCCESS", "Option berhasil ditambahkan");

//                runOnUiThread(() -> Toast.makeText(CreateQuizActivity.this, "Opsi " + optionLetter + " berhasil ditambahkan", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onError(Exception e) {
                Log.e("CREATE_OPTION_ERROR", "Tambah Opsi Gagal", e);
//                runOnUiThread(() -> {
//                    Toast.makeText(CreateQuizActivity.this, "Tambah opsi " + optionLetter + " gagal. Coba lagi.", Toast.LENGTH_SHORT).show();
//                });
            }
        });
    }
}
