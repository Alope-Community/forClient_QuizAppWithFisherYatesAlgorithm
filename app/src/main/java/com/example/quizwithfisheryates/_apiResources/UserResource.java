package com.example.quizwithfisheryates._apiResources;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class UserResource {

    public interface ApiCallback {
        void onSuccess(String response);
        void onError(Exception e);
    }
    public static void getUser(String role, UserResource.ApiCallback callback) {
        new Thread(() -> {
            try {
                // Buat URL dengan parameter GET
                String baseUrl = "https://quiz.alope.id/users";
                String queryParams = "?role=" + URLEncoder.encode(role, "UTF-8");

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

    public static void deleteUser(int userId, UserResource.ApiCallback callback) {
        new Thread(() -> {
            try {
                URL url = new URL("https://quiz.alope.id/delete-user");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String postData = "id=" + userId;
                OutputStream os = conn.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                reader.close();
                conn.disconnect();

                callback.onSuccess(result.toString());

            } catch (Exception e) {
                callback.onError(e);
            }
        }).start();
    }
}
