package com.example.quizwithfisheryates._apiResources;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class CourseResource {
    public interface ApiCallback {
        void onSuccess(String response);
        void onError(Exception e);
    }

    public static void getCourse(CourseResource.ApiCallback callback) {
        new Thread(() -> {
            try {
                // Buat URL dengan parameter GET
                String baseUrl = "https://quiz.alope.id/courses";

                URL url = new URL(baseUrl);
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

    public static void showCourse(int id, CourseResource.ApiCallback callback) {
        new Thread(() -> {
            try {
                // Buat URL dengan parameter GET
                String baseUrl = "https://quiz.alope.id/show-course";
                String queryParams = "?id=" + URLEncoder.encode(Integer.toString(id), "UTF-8");

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

//    public static void postCourse(
//            String title,
//            String description,
//            String body,
//            int account_id,
//            CourseResource.ApiCallback callback
//    ) {
//        new Thread(() -> {
//            try {
//                URL url = new URL("https://quiz.alope.id/create-course");
//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                conn.setRequestMethod("POST");
//                conn.setDoOutput(true);
//
//                String postData = "title=" + title + "&description=" + description + "&body=" + body + "&account_id=" + account_id;
//                OutputStream os = conn.getOutputStream();
//                os.write(postData.getBytes());
//                os.flush();
//                os.close();
//
//                InputStream is = conn.getInputStream();
//                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//                StringBuilder result = new StringBuilder();
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    result.append(line);
//                }
//
//                reader.close();
//                is.close();
//                conn.disconnect();
//
//                callback.onSuccess(result.toString());
//
//            } catch (Exception e) {
//                callback.onError(e);
//            }
//        }).start();
//    }

    private static void addFormField(PrintWriter writer, String boundary, String name, String value) {
        String LINE_FEED = "\r\n";
        writer.append("--").append(boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"").append(name).append("\"").append(LINE_FEED);
        writer.append("Content-Type: text/plain; charset=UTF-8").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(value).append(LINE_FEED);
        writer.flush();
    }

    private static void addFilePart(PrintWriter writer, OutputStream outputStream, String boundary, String fieldName, File uploadFile) throws IOException {
        String LINE_FEED = "\r\n";
        String fileName = uploadFile.getName();
        writer.append("--").append(boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"").append(fieldName)
                .append("\"; filename=\"").append(fileName).append("\"").append(LINE_FEED);
        writer.append("Content-Type: ").append(URLConnection.guessContentTypeFromName(fileName)).append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();

        FileInputStream inputStream = new FileInputStream(uploadFile);
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


    public static void postCourse(
            String title,
            String description,
            String body,
            int account_id,
            Uri coverUri,
            Context context,
            CourseResource.ApiCallback callback
    ) {
        new Thread(() -> {
            String boundary = "===" + System.currentTimeMillis() + "===";
            String LINE_FEED = "\r\n";

            try {
                URL url = new URL("https://quiz.alope.id/create-course");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setUseCaches(false);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                OutputStream outputStream = conn.getOutputStream();
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true);

                // --- Title
                writer.append("--").append(boundary).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"title\"").append(LINE_FEED);
                writer.append("Content-Type: text/plain; charset=UTF-8").append(LINE_FEED);
                writer.append(LINE_FEED).append(title).append(LINE_FEED);
                writer.flush();

                // --- Description
                writer.append("--").append(boundary).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"description\"").append(LINE_FEED);
                writer.append("Content-Type: text/plain; charset=UTF-8").append(LINE_FEED);
                writer.append(LINE_FEED).append(description).append(LINE_FEED);
                writer.flush();

                // --- Body
                writer.append("--").append(boundary).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"body\"").append(LINE_FEED);
                writer.append("Content-Type: text/plain; charset=UTF-8").append(LINE_FEED);
                writer.append(LINE_FEED).append(body).append(LINE_FEED);
                writer.flush();

                // --- Account ID
                writer.append("--").append(boundary).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"account_id\"").append(LINE_FEED);
                writer.append("Content-Type: text/plain; charset=UTF-8").append(LINE_FEED);
                writer.append(LINE_FEED).append(String.valueOf(account_id)).append(LINE_FEED);
                writer.flush();

                // --- Upload cover image
                if (coverUri != null) {
                    String fileName = "cover.jpg";

                    writer.append("--").append(boundary).append(LINE_FEED);
                    writer.append("Content-Disposition: form-data; name=\"cover\"; filename=\"").append(fileName).append("\"").append(LINE_FEED);
                    writer.append("Content-Type: image/jpeg").append(LINE_FEED);
                    writer.append(LINE_FEED);
                    writer.flush();

                    InputStream inputStream = context.getContentResolver().openInputStream(coverUri);
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

                // --- End boundary
                writer.append("--").append(boundary).append("--").append(LINE_FEED);
                writer.close();

                // --- Read response
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

    public static void updateCourse(
            int id,
            String title,
            String description,
            String body,
            int account_id,
            Uri coverUri,
            Context context,
            CourseResource.ApiCallback callback
    ) {
        new Thread(() -> {
            String boundary = "===" + System.currentTimeMillis() + "===";
            String LINE_FEED = "\r\n";

            try {
                URL url = new URL("https://quiz.alope.id/update-course");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setUseCaches(false);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                OutputStream outputStream = conn.getOutputStream();
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true);

                // --- Course ID
                writer.append("--").append(boundary).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"id\"").append(LINE_FEED);
                writer.append("Content-Type: text/plain; charset=UTF-8").append(LINE_FEED);
                writer.append(LINE_FEED).append(String.valueOf(id)).append(LINE_FEED);
                writer.flush();

                // --- Title
                writer.append("--").append(boundary).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"title\"").append(LINE_FEED);
                writer.append("Content-Type: text/plain; charset=UTF-8").append(LINE_FEED);
                writer.append(LINE_FEED).append(title).append(LINE_FEED);
                writer.flush();

                // --- Description
                writer.append("--").append(boundary).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"description\"").append(LINE_FEED);
                writer.append("Content-Type: text/plain; charset=UTF-8").append(LINE_FEED);
                writer.append(LINE_FEED).append(description).append(LINE_FEED);
                writer.flush();

                // --- Body
                writer.append("--").append(boundary).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"body\"").append(LINE_FEED);
                writer.append("Content-Type: text/plain; charset=UTF-8").append(LINE_FEED);
                writer.append(LINE_FEED).append(body).append(LINE_FEED);
                writer.flush();

                // --- Account ID
                writer.append("--").append(boundary).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"account_id\"").append(LINE_FEED);
                writer.append("Content-Type: text/plain; charset=UTF-8").append(LINE_FEED);
                writer.append(LINE_FEED).append(String.valueOf(account_id)).append(LINE_FEED);
                writer.flush();

                // --- Upload cover image (jika ada)
                if (coverUri != null) {
                    String fileName = "cover.jpg";

                    writer.append("--").append(boundary).append(LINE_FEED);
                    writer.append("Content-Disposition: form-data; name=\"cover\"; filename=\"").append(fileName).append("\"").append(LINE_FEED);
                    writer.append("Content-Type: image/jpeg").append(LINE_FEED);
                    writer.append(LINE_FEED);
                    writer.flush();

                    InputStream inputStream = context.getContentResolver().openInputStream(coverUri);
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

                // --- End of multipart
                writer.append("--").append(boundary).append("--").append(LINE_FEED);
                writer.close();

                // --- Read response
                int status = conn.getResponseCode();
                InputStream responseStream = (status == HttpURLConnection.HTTP_OK)
                        ? conn.getInputStream()
                        : conn.getErrorStream();

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

    public static void deleteCourse(int id, ApiCallback callback) {
        new Thread(() -> {
            try {
                URL url = new URL("https://quiz.alope.id/delete-course");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String postData = "id=" + id;

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(postData.getBytes("UTF-8"));
                    os.flush();
                }

                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
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
