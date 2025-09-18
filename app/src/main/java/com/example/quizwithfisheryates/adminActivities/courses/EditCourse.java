package com.example.quizwithfisheryates.adminActivities.courses;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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
import com.example.quizwithfisheryates._apiResources.CourseResource;

import org.json.JSONObject;

import java.io.IOException;

import jp.wasabeef.richeditor.RichEditor;

public class EditCourse extends AppCompatActivity {

    private EditText etTitle, etDescription;
    private RichEditor editor;
    private int courseId;

    private static final int REQUEST_CODE_PICK_IMAGE = 1001;
    private static final int REQUEST_CODE_PICK_AUDIO = 2002;
    private Uri selectedImageUri;
    private Uri selectedAudioUri;
    private MediaPlayer mediaPlayer;
    private ImageView imagePreview;
    private Button buttonUploadImage;
    private Button buttonUploadVoice;

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
        setContentView(R.layout.activity_admin_course_edit);

        initializeViews();
        setupEditor();
        setupClickListeners();

        courseId = getIntent().getIntExtra("course_id", -1);
        loadCourse();
    }

    private void initializeViews() {
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        editor = findViewById(R.id.editor);
        imagePreview = findViewById(R.id.imagePreview);
        buttonUploadImage = findViewById(R.id.buttonUploadImage);
        buttonUploadVoice = findViewById(R.id.buttonUploadVoice);

        // Audio Control Views
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
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        findViewById(R.id.btnSubmitCourse).setOnClickListener(this::submitEdit);
        buttonUploadImage.setOnClickListener(v -> openImagePicker());
        buttonUploadVoice.setOnClickListener(v -> openAudioPicker());

        if (buttonPlayPause != null) {
            buttonPlayPause.setOnClickListener(v -> togglePlayPause());
        }
        if (buttonCancelAudio != null) {
            buttonCancelAudio.setOnClickListener(v -> cancelAudio());
        }
    }

    private void openAudioPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "Select Audio"), REQUEST_CODE_PICK_AUDIO);
        } catch (Exception e) {
            Log.e("AUDIO_PICKER", "Error opening audio picker", e);
            Toast.makeText(this, "Gagal membuka pemilih audio", Toast.LENGTH_SHORT).show();
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        try {
            startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
        } catch (Exception e) {
            Log.e("IMAGE_PICKER", "Error opening image picker", e);
            Toast.makeText(this, "Gagal membuka pemilih gambar", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == REQUEST_CODE_PICK_IMAGE) {
                handleImageSelection(data.getData());
            } else if (requestCode == REQUEST_CODE_PICK_AUDIO) {
                handleAudioSelection(data.getData());
            }
        }
    }

    private void handleImageSelection(Uri imageUri) {
        selectedImageUri = imageUri;
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
            imagePreview.setImageBitmap(bitmap);
            imagePreview.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Gambar berhasil dipilih", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e("IMAGE_SELECTION", "Error loading image", e);
            Toast.makeText(this, "Gagal memuat gambar", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleAudioSelection(Uri audioUri) {
        selectedAudioUri = audioUri;

        // Release previous MediaPlayer
        releaseMediaPlayer();

        try {
            mediaPlayer = MediaPlayer.create(this, selectedAudioUri);

            if (mediaPlayer != null) {
                setupMediaPlayerListeners();
                showAudioControls();
                updateAudioFileName(audioUri);
                Toast.makeText(this, "Audio berhasil dipilih", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Gagal memuat file audio", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("AUDIO_SELECTION", "Error handling audio selection", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupMediaPlayerListeners() {
        mediaPlayer.setOnCompletionListener(mp -> {
            isPlaying = false;
            buttonPlayPause.setText("▶ Play");
            stopProgressUpdater();
            audioProgressBar.setProgress(0);
        });

        mediaPlayer.setOnErrorListener((mp, what, extra) -> {
            Log.e("MEDIA_PLAYER", "MediaPlayer error: " + what);
            releaseMediaPlayer();
            hideAudioControls();
            return true;
        });
    }

    private void showAudioControls() {
        if (audioControlPanel != null) {
            audioControlPanel.setVisibility(View.VISIBLE);
            buttonUploadVoice.setText("Ganti Audio");
            isPlaying = false;
            buttonPlayPause.setText("▶ Play");
            audioProgressBar.setProgress(0);
        }
    }

    private void hideAudioControls() {
        if (audioControlPanel != null) {
            audioControlPanel.setVisibility(View.GONE);
            buttonUploadVoice.setText("Upload Suara");
            stopProgressUpdater();
        }
    }

    private void updateAudioFileName(Uri audioUri) {
        if (audioFileName == null) return;

        String fileName = "Audio Selected";

        // Try to get actual filename
        try {
            String path = audioUri.getLastPathSegment();
            if (path != null && path.contains("/")) {
                fileName = path.substring(path.lastIndexOf("/") + 1);
            }
        } catch (Exception e) {
            Log.w("AUDIO_FILENAME", "Could not extract filename", e);
        }

        audioFileName.setText(fileName);
    }

    private void togglePlayPause() {
        if (mediaPlayer == null) return;

        try {
            if (isPlaying) {
                // Pause
                mediaPlayer.pause();
                isPlaying = false;
                buttonPlayPause.setText("▶ Play");
                stopProgressUpdater();
            } else {
                // Play
                mediaPlayer.start();
                isPlaying = true;
                buttonPlayPause.setText("⏸ Pause");
                startProgressUpdater();
            }
        } catch (Exception e) {
            Log.e("MEDIA_PLAYER", "Error toggling play/pause", e);
            Toast.makeText(this, "Error memutar audio", Toast.LENGTH_SHORT).show();
        }
    }

    private void cancelAudio() {
        releaseMediaPlayer();
        selectedAudioUri = null;
        hideAudioControls();
        Toast.makeText(this, "Audio dibatalkan", Toast.LENGTH_SHORT).show();
    }

    private void startProgressUpdater() {
        if (mediaPlayer == null || audioProgressBar == null) return;

        audioProgressBar.setVisibility(View.VISIBLE);
        progressRunnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && isPlaying) {
                    try {
                        int currentPosition = mediaPlayer.getCurrentPosition();
                        int duration = mediaPlayer.getDuration();

                        if (duration > 0) {
                            int progress = (int) ((currentPosition * 100L) / duration);
                            audioProgressBar.setProgress(progress);
                        }

                        progressHandler.postDelayed(this, 100);
                    } catch (Exception e) {
                        Log.e("PROGRESS_UPDATER", "Error updating progress", e);
                    }
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
        if (audioProgressBar != null) {
            audioProgressBar.setVisibility(View.GONE);
        }
    }

    private void releaseMediaPlayer() {
        stopProgressUpdater();

        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
            } catch (Exception e) {
                Log.e("MEDIA_PLAYER", "Error releasing MediaPlayer", e);
            } finally {
                mediaPlayer = null;
                isPlaying = false;
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

        CourseResource.updateCourse(courseId, title, description, body, accountID, selectedImageUri, selectedAudioUri, this, new CourseResource.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    Toast.makeText(EditCourse.this, "Materi berhasil diperbarui", Toast.LENGTH_SHORT).show();
                    // finish(); // Uncomment if you want to close after success
                });
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(EditCourse.this, "Gagal memperbarui materi", Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && isPlaying) {
            togglePlayPause(); // Pause when activity is paused
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }

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