package com.example.quizwithfisheryates._apiResources;

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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ScoreResource {

    public interface ApiCallback {
        void onSuccess(String response);
        void onError(Exception e);
    }

    public interface FileCallback {
        void onSuccess(String filePath);
        void onError(Exception e);
    }

    public static void getScore(String difficulty, String scoreType, String forRole, int accountId, ScoreResource.ApiCallback callback) {
        new Thread(() -> {
            try {
                // Buat URL dengan parameter GET
                String baseUrl = "https://quiz.alope.id/scores";
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

    public static void postScore(int accountId, String difficulty, int score, ScoreResource.ApiCallback callback) {
        new Thread(() -> {
            try {
                URL url = new URL("https://quiz.alope.id/create-score");
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

    public static void exportScore(Context context, ScoreResource.FileCallback callback) {
        new Thread(() -> {
            try {
                String fileUrl = "https://quiz.alope.id/export-scores";
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
}
