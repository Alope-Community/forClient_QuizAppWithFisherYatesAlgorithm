package com.example.quizwithfisheryates.adminActivities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quizwithfisheryates.R;
import com.example.quizwithfisheryates._apiResources.QuizResource;
import com.example.quizwithfisheryates._apiResources.ScoreResource;
import com.example.quizwithfisheryates._models.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.admin_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void goToListUser(View v){
        Intent intent = new Intent(MainActivity.this, ListUserActivity.class);
        startActivity(intent);
    }

    public void goToCreateQuestion(View v){
        Intent intent = new Intent(MainActivity.this, CreateQuizActivity.class);
        startActivity(intent);
    }
    public void goToListQuestion(View v){
        Intent intent = new Intent(MainActivity.this, ListQuizActivity.class);
        startActivity(intent);
    }

    public void goToRanking(View v){
        Intent intent = new Intent(MainActivity.this, RankingActivity.class);
        startActivity(intent);
    }

//    public void exportRanking(View v){
//        ScoreResource.exportScore(MainActivity.this, new ScoreResource.FileCallback() {
//            public void onSuccess(String filePath) {
//                runOnUiThread(() -> Toast.makeText(MainActivity.this,
//                        "Export berhasil disimpan: " + filePath,
//                        Toast.LENGTH_LONG).show());
//            }
//
//            @Override
//            public void onError(Exception e) {
//                e.printStackTrace();
//                runOnUiThread(() -> Toast.makeText(MainActivity.this,
//                        "Gagal mendownload file",
//                        Toast.LENGTH_SHORT).show());
//            }
//        });
//    }

    public void logoutAdmin(View v){
        sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Toast.makeText(MainActivity.this, "Logout Berhasil", Toast.LENGTH_SHORT).show();


        Intent intent = new Intent(MainActivity.this, com.example.quizwithfisheryates.MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
