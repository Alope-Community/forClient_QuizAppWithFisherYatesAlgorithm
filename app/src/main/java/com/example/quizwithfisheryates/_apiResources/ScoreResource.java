package com.example.quizwithfisheryates._apiResources;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ScoreResource {

    public interface ApiCallback {
        void onSuccess(String response);
        void onError(Exception e);
    }

    public static void getScore(String difficulty, ScoreResource.ApiCallback callback) {
        new Thread(() -> {
            try {
                // Buat URL dengan parameter GET
                String baseUrl = "https://quiz.alope.id/scores";
                String queryParams = "?difficulty=" + URLEncoder.encode(difficulty, "UTF-8");

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
}
