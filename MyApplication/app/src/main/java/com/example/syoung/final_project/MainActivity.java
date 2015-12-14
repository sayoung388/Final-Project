package com.example.syoung.final_project;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;



public class MainActivity extends Activity implements OnItemClickListener {

    private ListView mainListView;
    private Context context = this;
    private EditText settingsEtxt;
    private ListView expiringItemsListView;
    private ArrayList<FoodStorageDataModel> foodStorageList;
    private String settings;
    private String mainListArray[]={"Food Storage", "Budget", "Expiring Food", "Settings"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainListView = (ListView)findViewById(R.id.listView);
        mainListView.setAdapter(new ArrayAdapter<String>(this, R.layout.menu_text, mainListArray));
        mainListView.setOnItemClickListener(this);
        loadFoodStorage();
        loadSettings();

        //checks to see if you have set up the expiration date range
        if(settings != null && !"".equals(settings)){
            foodStorageExpirationNotification();
        }
    }

    /**
     * generates a list of items that are about to expire
     */
    private void foodStorageExpirationNotification() {
        String[] expiringItemsList;
        ArrayList<String> expiringItemArrayList = new ArrayList<>();
        loadFoodStorage();
        if(foodStorageList != null && settings != null && !"".equals(settings)) {
            for (FoodStorageDataModel item : foodStorageList) {
                if (compareDate(item)) {
                    expiringItemArrayList.add(item.getItemName());
                }
            }
            expiringItemsList = new String[expiringItemArrayList.size()];
            for (int i = 0; i < expiringItemArrayList.size(); i++) {
                expiringItemsList[i] = expiringItemArrayList.get(i);
            }

            setUpExpiringItemsDialog(expiringItemsList);
        }
        else {
            Toast.makeText(getApplicationContext(), "You must first choose your date range and add items to your food storage",
                Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * initializes the expiring items Dialog
     * @param expiringItemList
     */
    private void setUpExpiringItemsDialog(String[] expiringItemList) {
        Dialog expiringItemsDialog = new Dialog(context);
        expiringItemsDialog.setContentView(R.layout.expiringitemslayout);
        expiringItemsDialog.setTitle("Expiring Food Items");
        setUpExpiringItemsComponents(expiringItemsDialog, expiringItemList);
    }

    /**
     * initializes the components for the expiring items dialog
     * @param expiringItemsDialog
     * @param expiringItemList
     */
    private void setUpExpiringItemsComponents(final Dialog expiringItemsDialog, String[] expiringItemList) {
        expiringItemsListView = (ListView) expiringItemsDialog.findViewById(R.id.expiring_item_list);
        expiringItemsListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1 ,expiringItemList));
        Button expiringItemsOkButton = (Button) expiringItemsDialog.findViewById(R.id.expiringItemsButton);

        expiringItemsOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expiringItemsDialog.dismiss();
            }
        });

        expiringItemsDialog.show();
    }

    /**
     * checks to see if the date of a food item is within the tolerance
     * @param item
     * @return
     */
    private boolean compareDate(FoodStorageDataModel item){
        Calendar currentDate = Calendar.getInstance();
        int settingsDate = Integer.parseInt(settings);
        currentDate.add(Calendar.DATE, settingsDate);

        if(currentDate.getTimeInMillis() > item.getExpirationDateValue().getTime()) {
            return true;
        }

        return false;
    }

    /**
     * checks to see which menu item is pressed and then moves them to the new activity or dialog
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
//                Toast.LENGTH_SHORT).show();
        if("Food Storage".equals(((TextView) view).getText())){
            Intent foodStorage = new Intent(getApplicationContext(), FoodStorageActivity.class);
            foodStorage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            foodStorage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(foodStorage);
        }
        else if("Budget".equals(((TextView) view).getText())){
            Intent budget = new Intent(getApplicationContext(), BudgetActivity.class);
            budget.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            budget.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(budget);
        }
        else if("Settings".equals(((TextView) view).getText())){
            setUpSettingsDialog();
        }
        else if("Expiring Food".equals(((TextView) view).getText())){
            foodStorageExpirationNotification();
        }
    }

    /**
     * initialize the dialog for setting the range you want to see if something is going to expire
     */
    private void setUpSettingsDialog() {
        Dialog settingsDialog = new Dialog(context);
        settingsDialog.setContentView(R.layout.settingslayout);
        settingsDialog.setTitle("Settings");
        setUpSettingsComponents(settingsDialog);
    }

    /**
     * initializes the components on the settings dialog
     * @param settingsDialog
     */
    private void setUpSettingsComponents(final Dialog settingsDialog) {
        settingsEtxt = (EditText) settingsDialog.findViewById(R.id.settingsDateRange);
        settingsEtxt.setInputType(InputType.TYPE_CLASS_NUMBER);
        //if it isn't null then it will place the value in the text field
        if(settings != null && !"".equals(settings)){
            settingsEtxt.setText(settings);
        }

        //sets the buttons and what they do if they are pressed
        Button setButton = (Button) settingsDialog.findViewById(R.id.settingsSetButton);
        Button cancelButton = (Button) settingsDialog.findViewById(R.id.settingsCancelButton);

        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if there is no value in the textbox it will make you set one before it will set it.
                if("".equals(settingsEtxt.getText().toString())){
                    Toast.makeText(getApplicationContext(), "Enter the number of days in advance you want to know if something is going to expire.",
                            Toast.LENGTH_SHORT).show();
                }else {
                    settings = settingsEtxt.getText().toString();
                    saveSettings();
                    settingsDialog.dismiss();
                }

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsDialog.dismiss();
            }
        });

        settingsDialog.show();
    }


    /**
     * saves the settings values
     */
    private void saveSettings(){

        File file = new File(getFilesDir(), "settings.txt");
        Gson gson = new Gson();
        String saveList = gson.toJson(settings);
        try {
            FileWriter writer = new FileWriter(file);
            BufferedWriter buf = new BufferedWriter(writer);
            buf.write(saveList); //does this need to be append or write?
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * loads the food storage values
     */
    protected void loadFoodStorage() {

        try{
            File file = new File(getFilesDir(), "foodstorage.txt");
            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);
            String jsonString = reader.readLine();

            Gson gson = new Gson();
            Type collectionType = new TypeToken<ArrayList<FoodStorageDataModel>>(){}.getType();
            ArrayList<FoodStorageDataModel> items = gson.fromJson(jsonString, collectionType);
            foodStorageList = new ArrayList<>();
            foodStorageList = items;

        } catch (Exception e) {
            Log.e("Persistence", "Error saving file: " + e.getMessage());
        }
    }

    /**
     * loads settings value
     */
    protected void loadSettings() {

        try{
            File file = new File(getFilesDir(), "settings.txt");
            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);
            String jsonString = reader.readLine();

            Gson gson = new Gson();
            Type collectionType = new TypeToken<String>(){}.getType();
            String items = gson.fromJson(jsonString, collectionType);

            settings = items;

        } catch (Exception e) {
            Log.e("Persistence", "Error saving file: " + e.getMessage());
        }
    }

}
