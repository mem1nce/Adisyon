package com.example.adision;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuActivity extends AppCompatActivity {

    private List<Food> allFoods = new ArrayList<>();
    private FoodAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        ListView menuList = findViewById(R.id.menu_list);
        TextView donerTextView = findViewById(R.id.nav_doner);
        TextView bakeTextView = findViewById(R.id.nav_bake);
        TextView grillTextView = findViewById(R.id.nav_grill);
        TextView snackTextView = findViewById(R.id.nav_drinks);
        TextView dessertTextView = findViewById(R.id.nav_dessert);
        Button showAddButton = findViewById(R.id.show_add_form);
        ImageView homeButton = findViewById(R.id.img_home);

        adapter = new FoodAdapter(MenuActivity.this, new ArrayList<>());
        menuList.setAdapter(adapter);

        menuList.setOnItemLongClickListener((parent, view, position, id) -> {
            PopupMenu popupMenu = new PopupMenu(MenuActivity.this, view);
            popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.action_delete) {
                    // Silme işlemi
                    Food selectedItem = allFoods.get(position);
                    FoodApi foodApi = RetrofitClient.getRetrofitInstance().create(FoodApi.class);
                    Call<Void> call = foodApi.deleteFood(selectedItem.getId());

                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            Toast.makeText(MenuActivity.this, "Silindi!", Toast.LENGTH_SHORT).show();
                            allFoods.remove(selectedItem);
                            updateListByCategory(selectedItem.getCategory());
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(MenuActivity.this, "Failure: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    });
                    Toast.makeText(MenuActivity.this, selectedItem.getName() + " silindi!", Toast.LENGTH_SHORT).show();
                    updateListByCategory(selectedItem.getCategory());
                    return true;
                }
                return false;
            });

            popupMenu.show();
            return true;
        });

        showAddButton.setOnClickListener(v -> {
            Log.d("MenuActivity", "Add Food Button Clicked");
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.add_food);

            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = 700;
            params.height = 1100;
            dialog.getWindow().setAttributes(params);

            Button closeButton = dialog.findViewById(R.id.food_add_cancel);
            ImageView backButton = dialog.findViewById(R.id.back_arrow);

            backButton.setOnClickListener(view -> dialog.dismiss());
            closeButton.setOnClickListener(view -> dialog.dismiss());

            Button addButton = dialog.findViewById(R.id.food_add_btn);
            EditText nameEditText = dialog.findViewById(R.id.food_add_name);
            EditText priceEditText = dialog.findViewById(R.id.food_add_price);
            Spinner categorySpinner = dialog.findViewById(R.id.categorySpinner);

            String[] categories = {"Döner", "Fırın", "Izgara", "İçecekler", "Tatlı"};
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categorySpinner.setAdapter(spinnerAdapter);

            FoodApi foodApi = RetrofitClient.getRetrofitInstance().create(FoodApi.class);

            addButton.setOnClickListener((action) -> {
                Food food = new Food();
                food.setName(nameEditText.getText().toString());
                food.setPrice(Double.parseDouble(priceEditText.getText().toString()));
                food.setCategory(categorySpinner.getSelectedItem().toString());
                Log.d("Food Data", "Name: " + food.getName() + ", Price: " + food.getPrice() + ", Category: " + food.getCategory());

                Call<Food> call = foodApi.addFood(food);

                call.enqueue(new Callback<Food>() {
                    @Override
                    public void onResponse(Call<Food> call, Response<Food> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(MenuActivity.this, "Eklendi!", Toast.LENGTH_SHORT).show();
                            Food food = response.body();
                            allFoods.add(food);
                            assert response.body() != null;
                            String category = response.body().getCategory();
                            updateListByCategory(category);
                        }
                    }

                    @Override
                    public void onFailure(Call<Food> call, Throwable t) {
                        Toast.makeText(MenuActivity.this, "Failure: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                nameEditText.setText("");
                priceEditText.setText("");
                dialog.dismiss();
            });

            dialog.show();
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
                Toast.makeText(MenuActivity.this, "Veri alınamadı", Toast.LENGTH_SHORT).show();
            }
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

        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, HomeActivity.class);
            startActivity(intent);
        });

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
