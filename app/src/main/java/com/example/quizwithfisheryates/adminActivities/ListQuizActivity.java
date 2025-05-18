package com.example.quizwithfisheryates.adminActivities;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quizwithfisheryates.R;

public class ListQuizActivity extends AppCompatActivity {

    String[][] quizList = {
            {"Apa ibu kota Indonesia?", "Jakarta", "Bandung", "Surabaya", "Medan"},
            {"2 + 2 sama dengan?", "3", "4", "5", "6"},
            {"Siapa presiden pertama Indonesia?", "Soeharto", "Habibie", "Jokowi", "Soekarno"}
    };

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

        // Contoh data kuis (bisa diganti dengan data dari database/API)
        String[] quizTitles = {"Kuis Matematika", "Kuis Sejarah", "Kuis IPA", "Kuis Bahasa Indonesia"};

        LinearLayout quizContainer = findViewById(R.id.quizContainer);

        for (String[] quiz : quizList) {
            String question = quiz[0];

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

            // Tambah pilihan jawaban
            for (int i = 1; i < quiz.length; i++) {
                TextView optionView = new TextView(this);
                optionView.setText(quiz[i]);
                optionView.setTextSize(16);
                optionView.setPadding(16, 4, 0, 4);
                quizContainer.addView(optionView);
            }

            // Tambah garis pemisah
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
