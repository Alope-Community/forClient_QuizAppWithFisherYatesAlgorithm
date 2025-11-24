package com.example.quizwithfisheryates.adminActivities.courses;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quizwithfisheryates.R;
import com.example.quizwithfisheryates._models.Course;

import java.io.IOException;

import jp.wasabeef.richeditor.RichEditor;

public class CreateCourse extends AppCompatActivity {

    private EditText etTitle, etDescription;
    private RichEditor editor;
    private Button btnSubmit;

    private static final int REQUEST_CODE_PICK_IMAGE = 1001;
    private static final int REQUEST_CODE_PICK_AUDIO = 2002;
    private static final int REQUEST_CODE_PICK_VIDEO = 3003;

    private Uri selectedImageUri;
    private Uri selectedAudioUri;
    private Uri selectedVideoUri;

    private MediaPlayer mediaPlayer;

    // Image
    private ImageView imagePreview;
    private Button buttonUploadImage;

    // Audio
    private Button buttonUploadVoice;
    private LinearLayout audioControlPanel;
    private TextView audioFileName;
    private Button buttonPlayPause, buttonCancelAudio;
    private ProgressBar audioProgressBar;

    // Video
    private Button buttonUploadVideo;
    private ImageView videoThumbnail;

    private boolean isPlaying = false;
    private Handler progressHandler = new Handler();
    private Runnable progressRunnable;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_course_create);

        initializeViews();
        setupEditor();
        setupListeners();
    }

    private void initializeViews() {
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        editor = findViewById(R.id.editor);
        btnSubmit = findViewById(R.id.btnSubmitCourse);

        imagePreview = findViewById(R.id.imagePreview);
        buttonUploadImage = findViewById(R.id.buttonUploadImage);

        buttonUploadVoice = findViewById(R.id.buttonUploadVoice);

        buttonUploadVideo = findViewById(R.id.buttonUploadVideo);
        videoThumbnail = findViewById(R.id.videoThumbnail);

        audioControlPanel = findViewById(R.id.audioControlPanel);
        audioFileName = findViewById(R.id.audioFileName);
        buttonPlayPause = findViewById(R.id.buttonPlayPause);
        buttonCancelAudio = findViewById(R.id.buttonCancelAudio);
        audioProgressBar = findViewById(R.id.audioProgressBar);
    }

    private void setupEditor() {
        editor.setEditorHeight(200);
        editor.setPlaceholder("Write course content here...");
    }

    private void setupListeners() {
        btnSubmit.setOnClickListener(v -> submitCourse());
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        buttonUploadImage.setOnClickListener(v -> openImagePicker());
        buttonUploadVoice.setOnClickListener(v -> openAudioPicker());
        buttonUploadVideo.setOnClickListener(v -> openVideoPicker());

        buttonPlayPause.setOnClickListener(v -> togglePlayPause());
        buttonCancelAudio.setOnClickListener(v -> cancelAudio());
    }

    // ============= PICKERS =============

    private void openAudioPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(Intent.createChooser(intent, "Select Audio"), REQUEST_CODE_PICK_AUDIO);
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    private void openVideoPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        startActivityForResult(Intent.createChooser(intent, "Select Video"), REQUEST_CODE_PICK_VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null) return;
        Uri uri = data.getData();
        if (uri == null) return;

        switch (requestCode) {
            case REQUEST_CODE_PICK_IMAGE: handleImage(uri); break;
            case REQUEST_CODE_PICK_AUDIO: handleAudio(uri); break;
            case REQUEST_CODE_PICK_VIDEO: handleVideo(uri); break;
        }
    }

    // ============= IMAGE =============

    private void handleImage(Uri imageUri) {
        selectedImageUri = imageUri;
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            imagePreview.setImageBitmap(bitmap);
            imagePreview.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            Toast.makeText(this, "Gagal memuat gambar", Toast.LENGTH_SHORT).show();
        }
    }

    // ============= VIDEO =============

    private void handleVideo(Uri videoUri) {
        selectedVideoUri = videoUri;

        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(this, videoUri);
            Bitmap thumb = retriever.getFrameAtTime(0);

            videoThumbnail.setImageBitmap(thumb);
            videoThumbnail.setVisibility(View.VISIBLE);
            buttonUploadVideo.setText("Ganti Video");

        } catch (Exception e) {
            Toast.makeText(this, "Gagal memuat video", Toast.LENGTH_SHORT).show();
        }
    }

    // ============= AUDIO =============

    private void handleAudio(Uri audioUri) {
        selectedAudioUri = audioUri;
        releaseMediaPlayer();

        try {
            mediaPlayer = MediaPlayer.create(this, audioUri);
            if (mediaPlayer == null) {
                Toast.makeText(this, "Gagal load audio", Toast.LENGTH_SHORT).show();
                return;
            }

            setupAudioUI(audioUri);

        } catch (Exception e) {
            Toast.makeText(this, "Gagal memuat audio", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupAudioUI(Uri audioUri) {
        audioControlPanel.setVisibility(View.VISIBLE);
        buttonUploadVoice.setText("Ganti Audio");

        String name = audioUri.getLastPathSegment();
        audioFileName.setText(name != null ? name : "Audio Selected");

        audioProgressBar.setVisibility(View.VISIBLE);
        audioProgressBar.setProgress(0);

        mediaPlayer.setOnCompletionListener(mp -> {
            isPlaying = false;
            buttonPlayPause.setText("▶ Play");
            stopProgressUpdater();
            audioProgressBar.setProgress(0);
        });
    }

    private void togglePlayPause() {
        if (mediaPlayer == null) return;

        if (isPlaying) {
            mediaPlayer.pause();
            buttonPlayPause.setText("▶ Play");
            isPlaying = false;
            stopProgressUpdater();
        } else {
            mediaPlayer.start();
            buttonPlayPause.setText("⏸ Pause");
            isPlaying = true;
            startProgressUpdater();
        }
    }

    private void cancelAudio() {
        selectedAudioUri = null;
        releaseMediaPlayer();
        audioControlPanel.setVisibility(View.GONE);
        buttonUploadVoice.setText("Upload Suara");
        Toast.makeText(this, "Audio dibatalkan", Toast.LENGTH_SHORT).show();
    }

    private void startProgressUpdater() {
        progressRunnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && isPlaying) {
                    int pos = mediaPlayer.getCurrentPosition();
                    int dur = mediaPlayer.getDuration();
                    if (dur > 0) {
                        audioProgressBar.setProgress((int) ((pos * 100L) / dur));
                    }
                    progressHandler.postDelayed(this, 100);
                }
            }
        };
        progressHandler.post(progressRunnable);
    }

    private void stopProgressUpdater() {
        if (progressRunnable != null) progressHandler.removeCallbacks(progressRunnable);
    }

    private void releaseMediaPlayer() {
        stopProgressUpdater();
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            } catch (Exception ignored) {}
            mediaPlayer.release();
        }
        mediaPlayer = null;
        isPlaying = false;
    }

    // ============= SUBMIT COURSE =============

    public void submitCourse() {
        String title = etTitle.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();
        String html = editor.getHtml();

        if (title.isEmpty() || desc.isEmpty() || html == null || html.isEmpty()) {
            Toast.makeText(this, "Semua field wajib diisi!", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("Mengirim data...");
        progress.setCancelable(false);
        progress.show();

        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        int accountID = prefs.getInt("id", 1);

        Course.postCourse(
                title, desc, html, accountID,
                selectedImageUri, selectedAudioUri, selectedVideoUri,
                this,
                new Course.ApiCallback() {
                    @Override
                    public void onSuccess(String res) {
                        progress.dismiss();
                        runOnUiThread(() -> {
                            Toast.makeText(CreateCourse.this, "Kursus berhasil dibuat!", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        progress.dismiss();

                        Log.d("TEST", e.getMessage());

                        runOnUiThread(() ->
                                Toast.makeText(CreateCourse.this, e.getMessage(), Toast.LENGTH_LONG).show()
                        );
                    }
                }
        );
    }
}
