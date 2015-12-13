package com.example.syoung.final_project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FindItemActivity extends Activity implements SearchView.OnQueryTextListener{

    private SearchView searchView;
    private ArrayList<FoodStorageDataModel> displayItemList;
    private ArrayList<FoodStorageDataModel> fullItemList;
    private ArrayAdapter<FoodStorageDataModel> arrayAdapter;
    private ListView itemListView;
    private AdapterView.OnItemClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_item);
        displayItemList = new ArrayList<>();
        fullItemList = new ArrayList<>();
        loadFile();
        findViewsById();
        setSearch();
        setListAdapter();
    }


    private void findViewsById(){
        searchView = (SearchView) findViewById(R.id.search_button);
        itemListView = (ListView) findViewById(R.id.item_list);
    }

    private void setListAdapter(){

        listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent editItem = new Intent(getApplicationContext(), EditFoodItemActivity.class);
                editItem.putExtra("ItemName", ((TextView) view).getText().toString());
                editItem.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                editItem.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(editItem);

            }
        };
        ArrayList<String> itemNames = new ArrayList<String>();
        String itemNamesArray[] = new String[0];
        if(displayItemList.size() > 0) {
            for (FoodStorageDataModel model : displayItemList) {
                itemNames.add(model.getItemName());
            }
            if(itemNames.size() > 1) {
                Collections.sort(itemNames);
            }
            itemNamesArray = new String[itemNames.size()]; //check to make sure this worked
            int counter = 0;
            for(String names: itemNames){
                itemNamesArray[counter] = names;
                counter++;
            }

        }

        itemListView.setAdapter(new ArrayAdapter<String>(this, R.layout.menu_text, itemNamesArray));
        itemListView.setOnItemClickListener(listener);

    }

    private void setSearch(){

        searchView.setIconified(false);
        searchView.setOnQueryTextListener(this);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("Search Here");



//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                callSearch(query);
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                callSearch(newText); //todo decide if I want to do this or just the search button
//                return true;
//            }
//
//            private void callSearch(String query) {
//                //todo get information from file and output data based on query
//                //todo populate list view based on search
//                AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        //TODO go to edit view
//                    }
//                };
//                ArrayList<String> itemNames = new ArrayList<String>();
//
//                for(FoodStorageDataModel model : displayItemList){
//                    if()
//                }
//                String itemNamesArray[] = new String[itemNames.size()];
//
//                itemListView.setAdapter(new ArrayAdapter<String>(getCallingActivity(), R.layout.menu_text, itemNamesArray));
//                itemListView.setOnItemClickListener(listener);
//            }
//
//        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }



    protected void loadFile() {
        //super.onResume();
        try{
            File file = new File(getFilesDir(), "foodstorage.txt");
            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);
            String jsonString = reader.readLine();

            Gson gson = new Gson();
            Type collectionType = new TypeToken<ArrayList<FoodStorageDataModel>>(){}.getType();
            ArrayList<FoodStorageDataModel> items = gson.fromJson(jsonString, collectionType);
            //paintAreaView.setPointList(Points);
            fullItemList = items;
            displayItemList = items;

        }catch (Exception e){
            Log.e("Persistence", "Error saving file: " + e.getMessage());
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        callSearch(query);
        return true;
    }

    private void callSearch(String query) {
        displayItemList = new ArrayList<>();
        for (FoodStorageDataModel itemValue : fullItemList) {
            if(itemValue.getItemName().contains(query)){
                displayItemList.add(itemValue);
            }
        }
        setListAdapter();
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if("".equals(newText)){
            displayItemList = fullItemList;
            setListAdapter();
        }
        return true;
    }


//    @Override
//    protected void onPause() {
//        super.onPause();
//        List<FoodStorageDataModel> foodStorage = displayItemList;
//        Gson gson = new Gson();
//        String jsonString = gson.toJson(foodStorage);
//        try{
//            File file = new File(getFilesDir(), "foodstorage.txt");
//            FileWriter fileWriter = new FileWriter(file);
//            BufferedWriter writer = new BufferedWriter(fileWriter);
//            writer.write(jsonString);
//            writer.close();
//
//        }catch (Exception e){
//            Log.e("Persistence", "Error saving file: " + e.getMessage());
//        }
//    }

}
