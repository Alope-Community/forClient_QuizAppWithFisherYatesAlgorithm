package com.example.quizwithfisheryates.authActivities;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quizwithfisheryates.R;
import com.example.quizwithfisheryates._models.User;
import com.example.quizwithfisheryates.adminActivities.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {


    EditText password;
    AppCompatImageView toggleNisn;
    boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        password = findViewById(R.id.password);
        toggleNisn = findViewById(R.id.toggleNisn);

        toggleNisn.setOnClickListener(v -> {
            if (isPasswordVisible) {
                // HIDE PASSWORD
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                toggleNisn.setImageResource(R.drawable.ic_eye); // icon mata biasa
            } else {
                // SHOW PASSWORD
                password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                toggleNisn.setImageResource(R.drawable.ic_eye); // icon mata tertutup
            }

            // Keep cursor at end
            password.setSelection(password.getText().length());

            isPasswordVisible = !isPasswordVisible;
        });

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    public void onRegister(View view){
        EditText Ename       = findViewById(R.id.name);
//        EditText Eusername   = findViewById(R.id.username);
        EditText Epassword   = findViewById(R.id.password);

        String name = Ename.getText().toString();
//        String user = Eusername.getText().toString();
        String pass = Epassword.getText().toString();

        User.postRegister(name, pass, new User.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d("REGISTER SUCCESS", response);

                // decode JSON
                try {
                    JSONObject json = new JSONObject(response);
                    String message = json.getString("message");

                    // action success on UI
                    runOnUiThread(() -> {
                        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(RegisterActivity.this, ListUserActivity.class));

                        Ename.setText("");
//                        Eusername.setText("");
                        Epassword.setText("");
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(
                            RegisterActivity.this,
                            "Format response JSON salah",
                            Toast.LENGTH_SHORT
                    ).show());
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("REGISTER_ERROR", "Gagal Registrasi", e);
                runOnUiThread(() -> {
                    Toast.makeText(RegisterActivity.this, "Login gagal. Coba lagi.", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    public void goToLogin(View view){
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void goToAdminMain(View view){
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
    }
}