package com.example.syoung.final_project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FoodStorageActivity extends Activity implements AdapterView.OnItemClickListener{

    private String[] foodStorageMenu = {"Add Item", "Find Items"};
    private ListView foodStorageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_storage);
        foodStorageList = (ListView)findViewById(R.id.foodStorageListView);
        foodStorageList.setAdapter(new ArrayAdapter<String>(this, R.layout.menu_text, foodStorageMenu));
        foodStorageList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
                Toast.LENGTH_SHORT).show();
        if("Add Item".equals(((TextView) view).getText())){
            Intent addItem = new Intent(getApplicationContext(), AddItemActivity.class);
            addItem.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            addItem.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(addItem);
        }
        else if("Find Items".equals(((TextView) view).getText())){
            Intent findItem = new Intent(getApplicationContext(), FindItemActivity.class);
            findItem.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            findItem.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(findItem);
        }
    }
}
