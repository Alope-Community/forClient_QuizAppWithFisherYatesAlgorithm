package com.example.quizwithfisheryates.userActivities;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
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
import com.example.quizwithfisheryates._models.Course;
//import com.example.quizwithfisheryates._apiResources.CourseResource;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class CourseShowActivity extends AppCompatActivity {
    TextView tvTitle, tvDescription;
    WebView webBody, webVideo;

    ImageView ivCover;

    String bodyText;
    String audioUrl;
    private String videoUrl;

    AppCompatButton playTextToSpeech, stopTextToSpeech;

    private TextToSpeech tts;
    private MediaPlayer mediaPlayer; // <== MediaPlayer

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
        webVideo = findViewById(R.id.webVideo);  // <<< VIDEO BOX

        playTextToSpeech = findViewById(R.id.txPlayTTS);
        stopTextToSpeech = findViewById(R.id.txStopTTS);

        stopTextToSpeech.setVisibility(View.GONE);

        int courseId = getIntent().getIntExtra("course_id", 1);
        showCourseDetail(courseId);

        findViewById(R.id.backButton).setOnClickListener(v -> {
            if (tts != null) tts.stop();
            if (mediaPlayer != null) mediaPlayer.stop();
            finish();
        });

        // Inisialisasi TTS
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
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

        if (audioUrl == null || audioUrl.isEmpty() || audioUrl.equals("null")) {
            // Play TTS
            tts.speak(bodyText, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            // Play audio dari URL
            try {
                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setAudioAttributes(
                            new AudioAttributes.Builder()
                                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                    .setUsage(AudioAttributes.USAGE_MEDIA)
                                    .build()
                    );
                    mediaPlayer.setDataSource(audioUrl);
                    mediaPlayer.prepareAsync();
                    mediaPlayer.setOnPreparedListener(MediaPlayer::start);
                } else {
                    mediaPlayer.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Gagal memutar audio", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void stopTextToSpeech(View view) {
        playTextToSpeech.setVisibility(View.VISIBLE);
        stopTextToSpeech.setVisibility(View.GONE);

        if (audioUrl == null || audioUrl.isEmpty() || audioUrl.equals("null")) {
            // Stop TTS
            if (tts != null) {
                tts.stop();
            }
        } else {
            // Stop MediaPlayer
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }
    }

    // -----------------------------
    // LOAD MP4 VIDEO
    // -----------------------------

    private void loadVideoPlayer(String url) {
        webVideo.getSettings().setJavaScriptEnabled(true);
        webVideo.getSettings().setDomStorageEnabled(true);

        String html =
                "<video width=\"100%\" height=\"220\" controls>" +
                        "<source src=\"" + url + "\" type=\"video/mp4\">" +
                        "Browser tidak mendukung video." +
                        "</video>";

        webVideo.loadData(html, "text/html", "UTF-8");
    }

    private void showCourseDetail(int id) {
        Course.showCourse(id, new Course.ApiCallback() {
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
                        String audio = obj.optString("audio", null);
                        videoUrl = obj.optString("video", null); // <<< VIDEO URL

                        audioUrl = audio; // simpan audio dari API

                        // ubah HTML jadi plain text
                        Spanned spanned = Html.fromHtml(body, Html.FROM_HTML_MODE_LEGACY);
                        String plainText = spanned.toString();
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
                                ivCover.setVisibility(View.GONE);
                            }

                            // ==== KONDISI VIDEO ====
                            if (videoUrl != null && !videoUrl.isEmpty() && !videoUrl.equals("null")) {
                                webVideo.setVisibility(View.VISIBLE);
                                loadVideoPlayer(videoUrl);
                            } else {
                                webVideo.setVisibility(View.GONE);
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
        if (tts != null) tts.stop();
        if (mediaPlayer != null) mediaPlayer.stop();
        Intent intent = new Intent(CourseShowActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
