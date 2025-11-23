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
//import com.example.quizwithfisheryates._apiResources.CourseResource;
import com.example.quizwithfisheryates._models.Course;
import com.example.quizwithfisheryates.authActivities.LoginActivity;

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

    private ImageView imagePreview;
    private Button buttonUploadImage;
    private Button buttonUploadVoice;

    // VIDEO
    private Button buttonUploadVideo;
    private ImageView videoThumbnail;

    // Audio Control Elements
    private LinearLayout audioControlPanel;
    private TextView audioFileName;
    private Button buttonPlayPause;
    private Button buttonCancelAudio;
    private ProgressBar audioProgressBar;

    // Audio State
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
        setupClickListeners();
    }

    private void initializeViews() {
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        editor = findViewById(R.id.editor);
        btnSubmit = findViewById(R.id.btnSubmitCourse);

        imagePreview = findViewById(R.id.imagePreview);
        buttonUploadImage = findViewById(R.id.buttonUploadImage);

        buttonUploadVoice = findViewById(R.id.buttonUploadVoice);

        // VIDEO
//        buttonUploadVideo = findViewById(R.id.buttonUploadVideo);
//        videoThumbnail = findViewById(R.id.videoThumbnail);

        // Audio Controls
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

    private void setupClickListeners() {
        btnSubmit.setOnClickListener(v -> submitCourse());

        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        buttonUploadImage.setOnClickListener(v -> openImagePicker());
        buttonUploadVoice.setOnClickListener(v -> openAudioPicker());
        buttonUploadVideo.setOnClickListener(v -> openVideoPicker());

        buttonPlayPause.setOnClickListener(v -> togglePlayPause());
        buttonCancelAudio.setOnClickListener(v -> cancelAudio());
    }

    // ============================
    // PICKERS
    // ============================

    private void openAudioPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

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
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        startActivityForResult(Intent.createChooser(intent, "Select Video"), REQUEST_CODE_PICK_VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null || data.getData() == null) return;

        if (requestCode == REQUEST_CODE_PICK_IMAGE) handleImageSelection(data.getData());
        else if (requestCode == REQUEST_CODE_PICK_AUDIO) handleAudioSelection(data.getData());
        else if (requestCode == REQUEST_CODE_PICK_VIDEO) handleVideoSelection(data.getData());
    }

    // ============================
    // IMAGE
    // ============================

    private void handleImageSelection(Uri imageUri) {
        selectedImageUri = imageUri;

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            imagePreview.setImageBitmap(bitmap);
            imagePreview.setVisibility(View.VISIBLE);

            Toast.makeText(this, "Gambar berhasil dipilih", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Gagal memuat gambar", Toast.LENGTH_SHORT).show();
        }
    }

    // ============================
    // VIDEO
    // ============================

    private void handleVideoSelection(Uri videoUri) {
        selectedVideoUri = videoUri;

        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(this, videoUri);

            Bitmap thumbnail = retriever.getFrameAtTime(0);
            videoThumbnail.setImageBitmap(thumbnail);
            videoThumbnail.setVisibility(View.VISIBLE);

            buttonUploadVideo.setText("Ganti Video");

            Toast.makeText(this, "Video berhasil dipilih", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Gagal memuat video", Toast.LENGTH_SHORT).show();
        }
    }

    // ============================
    // AUDIO HANDLER
    // ============================

    private void handleAudioSelection(Uri audioUri) {
        selectedAudioUri = audioUri;

        releaseMediaPlayer();

        try {
            mediaPlayer = MediaPlayer.create(this, audioUri);

            if (mediaPlayer == null) {
                Toast.makeText(this, "Gagal memuat audio", Toast.LENGTH_SHORT).show();
                return;
            }

            setupMediaPlayerListeners();
            showAudioControls();
            updateAudioFileName(audioUri);

            Toast.makeText(this, "Audio berhasil dipilih", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "Gagal memuat audio", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupMediaPlayerListeners() {
        mediaPlayer.setOnCompletionListener(mp -> {
            isPlaying = false;
            buttonPlayPause.setText("▶ Play");
            stopProgressUpdater();
            audioProgressBar.setProgress(0);
        });
    }

    private void showAudioControls() {
        audioControlPanel.setVisibility(View.VISIBLE);
        buttonUploadVoice.setText("Ganti Audio");

        isPlaying = false;
        buttonPlayPause.setText("▶ Play");
    }

    private void updateAudioFileName(Uri audioUri) {
        String fileName = audioUri.getLastPathSegment();
        audioFileName.setText(fileName != null ? fileName : "Audio Selected");
    }

    private void togglePlayPause() {
        if (mediaPlayer == null) return;

        if (isPlaying) {
            mediaPlayer.pause();
            isPlaying = false;
            buttonPlayPause.setText("▶ Play");
            stopProgressUpdater();
        } else {
            mediaPlayer.start();
            isPlaying = true;
            buttonPlayPause.setText("⏸ Pause");
            startProgressUpdater();
        }
    }

    private void cancelAudio() {
        releaseMediaPlayer();
        selectedAudioUri = null;

        audioControlPanel.setVisibility(View.GONE);
        buttonUploadVoice.setText("Upload Suara");

        Toast.makeText(this, "Audio dibatalkan", Toast.LENGTH_SHORT).show();
    }

    private void startProgressUpdater() {
        if (mediaPlayer == null) return;

        progressRunnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && isPlaying) {
                    int position = mediaPlayer.getCurrentPosition();
                    int duration = mediaPlayer.getDuration();

                    if (duration > 0) {
                        int progress = (int) ((position * 100L) / duration);
                        audioProgressBar.setProgress(progress);
                    }

                    progressHandler.postDelayed(this, 100);
                }
            }
        };

        progressHandler.post(progressRunnable);
    }

    private void stopProgressUpdater() {
        if (progressRunnable != null) {
            progressHandler.removeCallbacks(progressRunnable);
            progressRunnable = null;
        }
    }

    private void releaseMediaPlayer() {
        stopProgressUpdater();

        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) mediaPlayer.stop();
                mediaPlayer.release();
            } catch (Exception ignored) {}

            mediaPlayer = null;
            isPlaying = false;
        }
    }

    // ============================
    // SUBMIT COURSE
    // ============================

    public void submitCourse() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String bodyHtml = editor.getHtml();

        ProgressDialog progressDialog = new ProgressDialog(CreateCourse.this);
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false); // agar tidak bisa dibatalkan
        progressDialog.show(); // tampilkan loading

        if (title.isEmpty() || description.isEmpty() || bodyHtml == null || bodyHtml.isEmpty()) {
            Toast.makeText(this, "Semua field wajib diisi!", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);
        int accountID = sharedPreferences.getInt("id", 1);

        Course.postCourse(
                title,
                description,
                bodyHtml,
                accountID,
                selectedImageUri,
                selectedAudioUri,
                selectedVideoUri,
                this,
                new Course.ApiCallback() {
                    @Override
                    public void onSuccess(String response) {
                        progressDialog.dismiss();
                        runOnUiThread(() -> {
                            Toast.makeText(CreateCourse.this, "Kursus berhasil dibuat!", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    }

                    @Override
                    public void onError(Exception e) {

                        Log.d("TEST", e.getMessage());
                        progressDialog.dismiss();
                        runOnUiThread(() ->
                                Toast.makeText(CreateCourse.this, "Gagal membuat kursus: " + e.getMessage(), Toast.LENGTH_LONG).show()
                        );
                    }
                }
        );
    }
}
