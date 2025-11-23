package com.example.quizwithfisheryates._models;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

//import com.example.quizwithfisheryates._apiResources.CourseResource;

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

public class Course {
    private int id;
    private String title;
    private String cover;
    private String description;
    private String body;

    private String created_at;
    private int account_id;

    public Course(int id, String title, String cover, String description, String body, String created_at, int account_id) {
        this.id = id;
        this.title = title;
        this.cover = cover;
        this.description = description;
        this.body = body;
        this.created_at = created_at;
        this.account_id = account_id;
    }

    //
    public interface ApiCallback {
        void onSuccess(String response);
        void onError(Exception e);
    }

    public static void getCourse(Course.ApiCallback callback) {
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

    public static void showCourse(int id, Course.ApiCallback callback) {
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

    private static String getFileName(Context context, Uri uri) {
        String result = null;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            result = cursor.getString(nameIndex);
            cursor.close();
        }
        return result != null ? result : "video.mp4";
    }


    public static void postCourse(
            String title,
            String description,
            String body,
            int account_id,
            Uri coverUri,
            Uri audioUri,
            Uri videoUri,
            Context context,
            Course.ApiCallback callback
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

                /* ------------------------
                 * TEXT FIELDS
                 * ------------------------ */

                // Title
                writer.append("--").append(boundary).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"title\"").append(LINE_FEED);
                writer.append("Content-Type: text/plain; charset=UTF-8").append(LINE_FEED);
                writer.append(LINE_FEED).append(title).append(LINE_FEED);
                writer.flush();

                // Description
                writer.append("--").append(boundary).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"description\"").append(LINE_FEED);
                writer.append("Content-Type: text/plain; charset=UTF-8").append(LINE_FEED);
                writer.append(LINE_FEED).append(description).append(LINE_FEED);
                writer.flush();

                // Body
                writer.append("--").append(boundary).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"body\"").append(LINE_FEED);
                writer.append("Content-Type: text/plain; charset=UTF-8").append(LINE_FEED);
                writer.append(LINE_FEED).append(body).append(LINE_FEED);
                writer.flush();

                // Account ID
                writer.append("--").append(boundary).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"account_id\"").append(LINE_FEED);
                writer.append("Content-Type: text/plain; charset=UTF-8").append(LINE_FEED);
                writer.append(LINE_FEED).append(String.valueOf(account_id)).append(LINE_FEED);
                writer.flush();


                /* ------------------------
                 * FILE: COVER IMAGE
                 * ------------------------ */
                if (coverUri != null) {
                    String fileName = "cover.jpg";
                    String mime = "image/jpeg";

                    writer.append("--").append(boundary).append(LINE_FEED);
                    writer.append("Content-Disposition: form-data; name=\"cover\"; filename=\"").append(fileName).append("\"").append(LINE_FEED);
                    writer.append("Content-Type: ").append(mime).append(LINE_FEED);
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


                /* ------------------------
                 * FILE: AUDIO
                 * ------------------------ */
                if (audioUri != null) {
                    String audioFileName = getAudioFileName(context, audioUri);
                    String mime = getMimeType(context, audioUri);

                    writer.append("--").append(boundary).append(LINE_FEED);
                    writer.append("Content-Disposition: form-data; name=\"audio\"; filename=\"")
                            .append(audioFileName).append("\"").append(LINE_FEED);
                    writer.append("Content-Type: ").append(mime).append(LINE_FEED);
                    writer.append(LINE_FEED);
                    writer.flush();

                    InputStream audioStream = context.getContentResolver().openInputStream(audioUri);
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = audioStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    outputStream.flush();
                    audioStream.close();

                    writer.append(LINE_FEED);
                    writer.flush();
                }


                /* ------------------------
                 * FILE: VIDEO  (NEW)
                 * ------------------------ */
                if (videoUri != null) {

                    String videoFileName = getFileName(context, videoUri);
                    String mime = getMimeType(context, videoUri);

                    if (mime == null) mime = "video/mp4"; // fallback default

                    writer.append("--").append(boundary).append(LINE_FEED);
                    writer.append("Content-Disposition: form-data; name=\"video\"; filename=\"")
                            .append(videoFileName).append("\"").append(LINE_FEED);
                    writer.append("Content-Type: ").append(mime).append(LINE_FEED);
                    writer.append(LINE_FEED);
                    writer.flush();

                    InputStream videoStream = context.getContentResolver().openInputStream(videoUri);
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = videoStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    outputStream.flush();
                    videoStream.close();

                    writer.append(LINE_FEED);
                    writer.flush();
                }


                /* ------------------------
                 * END MULTIPART
                 * ------------------------ */
                writer.append("--").append(boundary).append("--").append(LINE_FEED);
                writer.close();


                /* ------------------------
                 * READ RESPONSE
                 * ------------------------ */
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
                    callback.onError(new Exception("Server returned status: " + status + ", response: " + responseStrBuilder));
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
            Uri audioUri,
            Context context,
            Course.ApiCallback callback
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

                // --- Upload audio file (jika ada)
                if (audioUri != null) {
                    // Get file extension from URI
                    String audioFileName = getAudioFileName(context, audioUri);

                    writer.append("--").append(boundary).append(LINE_FEED);
                    writer.append("Content-Disposition: form-data; name=\"audio\"; filename=\"").append(audioFileName).append("\"").append(LINE_FEED);
                    writer.append("Content-Type: ").append(getMimeType(context, audioUri)).append(LINE_FEED);
                    writer.append(LINE_FEED);
                    writer.flush();

                    InputStream audioInputStream = context.getContentResolver().openInputStream(audioUri);
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = audioInputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    outputStream.flush();
                    audioInputStream.close();

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

    public static void deleteCourse(int id, Course.ApiCallback callback) {
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

    // Helper method untuk mendapatkan nama file audio
    private static String getAudioFileName(Context context, Uri audioUri) {
        String fileName = "audio.mp3"; // default

        try {
            Cursor cursor = context.getContentResolver().query(audioUri, null, null, null, null);
            if (cursor != null) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex >= 0 && cursor.moveToFirst()) {
                    String displayName = cursor.getString(nameIndex);
                    if (displayName != null && !displayName.isEmpty()) {
                        fileName = displayName;
                    }
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.w("AUDIO_FILENAME", "Could not get audio filename", e);
        }

        return fileName;
    }

    // Helper method untuk mendapatkan MIME type
    private static String getMimeType(Context context, Uri uri) {
        String mimeType = context.getContentResolver().getType(uri);
        if (mimeType == null) {
            mimeType = "audio/mpeg"; // default to mp3
        }
        return mimeType;
    }

    public int getID() { return id; }
    public String getTitle() { return title; }
    public String getCover() { return cover; }
    public String getDescription() { return description; }
    public String getBody() { return body; }
    public String getCreatedAt() { return created_at; }
    public int getAccountID() { return account_id; }
}
