package com.example.quizwithfisheryates.userActivities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.quizwithfisheryates.R;
import com.example.quizwithfisheryates._apiResources.QuizResource;
import com.example.quizwithfisheryates._models.Option;
import com.example.quizwithfisheryates._models.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //
    String difficulty;
    TextView currentQuestionNumber;
    Button btnA, btnB, btnC, btnD;
    private TextView textQuestion;
    private List<Question> questions = new ArrayList<>();
    private List<Option> options = new ArrayList<>();
    private int currentIndex = 0;

    private int score= 0;


    private CountDownTimer countDownTimer;
    private TextView timerTextView;
    private long timeLeftInMillis = 60000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.user_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ambil pilihan difficulty
        difficulty = getIntent().getStringExtra("DIFFICULTY");

        if(difficulty.equals("Easy")){
            timeLeftInMillis = 1200000;
        } else if(difficulty.equals("Medium")){
            timeLeftInMillis = 1200000;
        } else{
            timeLeftInMillis = 1200000;
        }

        timerTextView = findViewById(R.id.timerTextView);
        startCountdown();

        // Text untuk nomor soal
        currentQuestionNumber = findViewById(R.id.currentQuestionNumber);
        updateQuestionNumber();

        // Text untuk menampilkan soal
        textQuestion = findViewById(R.id.question);

        // Ambil soal dari database lewat resource
        getQuestions();

        // Untuk proses pilih jawaban
        btnA = findViewById(R.id.btnOptionA);
        btnB = findViewById(R.id.btnOptionB);
        btnC = findViewById(R.id.btnOptionC);
        btnD = findViewById(R.id.btnOptionD);

        View.OnClickListener optionClickListener = v -> {
            Button clickedButton = (Button) v;
            String selectedAnswer = clickedButton.getText().toString();

            Log.d("SELECTED ANSWER", selectedAnswer);
            String cleanedAnswer= selectedAnswer.substring(3);

            if(cleanedAnswer.equals(questions.get(currentIndex).getAnswer())){
                score += 10;
                Log.d("SCORE", Integer.toString(score));
            }

            // Lanjut ke soal berikutnya
            nextQuestion();
        };

        btnA.setOnClickListener(optionClickListener);
        btnB.setOnClickListener(optionClickListener);
        btnC.setOnClickListener(optionClickListener);
        btnD.setOnClickListener(optionClickListener);
    }

    @SuppressWarnings("MissingSuperCall")
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi")
                .setMessage("Anda belum menyelesaikan Soal!")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("Tidak", null)
                .show();
    }


    private void startCountdown() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                // Waktu habis -> pindah ke ScoreActivity
                Intent intent = new Intent(MainActivity.this, ScoreActivity.class);
                intent.putExtra("SCORE", score);
                intent.putExtra("DIFFICULTY", difficulty);
                startActivity(intent);
                finish(); // agar activity ini tidak bisa kembali dengan tombol back
            }
        }.start();
    }

    private void updateTimerText() {
        int seconds = (int) (timeLeftInMillis / 1000);
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;

        if (seconds >= 60) {
            timerTextView.setText("Sisa waktu: " + minutes + " menit " + String.format("%02d", remainingSeconds) + " detik");
        } else {
            timerTextView.setText("Sisa waktu: " + remainingSeconds + " detik");
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void showQuestion(int index) {
        Question question = questions.get(index);

        //
        ImageView imageView = findViewById(R.id.imageView);

        if (question.getImage() != null && !question.getImage().isEmpty()) {
            imageView.setVisibility(View.VISIBLE);

            Glide.with(this)
                    .load(question.getImage())
                    .into(imageView);
        } else {
            imageView.setVisibility(View.GONE);
            Glide.with(this).clear(imageView);
        }

        textQuestion.setText(question.getQuestion());

        getOptions(question.getId());
        showOption();

        Log.d("OPTIONS_LIST", options.toString());
    }

    private void showOption() {
        if (options.size() >= 4) {
            btnA.setText("A. " + options.get(0).getValue());
            btnA.setTag(options.get(0));

            btnB.setText("B. " + options.get(1).getValue());
            btnB.setTag(options.get(1));

            btnC.setText("C. " + options.get(2).getValue());
            btnC.setTag(options.get(2));

            btnD.setText("D. " + options.get(3).getValue());
            btnD.setTag(options.get(3));
        }
    }

    private void nextQuestion() {
        currentIndex++;
        if (currentIndex < questions.size()) {
            showQuestion(currentIndex); // update UI untuk soal baru
            updateQuestionNumber();
        } else {
            Toast.makeText(this, "Semua soal selesai!", Toast.LENGTH_SHORT).show();

            countDownTimer.cancel();

            Intent intent = new Intent(
                    MainActivity.this,
                    ScoreActivity.class
            );
            intent.putExtra("SCORE", score);
            intent.putExtra("DIFFICULTY", difficulty);
            startActivity(intent);
        }
    }

    private void updateQuestionNumber(){
//        currentQuestionNumber.setText("Soal No. " + (currentIndex + 1));

        int questionCountDown = questions.toArray().length - currentIndex;

        Log.d("SOAL", String.valueOf(questionCountDown));

        if(questionCountDown <= 0) {
            currentQuestionNumber.setText("");
        } else {
            currentQuestionNumber.setText("Sisa Soal " + questionCountDown);
        }
    }

    public void getQuestions(){
        QuizResource.getQuestion(difficulty, "user", new QuizResource.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    String status = json.getString("status");

                    if (status.equals("success")) {
                        JSONArray dataArray = json.getJSONArray("data");

                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject obj = dataArray.getJSONObject(i);
                            int id = obj.getInt("id");
                            String questionText = obj.getString("question");
                            String image = obj.isNull("image") ? null : obj.getString("image");
                            String difficulty = obj.getString("difficulty");
                            String answer = obj.getString("answer");

                            questions.add(new Question(id, questionText, image, difficulty, answer));
                        }

                        // Update UI di thread utama
                        runOnUiThread(() -> showQuestion(currentIndex));

                    } else {
                        runOnUiThread(() -> Toast.makeText(
                                MainActivity.this,
                                "Gagal mengambil data soal",
                                Toast.LENGTH_SHORT
                        ).show());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(
                            MainActivity.this,
                            "Format data soal salah",
                            Toast.LENGTH_SHORT
                    ).show());
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(
                        MainActivity.this,
                        "Gagal terhubung ke server",
                        Toast.LENGTH_SHORT
                ).show());
            }
        });
    }

    public void getOptions(int question_id){
        QuizResource.getOption(question_id, new QuizResource.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    String status = json.getString("status");

                    if (status.equals("success")) {
                        JSONArray dataArray = json.getJSONArray("data");

                        options.clear();

                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject obj = dataArray.getJSONObject(i);
                            int id = obj.getInt("id");
                            String optionValue = obj.getString("value");
                            int questionId = obj.getInt("question_id");

                            options.add(new Option(id, questionId, optionValue));
                        }

                        showOption();
                    } else {
                        runOnUiThread(() -> Toast.makeText(
                                MainActivity.this,
                                "Gagal mengambil data opsi jawaban",
                                Toast.LENGTH_SHORT
                        ).show());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(
                            MainActivity.this,
                            "Format data opsi jawaban salah",
                            Toast.LENGTH_SHORT
                    ).show());
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(
                        MainActivity.this,
                        "Gagal terhubung ke server",
                        Toast.LENGTH_SHORT
                ).show());
            }
        });
    }
}
