package com.example.quizwithfisheryates._apiResources;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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
}
