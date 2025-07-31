package com.example.quizwithfisheryates.adminActivities.quizzes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.quizwithfisheryates.R;
import com.example.quizwithfisheryates._apiResources.QuizResource;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class UpdateQuiz extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_IMAGE = 1001;

    private EditText editTextQuestion, editTextOptionA, editTextOptionB, editTextOptionC, editTextOptionD;
    private RadioGroup radioGroupAnswer, radioGroupDifficulty;
    private Button buttonSave, buttonUploadImage;
    private ImageView imagePreview;

    private Uri selectedImageUri = null;
    private String originalImageUrl = null;
    private int quizId;
    private ArrayList<String> options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_quiz_update);

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
        buttonUploadImage = findViewById(R.id.buttonUploadImage);

        // Upload image handler
        buttonUploadImage.setOnClickListener(v -> openImagePicker());

        // Ambil data dari intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("quiz_id")) {
            quizId = intent.getIntExtra("quiz_id", -1);
            String question = intent.getStringExtra("question");
            options = intent.getStringArrayListExtra("options");
            String correctAnswer = intent.getStringExtra("answer");
            String difficulty = intent.getStringExtra("difficulty");
            originalImageUrl = intent.getStringExtra("image");

            // Set data ke form
            editTextQuestion.setText(question);
            if (options != null && options.size() >= 4) {
                editTextOptionA.setText(options.get(0));
                editTextOptionB.setText(options.get(1));
                editTextOptionC.setText(options.get(2));
                editTextOptionD.setText(options.get(3));
            }

            if (originalImageUrl != null && !originalImageUrl.isEmpty()) {
                imagePreview.setVisibility(View.VISIBLE);
                Glide.with(this).load(originalImageUrl).into(imagePreview);
            }

            // Jawaban benar
            int index = options.indexOf(correctAnswer);
            if (index == 0) radioGroupAnswer.check(R.id.radioA);
            else if (index == 1) radioGroupAnswer.check(R.id.radioB);
            else if (index == 2) radioGroupAnswer.check(R.id.radioC);
            else if (index == 3) radioGroupAnswer.check(R.id.radioD);

            // Difficulty
            if (difficulty != null) {
                switch (difficulty.toLowerCase()) {
                    case "easy": radioGroupDifficulty.check(R.id.difficultEasy); break;
                    case "medium": radioGroupDifficulty.check(R.id.difficultMedium); break;
                    case "hard": radioGroupDifficulty.check(R.id.difficultHard); break;
                }
            }
        }

        buttonSave.setText("Update");
        buttonSave.setOnClickListener(v -> onSubmitUpdate());

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                imagePreview.setImageBitmap(bitmap);
                imagePreview.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Gagal memuat gambar", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void onSubmitUpdate() {
        String question = editTextQuestion.getText().toString().trim();
        String optionA = editTextOptionA.getText().toString().trim();
        String optionB = editTextOptionB.getText().toString().trim();
        String optionC = editTextOptionC.getText().toString().trim();
        String optionD = editTextOptionD.getText().toString().trim();

        int selectedAnswerId = radioGroupAnswer.getCheckedRadioButtonId();
        int selectedDifficultyId = radioGroupDifficulty.getCheckedRadioButtonId();

        if (selectedAnswerId == -1 || selectedDifficultyId == -1 || question.isEmpty()
                || optionA.isEmpty() || optionB.isEmpty() || optionC.isEmpty() || optionD.isEmpty()) {
            Toast.makeText(this, "Harap isi semua data dengan lengkap", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedAnswer = findViewById(selectedAnswerId);
        String answerLetter = selectedAnswer.getText().toString().trim();

        String finalAnswer = optionA;
        if (answerLetter.equals("B")) finalAnswer = optionB;
        else if (answerLetter.equals("C")) finalAnswer = optionC;
        else if (answerLetter.equals("D")) finalAnswer = optionD;

        RadioButton selectedDifficulty = findViewById(selectedDifficultyId);
        String finalDifficulty = selectedDifficulty.getText().toString().trim();

        QuizResource.updateQuestion(quizId, question, selectedImageUri, finalDifficulty, finalAnswer, this, new QuizResource.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    String message = json.getString("message");

                    // Update semua opsi
                    QuizResource.updateOptions(quizId, optionA, optionB, optionC, optionD, new QuizResource.ApiCallback() {
                        @Override
                        public void onSuccess(String response) {
                            runOnUiThread(() -> {
                                Toast.makeText(UpdateQuiz.this, "Update berhasil", Toast.LENGTH_SHORT).show();
//                                finish();
//                                Intent intent = new Intent(UpdateQuiz.this, UpdateQuiz.class);
//                                startActivity(intent);
                            });
                        }

                        @Override
                        public void onError(Exception e) {
                            runOnUiThread(() -> Toast.makeText(UpdateQuiz.this, "Gagal update opsi", Toast.LENGTH_SHORT).show());
                        }
                    });

                } catch (JSONException e) {
                    runOnUiThread(() -> Toast.makeText(UpdateQuiz.this, "Kesalahan JSON", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> Toast.makeText(UpdateQuiz.this, "Update gagal", Toast.LENGTH_SHORT).show());
            }
        });
    }
}
