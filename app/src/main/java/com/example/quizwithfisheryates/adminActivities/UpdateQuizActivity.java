package com.example.quizwithfisheryates.adminActivities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.quizwithfisheryates.R;

import java.util.ArrayList;

public class UpdateQuizActivity extends AppCompatActivity {

    private EditText editTextQuestion, editTextOptionA, editTextOptionB, editTextOptionC, editTextOptionD;
    private RadioGroup radioGroupAnswer, radioGroupDifficulty;
    private Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_update_quiz); // pakai layout yang sama

        // Inisialisasi komponen
        editTextQuestion = findViewById(R.id.editTextQuestion);
        editTextOptionA = findViewById(R.id.editTextOptionA);
        editTextOptionB = findViewById(R.id.editTextOptionB);
        editTextOptionC = findViewById(R.id.editTextOptionC);
        editTextOptionD = findViewById(R.id.editTextOptionD);
        radioGroupAnswer = findViewById(R.id.radioGroupAnswer);
        radioGroupDifficulty = findViewById(R.id.radioGroupDifficulty);
        buttonSave = findViewById(R.id.loginButton);

        // Ambil data quiz dari intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("quiz_id")) {
            String quizId = intent.getStringExtra("quiz_id");
            String question = intent.getStringExtra("question");
            ArrayList<String> options = intent.getStringArrayListExtra("options");
            String correctAnswer = intent.getStringExtra("answer");
            String difficulty = intent.getStringExtra("difficulty");

            // Isi data ke form
            editTextQuestion.setText(question);
            if (options != null && options.size() >= 4) {
                editTextOptionA.setText(options.get(0));
                editTextOptionB.setText(options.get(1));
                editTextOptionC.setText(options.get(2));
                editTextOptionD.setText(options.get(3));
            }

            ImageView imagePreview = findViewById(R.id.imagePreview);

            String image = intent.getStringExtra("image");
            if (image != null && !image.isEmpty()) {
                imagePreview.setVisibility(View.VISIBLE);

                Glide.with(this)
                        .load(image)
                        .into(imagePreview);
            }

            // Pilih jawaban yang benar
            int index = options.indexOf(correctAnswer);

            switch (index) {
                case 0:
                    radioGroupAnswer.check(R.id.radioA);
                    break;
                case 1:
                    radioGroupAnswer.check(R.id.radioB);
                    break;
                case 2:
                    radioGroupAnswer.check(R.id.radioC);
                    break;
                case 3:
                    radioGroupAnswer.check(R.id.radioD);
                    break;
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
                // TODO: Kirim data update ke server lewat API update
                Toast.makeText(this, "Quiz ID " + quizId + " berhasil diperbarui", Toast.LENGTH_SHORT).show();
                finish(); // kembali ke list
            });
        }
    }
}

