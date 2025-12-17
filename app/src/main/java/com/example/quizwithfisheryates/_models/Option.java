package com.example.quizwithfisheryates._models;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Option {
    private int id;
    private int question_id;
    private String value;

    public Option(int id, int question_id, String value) {
        this.id = id;
        this.question_id = question_id;
        this.value = value;
    }

    public interface ApiCallback {
        void onSuccess(String response);
        void onError(Exception e);
    }

    //
    public static void getOption(int question_id, ApiCallback callback) {
        new Thread(() -> {
            try {
                // Buat URL dengan parameter GET
                String baseUrl = "https://rizkypurnama.com/options";
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



    public static void updateOptions(int question_id, String optionA, String optionB, String optionC, String optionD, ApiCallback callback) {
        new Thread(() -> {
            try {
                URL url = new URL("https://rizkypurnama.com/update-options");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String postData =
                        "question_id=" + question_id +
                                "&option_a=" + Uri.encode(optionA) +
                                "&option_b=" + Uri.encode(optionB) +
                                "&option_c=" + Uri.encode(optionC) +
                                "&option_d=" + Uri.encode(optionD);

                OutputStream os = conn.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                InputStream is;
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    is = conn.getInputStream();
                } else {
                    is = conn.getErrorStream();
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                reader.close();
                is.close();
                conn.disconnect();

                Log.d("TAG", "updateOptions: " + url);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    callback.onSuccess(result.toString());
                } else {
                    callback.onError(new Exception("HTTP " + responseCode + ": " + result.toString()));
                }

            } catch (Exception e) {
                callback.onError(e);
            }
        }).start();
    }




    public static void postOption(int question_id, String value, ApiCallback callback) {
        new Thread(() -> {
            try {
                URL url = new URL("https://rizkypurnama.com/create-option");
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

    public static void deleteOption(int option_id, ApiCallback callback) {
        new Thread(() -> {
            try {
                URL url = new URL("https://rizkypurnama.com/delete-option");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                // Kirim parameter option_id sebagai x-www-form-urlencoded
                String postData = "option_id=" + URLEncoder.encode(Integer.toString(option_id), "UTF-8");

                OutputStream os = conn.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                int status = conn.getResponseCode();
                InputStream is = (status == HttpURLConnection.HTTP_OK) ? conn.getInputStream() : conn.getErrorStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                conn.disconnect();

                if (status == HttpURLConnection.HTTP_OK) {
                    callback.onSuccess(response.toString());
                } else {
                    callback.onError(new Exception("Server returned status: " + status + ", response: " + response.toString()));
                }
            } catch (Exception e) {
                callback.onError(e);
            }
        }).start();
    }

    public int getId() { return id; }
    public int getQuestionId() { return question_id; }
    public String getValue() { return value; }

    @Override
    public String toString() {
        return "Option{id=" + id +
                ", questionId=" + question_id +
                ", value='" + value + "'}";
    }
}
