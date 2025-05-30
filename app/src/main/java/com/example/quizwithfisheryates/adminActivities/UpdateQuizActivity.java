package com.example.quizwithfisheryates.adminActivities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.quizwithfisheryates.R;
import com.example.quizwithfisheryates._apiResources.QuizResource;

import java.util.ArrayList;

public class UpdateQuizActivity extends AppCompatActivity {

    private EditText editTextQuestion, editTextOptionA, editTextOptionB, editTextOptionC, editTextOptionD;
    private RadioGroup radioGroupAnswer, radioGroupDifficulty;
    private Button buttonSave;
    private ImageView imagePreview;

    private Uri selectedImageUri = null; // untuk gambar baru (jika dipilih)
    private String originalImageUrl = null; // untuk menyimpan gambar lama

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_update_quiz);

        // Inisialisasi komponen
        editTextQuestion = findViewById(R.id.editTextQuestion);
        editTextOptionA = findViewById(R.id.editTextOptionA);
        editTextOptionB = findViewById(R.id.editTextOptionB);
        editTextOptionC = findViewById(R.id.editTextOptionC);
        editTextOptionD = findViewById(R.id.editTextOptionD);
        radioGroupAnswer = findViewById(R.id.radioGroupAnswer);
        radioGroupDifficulty = findViewById(R.id.radioGroupDifficulty);
        buttonSave = findViewById(R.id.loginButton);
        imagePreview = findViewById(R.id.imagePreview);

        imagePreview.setOnClickListener(v -> {
            Intent intentPick = new Intent(Intent.ACTION_PICK);
            intentPick.setType("image/*");
            startActivityForResult(intentPick, 100);
        });

        // Ambil data quiz dari intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("quiz_id")) {
            String quizId = intent.getStringExtra("quiz_id");
            String question = intent.getStringExtra("question");
            ArrayList<String> options = intent.getStringArrayListExtra("options");
            String correctAnswer = intent.getStringExtra("answer");
            String difficulty = intent.getStringExtra("difficulty");
            originalImageUrl = intent.getStringExtra("image");

            // Isi data ke form
            editTextQuestion.setText(question);
            if (options != null && options.size() >= 4) {
                editTextOptionA.setText(options.get(0));
                editTextOptionB.setText(options.get(1));
                editTextOptionC.setText(options.get(2));
                editTextOptionD.setText(options.get(3));
            }

            if (originalImageUrl != null && !originalImageUrl.isEmpty()) {
                imagePreview.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(originalImageUrl)
                        .into(imagePreview);
            }

            // Pilih jawaban yang benar
            int index = options.indexOf(correctAnswer);
            switch (index) {
                case 0: radioGroupAnswer.check(R.id.radioA); break;
                case 1: radioGroupAnswer.check(R.id.radioB); break;
                case 2: radioGroupAnswer.check(R.id.radioC); break;
                case 3: radioGroupAnswer.check(R.id.radioD); break;
            }

            // Pilih tingkat kesulitan
            switch (difficulty.toLowerCase()) {
                case "easy": radioGroupDifficulty.check(R.id.difficultEasy); break;
                case "medium": radioGroupDifficulty.check(R.id.difficultMedium); break;
                case "hard": radioGroupDifficulty.check(R.id.difficultHard); break;
            }

            // Handle tombol simpan
            buttonSave.setText("Update");
            buttonSave.setOnClickListener(v -> {
                String editedQuestion = editTextQuestion.getText().toString();
                int selectedAnswerId = radioGroupAnswer.getCheckedRadioButtonId();
                int selectedDifficultyId = radioGroupDifficulty.getCheckedRadioButtonId();

                if (editedQuestion.isEmpty() || selectedAnswerId == -1 || selectedDifficultyId == -1) {
                    Toast.makeText(this, "Mohon isi semua field", Toast.LENGTH_SHORT).show();
                    return;
                }

                RadioButton selectedAnswerRadio = findViewById(selectedAnswerId);
                RadioButton selectedDifficultyRadio = findViewById(selectedDifficultyId);

                String editedAnswer = selectedAnswerRadio.getText().toString();
                String editedDifficulty = selectedDifficultyRadio.getText().toString();

                buttonSave.setEnabled(false); // cegah klik ganda

                // Gunakan gambar baru jika dipilih, jika tidak pakai gambar lama (URL string)
                Uri imageToUpload = selectedImageUri != null ? selectedImageUri : null;

                QuizResource.updateQuestion(
                        quizId,
                        editedQuestion,
                        imageToUpload,
                        editedDifficulty,
                        editedAnswer,
                        this,
                        new QuizResource.ApiCallback() {
                            @Override
                            public void onSuccess(String response) {
                                runOnUiThread(() -> {
                                    Toast.makeText(UpdateQuizActivity.this, "Quiz berhasil diperbarui!", Toast.LENGTH_SHORT).show();
                                    finish(); // Kembali ke list
                                });
                            }

                            @Override
                            public void onError(Exception e) {
                                runOnUiThread(() -> {
                                    Toast.makeText(UpdateQuizActivity.this, "Gagal update: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    buttonSave.setEnabled(true); // aktifkan tombol lagi
                                });
                            }
                        }
                );
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imagePreview.setImageURI(selectedImageUri); // tampilkan preview gambar baru
        }
    }
}
