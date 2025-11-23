package com.example.quizwithfisheryates.adminActivities;

import android.content.Intent;
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

import com.example.quizwithfisheryates.R;
import com.example.quizwithfisheryates._models.User;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdateUserActivity extends AppCompatActivity {

    EditText Ename, Eusername, Epassword;
    String userId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_user_update);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.admin_update_user), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Ename = findViewById(R.id.name);
//        Eusername = findViewById(R.id.username);
        Epassword = findViewById(R.id.password);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        // Ambil data dari Intent
        Intent intent = getIntent();
        if (intent != null) {
            userId = intent.getStringExtra("id");
            String name = intent.getStringExtra("name");
            String username = intent.getStringExtra("username");
            String NISN = intent.getStringExtra("password");

            Ename.setText(name);
//            Eusername.setText(username);

            Epassword.setText(NISN);
        }
    }

    public void onUpdate(View view) {
        String name = Ename.getText().toString();
//        String username = Eusername.getText().toString();
        String password = Epassword.getText().toString();

        if (userId == "") {
            Toast.makeText(this, "User ID tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        User.updateUser(userId, name, password, new User.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d("UPDATE SUCCESS", response);
                try {
                    JSONObject json = new JSONObject(response);
                    String message = json.getString("message");
                    runOnUiThread(() -> {
                        Toast.makeText(UpdateUserActivity.this, message, Toast.LENGTH_SHORT).show();
//                        finish(); // kembali ke halaman sebelumnya

                        Intent intent = new Intent(UpdateUserActivity.this, ListUserActivity.class);
                        startActivity(intent);
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(UpdateUserActivity.this, "Format JSON salah", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("UPDATE_ERROR", "Gagal Update", e);
                runOnUiThread(() -> Toast.makeText(UpdateUserActivity.this, "Update gagal", Toast.LENGTH_SHORT).show());
            }
        });
    }

    public void goToAdminMain(View view) {
        Intent intent = new Intent(UpdateUserActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
