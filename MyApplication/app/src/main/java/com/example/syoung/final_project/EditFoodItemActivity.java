package com.example.syoung.final_project;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class EditFoodItemActivity extends Activity implements View.OnClickListener {

    private EditText purchaseDateEtxt;
    private EditText expirationDateEtxt;
    private EditText itemQuantityEtxt;
    private EditText itemNameEtxt;
    private View purchaseDateButton;
    private View expirationsButton;
    private Button clearButton;
    private Button updateButton;
    private Button deleteButton;
    private FoodStorageDataModel editItemModel;
    private DatePickerDialog purchaseDatePickerDialog;
    private DatePickerDialog expirationDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    private ArrayList<FoodStorageDataModel> foodStorageList;


//    @Override
//    public void onBackPressed() {
//        Intent findItem = new Intent(getApplicationContext(), FindItemActivity.class);
//        startActivity(findItem);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_food_item);
        String itemName = getIntent().getExtras().getString("ItemName");
        foodStorageList = new ArrayList<>();
        loadFileData();
        dateFormatter = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
        editItemModel = new FoodStorageDataModel();
        findViewsById();
        setDateTimeField();
        setButtonField();
        setTextValues(itemName);

    }

    private void setTextValues(String itemName){
        for(FoodStorageDataModel itemValue: foodStorageList){
            if(itemValue.getItemName().equals(itemName)){
                itemNameEtxt.setText(itemValue.getItemName());
                itemQuantityEtxt.setText(itemValue.getItemQuantity());
                purchaseDateEtxt.setText(itemValue.getPurchaseDate());
                expirationDateEtxt.setText(itemValue.getExpirationDate());
                break;
            }
        }
    }



    private void findViewsById(){
        purchaseDateEtxt = (EditText) findViewById(R.id.purchaseEditDateField);
        purchaseDateEtxt.setInputType(InputType.TYPE_NULL);
        expirationDateEtxt = (EditText) findViewById(R.id.expirationEditDateTextField);
        expirationDateEtxt.setInputType(InputType.TYPE_CLASS_NUMBER);
        purchaseDateButton = findViewById(R.id.dateEditPurchaseButton);
        expirationsButton = findViewById(R.id.dateEditExpirationButton);
        clearButton = (Button)findViewById(R.id.clearEditButton);
        updateButton = (Button)findViewById(R.id.updateEditButton);
        deleteButton = (Button)findViewById(R.id.deleteEditButton);
        itemNameEtxt = (EditText) findViewById(R.id.addItemEditTextField);
        itemQuantityEtxt = (EditText) findViewById(R.id.quantityEditTextField);
    }


    private void setDateTimeField(){
        purchaseDateEtxt.setOnClickListener(this);
        expirationDateEtxt.setOnClickListener(this);
        purchaseDateButton.setOnClickListener(this);
        expirationsButton.setOnClickListener(this);
        Calendar newCal = Calendar.getInstance();

        expirationDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                expirationDateEtxt.setText(dateFormatter.format(newDate.getTime()));
                editItemModel.setExpirationDateValue(newDate.getTime());
            }
        }, newCal.get(Calendar.YEAR), newCal.get(Calendar.MONTH), newCal.get(Calendar.DAY_OF_MONTH));

        purchaseDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                purchaseDateEtxt.setText(dateFormatter.format(newDate.getTime()));
                editItemModel.setPurchaseDateValue(newDate.getTime());
            }
        }, newCal.get(Calendar.YEAR), newCal.get(Calendar.MONTH), newCal.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onClick(View v) {
        if(v == expirationDateEtxt || v == expirationsButton) {
            expirationDatePickerDialog.show();
        } else if(v == purchaseDateEtxt || v == purchaseDateButton) {
            purchaseDatePickerDialog.show();
        }
    }


    protected void loadFileData() {
        try{
            File file = new File(getFilesDir(), "foodstorage.txt");
            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);
            String jsonString = reader.readLine();

            Gson gson = new Gson();
            Type collectionType = new TypeToken<ArrayList<FoodStorageDataModel>>(){}.getType();
            ArrayList<FoodStorageDataModel> items = gson.fromJson(jsonString, collectionType);
            foodStorageList = items;

        }catch (Exception e){
            Log.e("Persistence", "Error saving file: " + e.getMessage());
        }
    }

    private void setButtonField() {

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearButtonOnClick(v);
            }
        });
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateButtonOnClick(v);
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteButtonOnClick(v);
            }
        });


    }

    private void clearButtonOnClick(View v){
        itemQuantityEtxt.setText("");
        purchaseDateEtxt.setText("");
        expirationDateEtxt.setText("");
    }

    private void deleteButtonOnClick(View v) {
        final AlertDialog.Builder inputAlert = new AlertDialog.Builder(v.getContext());
        inputAlert.setTitle("Delete Item");
        inputAlert.setMessage("Are you sure you want to delete " + itemNameEtxt.getText().toString() + "?");
        inputAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i = 0; i < foodStorageList.size(); i++) {
                    if (foodStorageList.get(i).getItemName().equals(itemNameEtxt.getText().toString())) {
                        foodStorageList.remove(i);
                        saveFileList();
                        Intent findItem = new Intent(getApplicationContext(), FindItemActivity.class);
                        findItem.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        findItem.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(findItem);
                    }
                }
            }
        });

        inputAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        inputAlert.show();
    }

    private void updateButtonOnClick(View v) {

        if(!"".equals(itemQuantityEtxt.getText().toString())){
            if(!quantityContainsNumber()) {
                Toast.makeText(getApplicationContext(), "please insert a number first in quantity text",
                        Toast.LENGTH_SHORT).show();
            }
        }

        if("".equals(itemQuantityEtxt.getText().toString())){
            Toast.makeText(getApplicationContext(), "Insert Item Quantity",
                    Toast.LENGTH_SHORT).show();
        }
        else if("".equals(purchaseDateEtxt.getText().toString())){
            Toast.makeText(getApplicationContext(), "Insert Purchase Date",
                    Toast.LENGTH_SHORT).show();
        }
        else if("".equals(expirationDateEtxt.getText().toString())){
            Toast.makeText(getApplicationContext(), "Insert Expiration Date",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            editItemModel = new FoodStorageDataModel();
            editItemModel.setItemName(itemNameEtxt.getText().toString());
            editItemModel.setItemQuantity(itemQuantityEtxt.getText().toString());
            editItemModel.setExpirationDate(expirationDateEtxt.getText().toString());
            editItemModel.setPurchaseDate(purchaseDateEtxt.getText().toString());
            for(int i = 0; i < foodStorageList.size(); i++){
                if(foodStorageList.get(i).getItemName().equals(editItemModel.getItemName())){
                    foodStorageList.remove(i);
                    break;
                }
            }

            foodStorageList.add(editItemModel);
            saveFileList();
            Toast.makeText(getApplicationContext(), "Item Updated",
                    Toast.LENGTH_SHORT).show();

        }
    }


    private void saveFileList(){

        File file = new File(getFilesDir(), "foodstorage.txt");
        Gson gson = new Gson();
        String saveList = gson.toJson(foodStorageList);
        try {
            FileWriter writer = new FileWriter(file);
            BufferedWriter buf = new BufferedWriter(writer);
            buf.write(saveList); //does this need to be append or write?
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean quantityContainsNumber(){

        for(char value : itemQuantityEtxt.getText().toString().toCharArray()){
            if(Character.isDigit(value)){
                return true;
            }
            return false;
        }

        return false;
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
}
