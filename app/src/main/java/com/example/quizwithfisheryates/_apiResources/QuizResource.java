package com.example.quizwithfisheryates._apiResources;

import android.content.Context;
import android.net.Uri;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class QuizResource {

    public interface ApiCallback {
        void onSuccess(String response);
        void onError(Exception e);
    }

    public static void getQuestion(String difficulty, String listFor, ApiCallback callback) {
        new Thread(() -> {
            try {
                // Buat URL dengan parameter GET
                String baseUrl = "https://quiz.alope.id/questions";
                String queryParams = "?difficulty=" + URLEncoder.encode(difficulty, "UTF-8") +
                        "&for=" + URLEncoder.encode(listFor, "UTF-8");

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

    public static void getOption(int question_id, ApiCallback callback) {
        new Thread(() -> {
            try {
                // Buat URL dengan parameter GET
                String baseUrl = "https://quiz.alope.id/options";
                String queryParams = "?question_id=" + URLEncoder.encode(Integer.toString(question_id), "UTF-8");

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

    public static void postQuestion(
            String question,
            Uri imageUri,
            String difficulty,
            String answer,
            Context context,
            ApiCallback callback
    ) {
        new Thread(() -> {
            String boundary = "===" + System.currentTimeMillis() + "===";
            String LINE_FEED = "\r\n";

            try {
                URL url = new URL("https://quiz.alope.id/create-question");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setUseCaches(false);
                conn.setDoOutput(true); // untuk POST
                conn.setDoInput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                OutputStream outputStream = conn.getOutputStream();
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true);

                // -- Kirim field question
                writer.append("--").append(boundary).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"question\"").append(LINE_FEED);
                writer.append("Content-Type: text/plain; charset=UTF-8").append(LINE_FEED);
                writer.append(LINE_FEED);
                writer.append(question).append(LINE_FEED);
                writer.flush();

                // -- Kirim field difficulty
                writer.append("--").append(boundary).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"difficulty\"").append(LINE_FEED);
                writer.append("Content-Type: text/plain; charset=UTF-8").append(LINE_FEED);
                writer.append(LINE_FEED);
                writer.append(difficulty).append(LINE_FEED);
                writer.flush();

                // -- Kirim field answer
                writer.append("--").append(boundary).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"answer\"").append(LINE_FEED);
                writer.append("Content-Type: text/plain; charset=UTF-8").append(LINE_FEED);
                writer.append(LINE_FEED);
                writer.append(answer).append(LINE_FEED);
                writer.flush();

                // -- Kirim file gambar kalau ada
                if (imageUri != null) {
                    // Ambil filename dari Uri
                    String fileName = "image.jpg";

                    // Jika ingin dapat nama file asli bisa di-custom dengan ContentResolver, tapi ini sederhana:
                    // Contoh ambil nama file bisa pakai helper (optional)

                    writer.append("--").append(boundary).append(LINE_FEED);
                    writer.append("Content-Disposition: form-data; name=\"image\"; filename=\"" + fileName + "\"").append(LINE_FEED);
                    writer.append("Content-Type: image/jpeg").append(LINE_FEED);
                    writer.append(LINE_FEED);
                    writer.flush();

                    // Baca input stream dari Uri dan tulis ke outputStream
                    InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    outputStream.flush();
                    inputStream.close();

                    writer.append(LINE_FEED);
                    writer.flush();
                }

                // Akhiri multipart/form-data
                writer.append("--").append(boundary).append("--").append(LINE_FEED);
                writer.close();

                // Baca response
                int status = conn.getResponseCode();
                InputStream responseStream = (status == HttpURLConnection.HTTP_OK) ?
                        conn.getInputStream() : conn.getErrorStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(responseStream));
                StringBuilder responseStrBuilder = new StringBuilder();

                String inputStr;
                while ((inputStr = reader.readLine()) != null) {
                    responseStrBuilder.append(inputStr);
                }

                reader.close();
                conn.disconnect();

                if (status == HttpURLConnection.HTTP_OK) {
                    callback.onSuccess(responseStrBuilder.toString());
                } else {
                    callback.onError(new Exception("Server returned status: " + status + ", response: " + responseStrBuilder.toString()));
                }

            } catch (Exception e) {
                callback.onError(e);
            }
        }).start();
    }

    public static void postOption(int question_id, String value, ApiCallback callback) {
        new Thread(() -> {
            try {
                URL url = new URL("https://quiz.alope.id/create-option");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                String postData =
                        "question_id=" + question_id +
                        "&value=" + value;

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
