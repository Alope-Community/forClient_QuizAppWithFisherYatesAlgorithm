package com.example.quizwithfisheryates.adminActivities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
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
        setContentView(R.layout.activity_admin_user_index);

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

                Integer id = user.optInt("id", 1);
                String name = user.optString("name", "Tanpa Nama");
                String username = user.optString("username", "-");

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

                // Tombol aksi (edit + delete)
                LinearLayout actionLayout = new LinearLayout(this);
                actionLayout.setOrientation(LinearLayout.HORIZONTAL);
                actionLayout.setPadding(0, 8, 0, 0);

                int sizeInDp = 40;
                float scale = getResources().getDisplayMetrics().density;
                int sizeInPx = (int) (sizeInDp * scale + 0.5f);

                // Tombol Edit
                ImageButton editButton = new ImageButton(this);
                editButton.setLayoutParams(new LinearLayout.LayoutParams(sizeInPx, sizeInPx));
                editButton.setImageResource(R.drawable.ic_edit);
                editButton.setBackgroundColor(Color.parseColor("#1f94f0"));
                editButton.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
                editButton.setPadding(16, 16, 16, 16);
                editButton.setColorFilter(Color.WHITE);
                editButton.setContentDescription("Edit User");

                editButton.setOnClickListener(v -> {
                    Intent intent = new Intent(ListUserActivity.this, UpdateUserActivity.class);
                    intent.putExtra("id", String.valueOf(id));
                    intent.putExtra("name", name);
                    intent.putExtra("username", username);
                    startActivity(intent);
                });

                actionLayout.addView(editButton);

                // Tombol Delete
                ImageButton deleteButton = new ImageButton(this);
                deleteButton.setLayoutParams(new LinearLayout.LayoutParams(sizeInPx, sizeInPx));
                deleteButton.setImageResource(R.drawable.ic_delete);
                deleteButton.setBackgroundColor(Color.parseColor("#F44336"));
                deleteButton.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
                deleteButton.setPadding(16, 16, 16, 16);
                deleteButton.setColorFilter(Color.WHITE);
                deleteButton.setContentDescription("Hapus User");

                deleteButton.setOnClickListener(v -> {
                    new AlertDialog.Builder(ListUserActivity.this)
                            .setTitle("Konfirmasi Hapus")
                            .setMessage("Apakah kamu yakin ingin menghapus user: " + name + "?")
                            .setPositiveButton("Hapus", (dialog, which) -> {
                                UserResource.deleteUser(id, new UserResource.ApiCallback() {
                                    @Override
                                    public void onSuccess(String response) {
                                        runOnUiThread(() -> {
                                            Toast.makeText(ListUserActivity.this, "User berhasil dihapus", Toast.LENGTH_SHORT).show();
                                            fetchUserList("user");
                                        });
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        runOnUiThread(() -> Toast.makeText(
                                                ListUserActivity.this,
                                                "Gagal menghapus user: " + e.getMessage(),
                                                Toast.LENGTH_LONG
                                        ).show());
                                    }
                                });
                            })
                            .setNegativeButton("Batal", null)
                            .show();
                });

                actionLayout.addView(deleteButton);
                quizContainer.addView(actionLayout);

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
