package com.example.adision;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends AppCompatActivity {

    private List<Food> allFoods = new ArrayList<>();
    private List<String> orderItems = new ArrayList<>();
    private Tables table;
    private FoodAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order);

        ListView menuList = findViewById(R.id.menu_list);
        ListView orderList = findViewById(R.id.order_list);
        TextView donerTextView = findViewById(R.id.nav_doner);
        TextView bakeTextView = findViewById(R.id.nav_bake);
        TextView grillTextView = findViewById(R.id.nav_grill);
        TextView snackTextView = findViewById(R.id.nav_drinks);
        TextView dessertTextView = findViewById(R.id.nav_dessert);
        Button btnOrder = findViewById(R.id.btn_order);
        Button btnGiven = findViewById(R.id.btn_given);
        Button btnPaid = findViewById(R.id.btn_paid);
        ImageView backButton = findViewById(R.id.back);

        adapter = new FoodAdapter(OrderActivity.this, new ArrayList<>());
        menuList.setAdapter(adapter);

        ArrayAdapter<String> orderAdapter = new ArrayAdapter<>(OrderActivity.this, android.R.layout.simple_list_item_1, orderItems);
        orderList.setAdapter(orderAdapter);

        OrderApi orderApi = RetrofitClient.getRetrofitInstance().create(OrderApi.class);
        TablesApi tablesApi = RetrofitClient.getRetrofitInstance().create(TablesApi.class);

        menuList.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                menuList.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int desiredHeight = 400;
                ViewGroup.LayoutParams params = menuList.getLayoutParams();
                params.height = desiredHeight;
                menuList.setLayoutParams(params);
            }
        });

        String tableNo = getIntent().getStringExtra("tableNo");

        if(tableNo != null){
            Call<Order> orderCall = orderApi.getOrderById(Integer.parseInt(tableNo));

            orderCall.enqueue(new Callback<Order>() {
                @Override
                public void onResponse(Call<Order> call, Response<Order> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if(!orderItems.isEmpty()){
                            orderItems.clear();
                        }
                        if (response.body().getFoods() != null){
                            String orderedFoods = response.body().getFoods();
                            String[] foodNames = orderedFoods.split(",");
                            Collections.addAll(orderItems, foodNames);
                            orderAdapter.notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Order> call, Throwable t) {

                }
            });

            Call<Tables> call = tablesApi.getById(Integer.parseInt(tableNo));

            call.enqueue(new Callback<Tables>() {
                @Override
                public void onResponse(Call<Tables> call, Response<Tables> response) {
                    table = response.body();
                }

                @Override
                public void onFailure(Call<Tables> call, Throwable t) {

                }
            });
        }

        orderList.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                orderList.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int desiredHeight = 400;
                int desiredWidth = 520;
                ViewGroup.LayoutParams params = orderList.getLayoutParams();
                params.height = desiredHeight;
                params.width = desiredWidth;
                orderList.setLayoutParams(params);
            }
        });

        orderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                orderItems.remove(position);
                orderAdapter.notifyDataSetChanged();
            }
        });

        FoodApi foodApi = RetrofitClient.getRetrofitInstance().create(FoodApi.class);
        Call<List<Food>> call = foodApi.getFoodsByCategory("Döner");

        call.enqueue(new Callback<List<Food>>() {
            @Override
            public void onResponse(Call<List<Food>> call, Response<List<Food>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allFoods = response.body();
                    updateListByCategory("Döner");
                }
            }

            @Override
            public void onFailure(Call<List<Food>> call, Throwable t) {
                Toast.makeText(OrderActivity.this, "Veri alınamadı", Toast.LENGTH_SHORT).show();
            }
        });

        menuList.setOnItemClickListener((parent, view, position, id) -> {

            Food selectedItem = allFoods.get(position);
            orderItems.add(selectedItem.getName());
            orderAdapter.notifyDataSetChanged();

        });


        btnOrder.setOnClickListener((action) -> {
            StringBuilder sb = new StringBuilder();
            for(String foodName : orderItems){
                sb.append(foodName).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            Order order = new Order();
            assert tableNo != null;
            order.setTableNo(Integer.parseInt(tableNo));
            order.setFoods(sb.toString());
            Call<Order> call2 = orderApi.updateOrder(Integer.parseInt(tableNo),order);
            call2.enqueue(new Callback<Order>() {
                @Override
                public void onResponse(Call<Order> call, Response<Order> response) {
                    table.setState("ordered");
                    Call<Tables> updatedTableCall = tablesApi.updateTables(table.getId(), table);
                    updatedTableCall.enqueue(new Callback<Tables>() {
                        @Override
                        public void onResponse(Call<Tables> call, Response<Tables> response) {
                            Toast.makeText(OrderActivity.this,"Sipariş alındı",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(OrderActivity.this, TablesActivity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(Call<Tables> call, Throwable t) {

                        }
                    });
                    Toast.makeText(OrderActivity.this, "Sipariş eklendi", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<Order> call, Throwable t) {
                    Toast.makeText(OrderActivity.this, "Veri alınamadı", Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnGiven.setOnClickListener((action) -> {
            table.setState("given");
            Call<Tables> call1 = tablesApi.updateTables(table.getId(),table);
            call1.enqueue(new Callback<Tables>() {
                @Override
                public void onResponse(Call<Tables> call, Response<Tables> response) {
                    Toast.makeText(OrderActivity.this,"Sipariş verildi",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(OrderActivity.this, TablesActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onFailure(Call<Tables> call, Throwable t) {

                }
            });
        });

        btnPaid.setOnClickListener((action) -> {
            assert tableNo != null;
            Call<Order> call1 = orderApi.getOrderById(Integer.parseInt(tableNo));
            Call<Tables> call2 = tablesApi.getById(Integer.parseInt(tableNo));

            call2.enqueue(new Callback<Tables>() {
                @Override
                public void onResponse(Call<Tables> call, Response<Tables> response) {
                    Tables table = response.body();
                    table.setState("available");

                    Call<Tables> call3 = tablesApi.updateTables(Integer.parseInt(tableNo),table);
                    call3.enqueue(new Callback<Tables>() {
                        @Override
                        public void onResponse(Call<Tables> call, Response<Tables> response) {

                        }

                        @Override
                        public void onFailure(Call<Tables> call, Throwable t) {

                        }
                    });
                }

                @Override
                public void onFailure(Call<Tables> call, Throwable t) {

                }
            });


            call1.enqueue(new Callback<Order>() {
                @Override
                public void onResponse(Call<Order> call, Response<Order> response) {
                    Order order = response.body();
                    order.setFoods(null);
                    Call<Order> call1 = orderApi.updateOrder(Integer.parseInt(tableNo),order);
                    call1.enqueue(new Callback<Order>() {
                        @Override
                        public void onResponse(Call<Order> call, Response<Order> response) {
                            Toast.makeText(OrderActivity.this,"Hesap ödendi",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(OrderActivity.this, TablesActivity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(Call<Order> call, Throwable t) {

                        }
                    });
                }

                @Override
                public void onFailure(Call<Order> call, Throwable t) {

                }
            });
        });

        backButton.setOnClickListener((action) -> {
            Intent intent = new Intent(OrderActivity.this, TablesActivity.class);
            startActivity(intent);
        });

        View.OnClickListener textViewClickListener = view -> {
            if (view instanceof TextView) {
                TextView textView = (TextView) view;
                String category = textView.getText().toString();
                Call<List<Food>> call1 = foodApi.getFoodsByCategory(category);

                call1.enqueue(new Callback<List<Food>>() {
                    @Override
                    public void onResponse(Call<List<Food>> call1, Response<List<Food>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            allFoods = response.body();
                            updateListByCategory(category);
                        } else {
                            Log.e("API", "Response failed: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Food>> call1, Throwable t) {
                        Log.e("API", "Request failed", t);
                    }
                });
            }
        };

        donerTextView.setOnClickListener(textViewClickListener);
        bakeTextView.setOnClickListener(textViewClickListener);
        grillTextView.setOnClickListener(textViewClickListener);
        snackTextView.setOnClickListener(textViewClickListener);
        dessertTextView.setOnClickListener(textViewClickListener);
    }

    private void updateListByCategory(String category) {
        List<Food> filteredFoods = new ArrayList<>();
        for (Food food : allFoods) {
            if (food.getCategory().equalsIgnoreCase(category)) {
                filteredFoods.add(food);
            }
        }

        adapter.clear();
        adapter.addAll(filteredFoods);
        adapter.notifyDataSetChanged();
    }
}
