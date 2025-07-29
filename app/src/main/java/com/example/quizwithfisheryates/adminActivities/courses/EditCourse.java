package com.example.quizwithfisheryates.adminActivities.courses;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quizwithfisheryates.R;
import com.example.quizwithfisheryates._apiResources.CourseResource;
import com.example.quizwithfisheryates._models.Course;

import org.json.JSONObject;

import java.io.IOException;

import jp.wasabeef.richeditor.RichEditor;

public class EditCourse extends AppCompatActivity {

    private EditText etTitle, etDescription;
    private RichEditor editor;
    private int courseId;

    private static final int REQUEST_CODE_PICK_IMAGE = 1001;
    private Uri selectedImageUri;
    private ImageView imagePreview;
    private Button buttonUploadImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_course_edit);

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        editor = findViewById(R.id.editor);
        ImageView backButton = findViewById(R.id.backButton);

        courseId = getIntent().getIntExtra("course_id", -1);

        backButton.setOnClickListener(v -> finish());

        loadCourse();

        findViewById(R.id.btnSubmitCourse).setOnClickListener(this::submitEdit);

        imagePreview = findViewById(R.id.imagePreview);
        buttonUploadImage = findViewById(R.id.buttonUploadImage);

        buttonUploadImage.setOnClickListener(v -> openImagePicker());
        buttonUploadImage.setOnClickListener(v -> openImagePicker());
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

    private void loadCourse() {
        CourseResource.showCourse(courseId, new CourseResource.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        JSONObject data = json.getJSONObject("data");

                        Log.d("COURSE_LOAD", "Data Loaded: " + data.toString());

                        String title = data.optString("title", "");
                        String description = data.optString("description", "");
                        String body = data.optString("body", "");

                        etTitle.setText(title);
                        etDescription.setText(description);
                        editor.setHtml(body);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(EditCourse.this, "Gagal memuat data", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(EditCourse.this, "Terjadi kesalahan jaringan", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void submitEdit(View view) {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String body = editor.getHtml();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Title dan Deskripsi tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get ID From Session
        SharedPreferences sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);
        int accountID = sharedPreferences.getInt("id", 1);

        CourseResource.updateCourse(courseId, title, description, body, accountID, selectedImageUri, this, new CourseResource.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    Toast.makeText(EditCourse.this, "Materi berhasil diperbarui", Toast.LENGTH_SHORT).show();
//                    finish();
                });
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(EditCourse.this, "Gagal memperbarui materi", Toast.LENGTH_SHORT).show());
            }
        });
    }

    // RichEditor tools
    public void setBold(View view) {
        editor.setBold();
    }

    public void setItalic(View view) {
        editor.setItalic();
    }

    public void setHeading1(View view) {
        editor.setHeading(1);
    }

    public void setUnorderedList(View view) {
        editor.setBullets();
    }
}
