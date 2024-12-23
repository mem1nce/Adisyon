package com.example.adision;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class FoodAdapter extends ArrayAdapter<Food> {
    private final Context context;
    private final List<Food> foods;

    public FoodAdapter(Context context, List<Food> foods) {
        super(context, R.layout.list_item_food, foods);
        this.context = context;
        this.foods = foods;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_food, parent, false);
        }

        TextView nameTextView = convertView.findViewById(R.id.textViewName);
        TextView priceTextView = convertView.findViewById(R.id.textViewPrice);

        Food currentFood = foods.get(position);

        nameTextView.setText(currentFood.getName());
        priceTextView.setText(String.format("%.2f TL", currentFood.getPrice()));

        return convertView;
    }
}
