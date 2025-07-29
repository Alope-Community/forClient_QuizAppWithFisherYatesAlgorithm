package com.example.quizwithfisheryates.adminActivities.courses;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.quizwithfisheryates.R;
import com.example.quizwithfisheryates._apiResources.CourseResource;
import com.example.quizwithfisheryates.adminActivities.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class ShowCourse extends AppCompatActivity {
    TextView tvTitle, tvDescription;
    WebView webBody;
    ImageView ivCover; // ⬅️ Tambahkan imageView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_course_show);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.admin_course_show), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        webBody = findViewById(R.id.webBody);
        ivCover = findViewById(R.id.ivCover);

        int courseId = getIntent().getIntExtra("course_id", 1);
        showCourseDetail(courseId);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    private void showCourseDetail(int id) {
        CourseResource.showCourse(id, new CourseResource.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    String status = json.getString("status");

                    if (status.equals("success")) {
                        JSONObject obj = json.getJSONObject("data");

                        int courseId = obj.getInt("id");
                        String title = obj.getString("title");
                        String description = obj.getString("description");
                        String body = obj.getString("body");
                        String cover = obj.optString("cover", null);

                        runOnUiThread(() -> {
                            tvTitle.setText(title);
                            tvDescription.setText(description);
                            webBody.loadDataWithBaseURL(null, body, "text/html", "UTF-8", null);

                            if (cover != null && !cover.isEmpty()) {
                                Glide.with(ShowCourse.this)
                                        .load(cover)
                                        .into(ivCover);
                            } else {
                                ivCover.setVisibility(View.GONE); // Sembunyikan jika tidak ada gambar
                            }
                        });

                    } else {
                        showToast("Gagal mengambil detail materi");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast("Format data detail salah");
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                showToast("Terjadi kesalahan saat mengambil data");
            }
        });
    }

    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(ShowCourse.this, message, Toast.LENGTH_SHORT).show());
    }

    public void goToAdminMain(View view){
        Intent intent = new Intent(ShowCourse.this, MainActivity.class);
        startActivity(intent);
    }
}
