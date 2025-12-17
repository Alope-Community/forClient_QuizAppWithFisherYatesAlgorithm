package com.example.quizwithfisheryates._models;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Score {
    private String name;

    private String nisn;
    private String difficulty;
    private String createdAt;
    private int score;

    public Score(String name, String nisn, String difficulty, String createdAt, int score) {
        this.name = name;
        this.nisn = nisn;
        this.difficulty = difficulty;
        this.createdAt = createdAt;
        this.score = score;
    }


    //
    public interface ApiCallback {
        void onSuccess(String response);
        void onError(Exception e);
    }

    public interface FileCallback {
        void onSuccess(String filePath);
        void onError(Exception e);
    }

    public static void getScore(String difficulty, String scoreType, String forRole, int accountId, Score.ApiCallback callback) {
        new Thread(() -> {
            try {
                // Buat URL dengan parameter GET
                String baseUrl = "https://rizkypurnama.com/scores";
                String queryParams = "?difficulty=" + URLEncoder.encode(difficulty, "UTF-8") +
                        "&type=" + URLEncoder.encode(scoreType, "UTF-8") +
                        "&forRole=" + URLEncoder.encode(forRole, "UTF-8") +
                        "&accountId=" + URLEncoder.encode(String.valueOf(accountId), "UTF-8");

                URL url = new URL(baseUrl + queryParams);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                // Baca respons
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                reader.close();
                is.close();
                conn.disconnect();

                callback.onSuccess(result.toString());

            } catch (Exception e) {
                callback.onError(e);
            }
        }).start();
    }

    public static void postScore(int accountId, String difficulty, int score, Score.ApiCallback callback) {
        new Thread(() -> {
            try {
                URL url = new URL("https://rizkypurnama.com/create-score");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                String postData =
                        "account_id=" + accountId +
                                "&difficulty=" + difficulty +
                                "&score=" + score;

                OutputStream os = conn.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                reader.close();
                is.close();
                conn.disconnect();

                callback.onSuccess(result.toString());

            } catch (Exception e) {
                callback.onError(e);
            }
        }).start();
    }

    public static void exportScore(Context context, Score.FileCallback callback) {
        new Thread(() -> {
            try {
                String fileUrl = "https://rizkypurnama.com/export-scores";
                URL url = new URL(fileUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                InputStream inputStream = conn.getInputStream();

                // Format nama file dengan tanggal dan waktu
                String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault()).format(new Date());
                String fileName = "nilai_" + timestamp + ".xlsx";

                File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File file = new File(downloadsDir, fileName);

                FileOutputStream outputStream = new FileOutputStream(file);
                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                outputStream.close();
                inputStream.close();
                conn.disconnect();

                callback.onSuccess(file.getAbsolutePath());

            } catch (Exception e) {
                callback.onError(e);
            }
        }).start();
    }


    public String getName() {
        return name;
    }

    public String getNisn() {
        return nisn;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public int getScore() {
        return score;
    }
    public String getCreatedAt() {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date date = inputFormat.parse(createdAt);

            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return createdAt;
        }
    }
}
