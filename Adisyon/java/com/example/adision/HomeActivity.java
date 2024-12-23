package com.example.adision;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.home);

        Button btnMenu = findViewById(R.id.btn_menu);
        Button btnTables = findViewById(R.id.btn_tables);
        Button btnQuit = findViewById(R.id.btn_quit);

        btnMenu.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MenuActivity.class);
            startActivity(intent);
        });

        btnTables.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, TablesActivity.class);
            startActivity(intent);
        });

        btnQuit.setOnClickListener(v -> {
            finishAffinity();
            System.exit(0);
        });
    }
}
