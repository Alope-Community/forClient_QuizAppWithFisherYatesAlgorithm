package com.example.quizwithfisheryates.adminActivities;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quizwithfisheryates.R;
import com.example.quizwithfisheryates._apiResources.CourseResource;
import com.example.quizwithfisheryates._models.Course;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ShowCourseActivity extends AppCompatActivity {
    TextView tvTitle, tvDescription;
    WebView webBody; // untuk menampilkan isi HTML

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

        int courseId = getIntent().getIntExtra("course_id", 1);
        showCourseDetail(courseId);
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

                        runOnUiThread(() -> {
                            tvTitle.setText(title);
                            tvDescription.setText(description);
                            webBody.loadDataWithBaseURL(null, body, "text/html", "UTF-8", null);
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
        runOnUiThread(() -> Toast.makeText(ShowCourseActivity.this, message, Toast.LENGTH_SHORT).show());
    }
}
