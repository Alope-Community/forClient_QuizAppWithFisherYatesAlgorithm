package com.example.quizwithfisheryates.adminActivities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quizwithfisheryates.R;
import com.example.quizwithfisheryates._apiResources.UserResource;
import com.example.quizwithfisheryates.authActivities.RegisterActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ListUserActivity extends AppCompatActivity {

    LinearLayout quizContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_list_user);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.admin_list_user), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        quizContainer = findViewById(R.id.quizContainer);

        fetchUserList("user");

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    private void fetchUserList(String role) {
        UserResource.getUser(role, new UserResource.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> showUsers(response));
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> Toast.makeText(
                        ListUserActivity.this,
                        "Gagal mengambil data user: " + e.getMessage(),
                        Toast.LENGTH_LONG
                ).show());
            }
        });
    }

    private void showUsers(String response) {
        quizContainer.removeAllViews();

        try {
            JSONObject json = new JSONObject(response);
            if (!json.getString("status").equals("success")) {
                Toast.makeText(this, "Status gagal", Toast.LENGTH_SHORT).show();
                return;
            }

            JSONArray users = json.getJSONArray("data");

            for (int i = 0; i < users.length(); i++) {
                JSONObject user = users.getJSONObject(i);

                String name = user.optString("name", "Tanpa Nama");
                String username = user.optString("username", "-");
                String password = user.optString("password", "-");

                // Nama
                TextView nameView = new TextView(this);
                nameView.setText("Nama: " + name);
                nameView.setTextSize(20);
                nameView.setTypeface(null, Typeface.BOLD);
                nameView.setPadding(0, 16, 0, 4);
                quizContainer.addView(nameView);

                // Username
                TextView usernameView = new TextView(this);
                usernameView.setText("Username: " + username);
                usernameView.setPadding(0, 0, 0, 4);
                quizContainer.addView(usernameView);

                // Password
                TextView passwordView = new TextView(this);
                passwordView.setText("Password: " + password);
                quizContainer.addView(passwordView);

                // Divider
                View divider = new View(this);
                LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1
                );
                dividerParams.setMargins(0, 20, 0, 20);
                divider.setLayoutParams(dividerParams);
                divider.setBackgroundColor(Color.BLACK);
                quizContainer.addView(divider);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Format JSON tidak valid", Toast.LENGTH_SHORT).show();
        }
    }

    public void goToRegister(View view){
        Intent intent = new Intent(ListUserActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}
