package com.example.quizwithfisheryates.userActivities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.quizwithfisheryates.MainActivity;
import com.example.quizwithfisheryates.R;
import com.example.quizwithfisheryates._apiResources.CourseResource;
import com.example.quizwithfisheryates._models.Course;
import com.example.quizwithfisheryates.adminActivities.courses.ShowCourse;
import com.example.quizwithfisheryates.authActivities.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class CourseShowActivity extends AppCompatActivity {
    TextView tvTitle, tvDescription;
    WebView webBody;

    ImageView ivCover;

    String bodyText;

    AppCompatButton playTextToSpeech, stopTextToSpeech;

    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_course_show); // layout baru untuk detail

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.user_course_show), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        webBody = findViewById(R.id.webBody);
        ivCover = findViewById(R.id.ivCover);

        playTextToSpeech = findViewById(R.id.txPlayTTS);
        stopTextToSpeech = findViewById(R.id.txStopTTS);

        stopTextToSpeech.setVisibility(View.GONE);

        int courseId = getIntent().getIntExtra("course_id", 1);
        showCourseDetail(courseId);

        findViewById(R.id.backButton).setOnClickListener(v -> {
            tts.stop();
            finish();
        });

        // Inisialisasi TTS
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                // Set bahasa
                int result = tts.setLanguage(new Locale("id", "ID")); // bahasa Indonesia

                if (result == TextToSpeech.LANG_MISSING_DATA ||
                        result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Bahasa tidak didukung");
                }
            } else {
                Log.e("TTS", "Inisialisasi gagal");
            }
        });
    }
    public void playTextToSpeech(View view) {
        playTextToSpeech.setVisibility(View.GONE);
        stopTextToSpeech.setVisibility(View.VISIBLE);
        tts.speak(bodyText, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    public void stopTextToSpeech(View view) {
        playTextToSpeech.setVisibility(View.VISIBLE);
        stopTextToSpeech.setVisibility(View.GONE);
        tts.stop();
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

                        String title = obj.getString("title");
                        String description = obj.getString("description");
                        String body = obj.getString("body");
                        String cover = obj.optString("cover", null);

                        // ubah HTML jadi plain text
                        Spanned spanned = Html.fromHtml(body, Html.FROM_HTML_MODE_LEGACY);
                        String plainText = spanned.toString();

                        // pakai plainText untuk TTS
                        bodyText = plainText;

                        runOnUiThread(() -> {
                            tvTitle.setText(title);
                            tvDescription.setText(description);
                            webBody.loadDataWithBaseURL(null, body, "text/html", "UTF-8", null);

                            if (cover != null && !cover.isEmpty()) {
                                Glide.with(CourseShowActivity.this)
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
        runOnUiThread(() -> Toast.makeText(CourseShowActivity.this, message, Toast.LENGTH_SHORT).show());
    }

    public void goToUserMain(View view){
        tts.stop();
        Intent intent = new Intent(CourseShowActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
