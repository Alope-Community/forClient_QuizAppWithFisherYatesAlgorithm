package com.example.quizwithfisheryates.adminActivities.quizzes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quizwithfisheryates.R;
//import com.example.quizwithfisheryates._apiResources.QuizResource;
import com.example.quizwithfisheryates._models.Option;
import com.example.quizwithfisheryates._models.Question;
import com.example.quizwithfisheryates.adminActivities.MainActivity;
import com.example.quizwithfisheryates.adminActivities.courses.ShowCourse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class CreateQuiz extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_IMAGE = 1001;
    private Uri selectedImageUri;
    private ImageView imagePreview;
    private Button buttonUploadImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_quiz_create);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.admin_create_quiz), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imagePreview = findViewById(R.id.imagePreview);
        buttonUploadImage = findViewById(R.id.buttonUploadImage);

        buttonUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                imagePreview.setImageBitmap(bitmap);
                imagePreview.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Gagal memuat gambar", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onSubmit(View view){
        EditText editTextQuestion = findViewById(R.id.editTextQuestion);
        EditText editTextOptionA = findViewById(R.id.editTextOptionA);
        EditText editTextOptionB = findViewById(R.id.editTextOptionB);
        EditText editTextOptionC = findViewById(R.id.editTextOptionC);
        EditText editTextOptionD = findViewById(R.id.editTextOptionD);
        RadioGroup radioGroupAnswer = findViewById(R.id.radioGroupAnswer);
        RadioGroup radioGroupDifficulty = findViewById(R.id.radioGroupDifficulty);

        String question = editTextQuestion.getText().toString().trim();
        String optionA = editTextOptionA.getText().toString().trim();
        String optionB = editTextOptionB.getText().toString().trim();
        String optionC = editTextOptionC.getText().toString().trim();
        String optionD = editTextOptionD.getText().toString().trim();

        // Ambil Answer
        int selectedRadioId = radioGroupAnswer.getCheckedRadioButtonId();
        RadioButton selectedRadio = findViewById(selectedRadioId);
        String correctAnswer = selectedRadio.getText().toString().trim();

        //
        String finalAnswer = optionA;

        if(correctAnswer.equals("B")){
            finalAnswer= optionB;
        } else if(correctAnswer.equals("C")){
            finalAnswer= optionC;
        } else if(correctAnswer.equals("D")){
            finalAnswer= optionD;
        }

        // Ambil Difficulty
        int selectedRadioDifficultyId = radioGroupDifficulty.getCheckedRadioButtonId();
        RadioButton selectedDifficulty = findViewById(selectedRadioDifficultyId);
        String finalDifficulty = selectedDifficulty.getText().toString().trim();

        Question.postQuestion(question, selectedImageUri, finalDifficulty, finalAnswer, CreateQuiz.this, new Question.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d("CREATE_SUCCESS", response);

                try {
                    JSONObject json = new JSONObject(response);
                    String status = json.getString("status");
                    String message = json.getString("message");
                    JSONObject data = json.getJSONObject("data");

                    int question_id = data.getInt("question_id");

                    // Insert Options
                    insertOption(question_id, optionA);
                    insertOption(question_id, optionB);
                    insertOption(question_id, optionC);
                    insertOption(question_id, optionD);

                    runOnUiThread(() -> {
                        Toast.makeText(CreateQuiz.this, message, Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(CreateQuizActivity.this, MainActivity.class));

                        editTextQuestion.setText("");
                        editTextOptionA.setText("");
                        editTextOptionB.setText("");
                        editTextOptionC.setText("");
                        editTextOptionD.setText("");

                        radioGroupAnswer.clearCheck();
                        radioGroupDifficulty.clearCheck();

                         selectedImageUri = null;
                    });
//                    finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(
                            CreateQuiz.this,
                            "Format response JSON salah",
                            Toast.LENGTH_SHORT
                    ).show());
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("CREATE_ERROR", "Tambah Gagal", e);
                runOnUiThread(() -> {
                    Toast.makeText(CreateQuiz.this, "Tambah gagal. Coba lagi.", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void insertOption(int questionId, String option) {
        Option.postOption(questionId, option, new Option.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d("CREATE_OPTION_SUCCESS", "Option berhasil ditambahkan");

//                runOnUiThread(() -> Toast.makeText(CreateQuizActivity.this, "Opsi " + optionLetter + " berhasil ditambahkan", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onError(Exception e) {
                Log.e("CREATE_OPTION_ERROR", "Tambah Opsi Gagal", e);
//                runOnUiThread(() -> {
//                    Toast.makeText(CreateQuizActivity.this, "Tambah opsi " + optionLetter + " gagal. Coba lagi.", Toast.LENGTH_SHORT).show();
//                });
            }
        });
    }

    public void goToAdminMain(View view){
        Intent intent = new Intent(CreateQuiz.this, MainActivity.class);
        startActivity(intent);
    }
}
