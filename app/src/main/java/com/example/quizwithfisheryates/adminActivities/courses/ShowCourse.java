package com.example.quizwithfisheryates.adminActivities.courses;

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
import com.example.quizwithfisheryates.R;
import com.example.quizwithfisheryates._models.Course;
import com.example.quizwithfisheryates.adminActivities.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class ShowCourse extends AppCompatActivity {

    TextView tvTitle, tvDescription;
    WebView webBody, webVideo;
    ImageView ivCover;

    String bodyText;
    private String audioUrl;
    private String videoUrl;

    private MediaPlayer mediaPlayer;
    private TextToSpeech tts;

    AppCompatButton playTextToSpeech, stopTextToSpeech;

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
        webVideo = findViewById(R.id.webVideo);  // <<< VIDEO BOX
        ivCover = findViewById(R.id.ivCover);

        playTextToSpeech = findViewById(R.id.txPlayTTS);
        stopTextToSpeech = findViewById(R.id.txStopTTS);

        stopTextToSpeech.setVisibility(View.GONE);

        int courseId = getIntent().getIntExtra("course_id", 1);
        showCourseDetail(courseId);

        findViewById(R.id.backButton).setOnClickListener(v -> {
            if (tts != null) tts.stop();
            finish();
        });

        // TTS Init
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(new Locale("id", "ID"));
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
                    Log.e("TTS", "Bahasa tidak didukung");
            } else {
                Log.e("TTS", "Inisialisasi gagal");
            }
        });
    }

    // -----------------------------
    // PLAY AUDIO / TTS
    // -----------------------------

    public void playTextToSpeech(View view) {
        playTextToSpeech.setVisibility(View.GONE);
        stopTextToSpeech.setVisibility(View.VISIBLE);

        if (audioUrl == null || audioUrl.isEmpty() || audioUrl.equals("null")) {
            tts.speak(bodyText, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
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
            if (tts != null) tts.stop();
        } else {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) mediaPlayer.pause();
        }
    }

    // -----------------------------
    // LOAD COURSE DETAIL
    // -----------------------------

    private void showCourseDetail(int id) {

        Course.showCourse(id, new Course.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject json = new JSONObject(response);

                    if (!json.getString("status").equals("success")) {
                        showToast("Gagal mengambil detail");
                        return;
                    }

                    JSONObject obj = json.getJSONObject("data");

                    String title = obj.getString("title");
                    String description = obj.getString("description");
                    String body = obj.getString("body");
                    String cover = obj.optString("cover", null);
                    audioUrl = obj.optString("audio", null);
                    videoUrl = obj.optString("video", null); // <<< VIDEO URL

                    Spanned spanned = Html.fromHtml(body, Html.FROM_HTML_MODE_LEGACY);
                    bodyText = spanned.toString();

                    runOnUiThread(() -> {
                        tvTitle.setText(title);
                        tvDescription.setText(description);
                        webBody.loadDataWithBaseURL(null, body, "text/html", "UTF-8", null);

                        if (cover != null && !cover.isEmpty()) {
                            Glide.with(ShowCourse.this).load(cover).into(ivCover);
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

                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast("Kesalahan format data");
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                showToast("Terjadi kesalahan");
            }
        });
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

    // -----------------------------
    // HELPER
    // -----------------------------

    private void showToast(String msg) {
        runOnUiThread(() -> Toast.makeText(ShowCourse.this, msg, Toast.LENGTH_SHORT).show());
    }

    public void goToAdminMain(View view) {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        startActivity(new Intent(ShowCourse.this, MainActivity.class));
    }
}
