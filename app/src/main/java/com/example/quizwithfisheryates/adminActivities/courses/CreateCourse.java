package com.example.quizwithfisheryates.adminActivities.courses;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quizwithfisheryates.R;
import com.example.quizwithfisheryates._apiResources.CourseResource;

import jp.wasabeef.richeditor.RichEditor;

public class CreateCourse extends AppCompatActivity {

    private EditText etTitle, etDescription;
    private RichEditor editor;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_course_create);

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        editor = findViewById(R.id.editor);
        btnSubmit = findViewById(R.id.btnSubmitCourse);

        editor.setEditorHeight(200);
        editor.setPlaceholder("Write course content here...");

        btnSubmit.setOnClickListener(v -> submitCourse());
    }

    // Fungsi Toolbar Formatting
    public void setBold(View v) {
        editor.setBold();
    }

    public void setItalic(View v) {
        editor.setItalic();
    }

    public void setHeading1(View v) {
        editor.setHeading(1);
    }

    public void setUnorderedList(View v) {
        editor.setBullets();
    }

    // Submit Course
    public void submitCourse() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String bodyHtml = editor.getHtml();

        if (title.isEmpty() || description.isEmpty() || bodyHtml == null || bodyHtml.isEmpty()) {
            Toast.makeText(this, "Semua field wajib diisi!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get ID From Session
        SharedPreferences sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);
        int accountID = sharedPreferences.getInt("id", 1);

        CourseResource.postCourse(title, description, bodyHtml, accountID, new CourseResource.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    Log.d("CREATE_COURSE", "Response: " + response);
                    Toast.makeText(CreateCourse.this, "Kursus berhasil dibuat!", Toast.LENGTH_SHORT).show();
                    finish(); // kembali ke halaman sebelumnya
                });
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(CreateCourse.this, "Gagal membuat kursus: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}
