package com.example.quizwithfisheryates.userActivities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import java.util.ArrayList;
import java.util.List;

public class CourseIndexActivity extends AppCompatActivity {

    List<Course> courseList = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_course_index);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.user_course_index), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getCourses();
    }

    private void getCourses() {
        CourseResource.getCourse(new CourseResource.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d("LEADERBOARD_RESPONSE", response); // <-- Cetak response di Logcat
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
                            String description = obj.getString("description");
                            String body = obj.getString("body");
                            int account_id = obj.getInt("account_id");
                            String created_at = obj.getString("created_at");
                            String account_name = obj.getString("account_name");

                            courseList.add(new Course(id, title, description, body, created_at, account_id));
                        }

                        runOnUiThread(() -> renderListToView());

                    } else {
                        runOnUiThread(() -> Toast.makeText(
                                CourseIndexActivity.this,
                                "Gagal mengambil data Materi",
                                Toast.LENGTH_SHORT
                        ).show());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(
                            CourseIndexActivity.this,
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
                        CourseIndexActivity.this,
                        "Gagal terhubung ke server",
                        Toast.LENGTH_SHORT
                ).show());
            }
        });
    }

    void renderListToView(){
        LinearLayout container = findViewById(R.id.container); // dari activity XML
        container.removeAllViews();

        for (Course item : courseList) {
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(20, 20, 20, 20);
            layout.setClickable(true); // penting untuk bisa diklik
            layout.setBackgroundResource(R.drawable.primary_color);

            TextView tvName = new TextView(this);
            tvName.setText(item.getTitle());
            tvName.setTextSize(16);
            tvName.setTypeface(null, Typeface.BOLD);

            TextView tvDescription = new TextView(this);
            tvDescription.setText(item.getDescription());

            layout.addView(tvName);
            layout.addView(tvDescription);

            // Kirim ID ketika di klik
            layout.setOnClickListener(v -> {
                Intent intent = new Intent(this, CourseShowActivity.class);
                intent.putExtra("course_id", item.getID()); // pastikan item.getId() return String atau int
                startActivity(intent);
            });

            View line = new View(this);
            line.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 2));
            line.setBackgroundColor(Color.LTGRAY);

            container.addView(layout);
            container.addView(line);
        }
    }

}
