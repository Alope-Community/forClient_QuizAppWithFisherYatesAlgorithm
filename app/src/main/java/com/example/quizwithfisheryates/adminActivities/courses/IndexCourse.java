package com.example.quizwithfisheryates.adminActivities.courses;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.quizwithfisheryates.R;
import com.example.quizwithfisheryates._apiResources.CourseResource;
import com.example.quizwithfisheryates._models.Course;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class IndexCourse extends AppCompatActivity {

    List<Course> courseList = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_course_index);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.user_admin_index), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getCourses();

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    protected void onResume() {
        super.onResume();
        getCourses();
    }

    public void goToCreateCourse(View view){
        Intent intent = new Intent(IndexCourse.this, CreateCourse.class);
        startActivity(intent);
    }

    private void getCourses() {
        CourseResource.getCourse(new CourseResource.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d("LEADERBOARD_RESPONSE", response);
                try {
                    JSONObject json = new JSONObject(response);
                    String status = json.getString("status");

                    if (status.equals("success")) {
                        JSONArray dataArray = json.getJSONArray("data");

                        courseList.clear();

                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject obj = dataArray.getJSONObject(i);

                            int id = obj.getInt("id");
                            String title = obj.getString("title");
                            String cover = obj.getString("cover");
                            String description = obj.getString("description");
                            String body = obj.getString("body");
                            int account_id = obj.getInt("account_id");
                            String created_at = obj.getString("created_at");
                            String account_name = obj.getString("account_name");

                            courseList.add(new Course(id, title, cover, description, body, created_at, account_id));
                        }

                        runOnUiThread(() -> renderListToView());

                    } else {
                        runOnUiThread(() -> Toast.makeText(
                                IndexCourse.this,
                                "Gagal mengambil data Materi",
                                Toast.LENGTH_SHORT
                        ).show());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(
                            IndexCourse.this,
                            "Format data materi salah",
                            Toast.LENGTH_SHORT
                    ).show());
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("LEADERBOARD_ERROR", "Error saat mengambil materi: " + e.getMessage());
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(
                        IndexCourse.this,
                        "Gagal terhubung ke server",
                        Toast.LENGTH_SHORT
                ).show());
            }
        });
    }

    void renderListToView() {
        LinearLayout container = findViewById(R.id.container);
        container.removeAllViews();

        for (Course item : courseList) {
            TextView tvMateri = new TextView(this);
            tvMateri.setText(item.getTitle());
            tvMateri.setTextSize(18);
            tvMateri.setTypeface(null, Typeface.BOLD);
            tvMateri.setTextColor(Color.BLACK);
            tvMateri.setPadding(20, 20, 20, 20);
            tvMateri.setBackgroundColor(Color.parseColor("#a4c9ff"));
            container.addView(tvMateri);

            // Tambahkan cover image
            if (item.getCover() != null || !item.getCover().trim().isEmpty()) {
                ImageView ivCover = new ImageView(this);
                LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        500
                );
                imageParams.setMargins(0, 0, 0, 0);
                ivCover.setLayoutParams(imageParams);
                ivCover.setScaleType(ImageView.ScaleType.CENTER_CROP);

                container.addView(ivCover);

                ivCover.setOnClickListener(v -> {
                    Intent intent = new Intent(this, ShowCourse.class);
                    intent.putExtra("course_id", item.getID());
                    startActivity(intent);
                });

                Glide.with(this)
                        .load(item.getCover())
                        .into(ivCover);
            }

            // Card layout
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(20, 20, 20, 20);
            layout.setBackgroundResource(R.drawable.primary_color);

            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            cardParams.setMargins(0, 0, 0, 24);
            layout.setLayoutParams(cardParams);

            TextView tvDescription = new TextView(this);
            tvDescription.setText(item.getDescription());

            layout.addView(tvDescription);

            // Tombol edit dan hapus
            LinearLayout buttonLayout = new LinearLayout(this);
            buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
            buttonLayout.setPadding(0, 16, 0, 0);

            ImageButton editButton = new ImageButton(this);
            editButton.setLayoutParams(new LinearLayout.LayoutParams(80, 80));
            editButton.setImageResource(R.drawable.ic_edit);
            editButton.setBackgroundColor(Color.parseColor("#2196F3")); // Biru
            editButton.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
            editButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, EditCourse.class);
                intent.putExtra("course_id", item.getID());
                startActivity(intent);
            });

            ImageButton deleteButton = new ImageButton(this);
            deleteButton.setLayoutParams(new LinearLayout.LayoutParams(80, 80));
            deleteButton.setImageResource(R.drawable.ic_delete);
            deleteButton.setBackgroundColor(Color.parseColor("#F44336")); // Merah
            deleteButton.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
            deleteButton.setOnClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setTitle("Konfirmasi Hapus")
                        .setMessage("Yakin ingin menghapus materi ini?")
                        .setPositiveButton("Hapus", (dialog, which) -> {
                            CourseResource.deleteCourse(item.getID(), new CourseResource.ApiCallback() {
                                @Override
                                public void onSuccess(String response) {
                                    runOnUiThread(() -> {
                                        Toast.makeText(IndexCourse.this, "Materi berhasil dihapus", Toast.LENGTH_SHORT).show();
                                        getCourses();
                                    });
                                }

                                @Override
                                public void onError(Exception e) {
                                    runOnUiThread(() -> Toast.makeText(IndexCourse.this, "Gagal menghapus materi", Toast.LENGTH_SHORT).show());
                                }
                            });
                        })
                        .setNegativeButton("Batal", null)
                        .show();
            });

            buttonLayout.addView(editButton);
            buttonLayout.addView(deleteButton);
            layout.addView(buttonLayout);

            container.addView(layout);

            layout.setOnClickListener(v -> {
                Intent intent = new Intent(this, ShowCourse.class);
                intent.putExtra("course_id", item.getID());
                startActivity(intent);
            });
        }
    }
}