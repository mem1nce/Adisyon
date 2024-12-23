package com.example.adision;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TablesActivity extends AppCompatActivity {
    private List<Tables> tables = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tables);

        TextView table1 = findViewById(R.id.textView1);
        TextView table2 = findViewById(R.id.textView2);
        TextView table3 = findViewById(R.id.textView3);
        TextView table4 = findViewById(R.id.textView4);
        TextView table5 = findViewById(R.id.textView5);
        TextView table6 = findViewById(R.id.textView6);
        TextView table7 = findViewById(R.id.textView7);
        TextView table8 = findViewById(R.id.textView8);
        TextView table9 = findViewById(R.id.textView9);
        ImageView homeButton = findViewById(R.id.img_home);

        View.OnClickListener textViewClickListener = v -> {
            if (v instanceof TextView) {
                TextView textView = (TextView) v;
                Intent intent = new Intent(TablesActivity.this, OrderActivity.class);
                String text = textView.getText().toString();
                intent.putExtra("tableNo", String.valueOf(text.charAt(textView.length() - 1)));
                startActivity(intent);
            }
        };

        TablesApi tablesApi = RetrofitClient.getRetrofitInstance().create(TablesApi.class);
        Call<List<Tables>> call = tablesApi.getAll();

        call.enqueue(new Callback<List<Tables>>() {
            @Override
            public void onResponse(Call<List<Tables>> call, Response<List<Tables>> response) {
                tables = response.body();
                if (tables != null && tables.size() >= 9) {
                    setTextViewBackground(table1, tables.get(0).getState());
                    setTextViewBackground(table2, tables.get(1).getState());
                    setTextViewBackground(table3, tables.get(2).getState());
                    setTextViewBackground(table4, tables.get(3).getState());
                    setTextViewBackground(table5, tables.get(4).getState());
                    setTextViewBackground(table6, tables.get(5).getState());
                    setTextViewBackground(table7, tables.get(6).getState());
                    setTextViewBackground(table8, tables.get(7).getState());
                    setTextViewBackground(table9, tables.get(8).getState());
                } else {
                    Log.e("TablesActivity", "Tables listesi boş veya yetersiz eleman var!");
                }
            }

            @Override
            public void onFailure(Call<List<Tables>> call, Throwable t) {
                Log.e("TablesActivity", "Ağ isteği başarısız!", t);
            }
        });

        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(TablesActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        table1.setOnClickListener(textViewClickListener);
        table2.setOnClickListener(textViewClickListener);
        table3.setOnClickListener(textViewClickListener);
        table4.setOnClickListener(textViewClickListener);
        table5.setOnClickListener(textViewClickListener);
        table6.setOnClickListener(textViewClickListener);
        table7.setOnClickListener(textViewClickListener);
        table8.setOnClickListener(textViewClickListener);
        table9.setOnClickListener(textViewClickListener);
    }

    private void setTextViewBackground(TextView textView,String state){
        GradientDrawable border = new GradientDrawable();
        border.setColor(Color.WHITE);
        border.setCornerRadius(8);
        switch (state) {
            case "ordered":
                border.setStroke(4, Color.YELLOW);
                break;
            case "given":
                border.setStroke(4, Color.RED);
                break;
            default:
                border.setStroke(4, Color.GREEN);
                break;
        }
        textView.setBackground(border);
    }
}