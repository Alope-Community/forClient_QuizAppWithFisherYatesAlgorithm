package com.example.quizwithfisheryates.authActivities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quizwithfisheryates.MainActivity;
import com.example.quizwithfisheryates.R;
import com.example.quizwithfisheryates._apiResources.AuthResource;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void onLogin(View view) {
        EditText username = findViewById(R.id.username);
        EditText password = findViewById(R.id.password);

        String user = username.getText().toString();
        String pass = password.getText().toString();

        ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false); // agar tidak bisa dibatalkan
        progressDialog.show(); // tampilkan loading

        AuthResource.postLogin(user, pass, new AuthResource.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                progressDialog.dismiss(); // sembunyikan loading

                Log.d("LOGIN_SUCCESS", response);
                try {
                    JSONObject json = new JSONObject(response);
                    String message = json.getString("message");
                    JSONObject data = json.getJSONObject("data");

                    int id = data.getInt("id");
                    String name = data.getString("name");
                    String username = data.getString("username");
                    String role = data.getString("role");

                    Class<?> nextActivity = role.equals("admin")
                            ? com.example.quizwithfisheryates.adminActivities.MainActivity.class
                            : MainActivity.class;

                    sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.putInt("id", id);
                    editor.putString("name", name);
                    editor.putString("username", username);
                    editor.putString("role", role);
                    editor.apply();

                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, nextActivity));
                    });

                    finish();

                } catch (JSONException e) {
                    progressDialog.dismiss(); // pastikan loading di-dismiss jika error
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(
                            LoginActivity.this,
                            "Format response JSON salah",
                            Toast.LENGTH_SHORT
                    ).show());
                }
            }

            @Override
            public void onError(Exception e) {
                progressDialog.dismiss(); // sembunyikan loading jika error
                Log.e("LOGIN_ERROR", "Gagal login", e);
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, "Login gagal. Coba lagi.", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }



//    public void goToRegister(View view){
//        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
//        startActivity(intent);
//    }

//    public void goToMainMenu(View view){
//        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//        startActivity(intent);
//    }
}