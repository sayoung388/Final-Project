package com.example.syoung.final_project;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
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

public class AddItemActivity extends Activity implements OnClickListener {


    private EditText purchaseDateEtxt;
    private EditText expirationDateEtxt;
    private EditText itemQuantityEtxt;
    private EditText itemNameEtxt;
    private View purchaseDateButton;
    private View expirationsButton;
    private Button clearButton;
    private Button submitButton;
    private FoodStorageDataModel foodStorageDataModel;
    private DatePickerDialog purchaseDatePickerDialog;
    private DatePickerDialog expirationDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    private ArrayList<FoodStorageDataModel> foodStorageList;

    /**
     * if you click off of the keyboard it will close it
     * @param event
     * @return
     */
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        foodStorageList = new ArrayList<>();
        loadFile();
        dateFormatter = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
        foodStorageDataModel = new FoodStorageDataModel();

        findViewsById();
        setDateTimeField();
        setButtonField();
    }

    /**
     * clear all of the view values
     * @param v
     */
    private void ClearButtonOnClick(View v){
        itemNameEtxt.setText("");
        itemQuantityEtxt.setText("");
        purchaseDateEtxt.setText("");
        expirationDateEtxt.setText("");
    }

    /**
     * initializes all of the view items for the Add items view
     */
    private void findViewsById(){
        purchaseDateEtxt = (EditText) findViewById(R.id.purchaseDateField);
        purchaseDateEtxt.setInputType(InputType.TYPE_NULL);
        expirationDateEtxt = (EditText) findViewById(R.id.expirationDateTextField);
        expirationDateEtxt.setInputType(InputType.TYPE_CLASS_NUMBER);
        purchaseDateButton = findViewById(R.id.datePurchaseButton);
        expirationsButton = findViewById(R.id.dateExpirationButton);
        clearButton = (Button)findViewById(R.id.clearButton);
        submitButton = (Button)findViewById(R.id.submitButton);
        itemNameEtxt = (EditText) findViewById(R.id.addItemTextField);
        itemQuantityEtxt = (EditText) findViewById(R.id.quantityTextField);
    }

    /**
     * initializes what the buttons will do in the activity
     */
    private void setButtonField() {

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClearButtonOnClick(v);
            }
        });
        submitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SubmitButtonOnClick(v);
            }
        });


    }

    /**
     * This is what the submit button does
     * @param v
     */
    private void SubmitButtonOnClick(View v) {

        //make sure that there is a number first
        if(!"".equals(itemQuantityEtxt.getText().toString())){
            if(!quantityContainsNumber()) {
                Toast.makeText(getApplicationContext(), "please insert a number first in quantity text",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }

        //makes sure all of the fields are filled before it will submit the data
        if("".equals(itemNameEtxt.getText().toString())){
            Toast.makeText(getApplicationContext(), "Insert Item Name",
                    Toast.LENGTH_SHORT).show();
        }
        else if(checkExistingName()){
            Toast.makeText(getApplicationContext(), "Item with this name already exists; please give it a unique name",
                    Toast.LENGTH_SHORT).show();
        }
        else if("".equals(itemQuantityEtxt.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Insert Item Quantity",
                    Toast.LENGTH_SHORT).show();
        }
        else if("".equals(purchaseDateEtxt.getText().toString())){
            Toast.makeText(getApplicationContext(), "Insert Purchase Date",
                    Toast.LENGTH_SHORT).show();
        }
        else if ("".equals(expirationDateEtxt.getText().toString())){
            Toast.makeText(getApplicationContext(), "Insert Expiration Date",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            foodStorageDataModel.setItemName(itemNameEtxt.getText().toString());
            foodStorageDataModel.setItemQuantity(itemQuantityEtxt.getText().toString());
            foodStorageDataModel.setPurchaseDate(purchaseDateEtxt.getText().toString());
            foodStorageDataModel.setExpirationDate(expirationDateEtxt.getText().toString());
            foodStorageList.add(foodStorageDataModel);
            saveFileList();
            foodStorageDataModel = new FoodStorageDataModel();
            ClearButtonOnClick(v); //reset the buttons
            Toast.makeText(getApplicationContext(), "Item Submitted",
                    Toast.LENGTH_SHORT).show();
        }
    }

    //makes sure that there isn't already an item with this name
    private boolean checkExistingName(){
        if(foodStorageList != null && foodStorageList.size() > 0){
            for(FoodStorageDataModel model : foodStorageList){
                if(model.getItemName().equals(itemNameEtxt.getText().toString())){
                    return true;
                }
            }
            return false;
        }

        return false;
    }

    //sets up the dialog to choose a date value for expiration and purchase date
    private void setDateTimeField(){
        purchaseDateEtxt.setOnClickListener(this);
        expirationDateEtxt.setOnClickListener(this);
        purchaseDateButton.setOnClickListener(this);
        expirationsButton.setOnClickListener(this);
        Calendar newCal = Calendar.getInstance();

        expirationDatePickerDialog = new DatePickerDialog(this, new OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                expirationDateEtxt.setText(dateFormatter.format(newDate.getTime()));
                foodStorageDataModel.setExpirationDateValue(newDate.getTime());
            }
        }, newCal.get(Calendar.YEAR), newCal.get(Calendar.MONTH), newCal.get(Calendar.DAY_OF_MONTH));

        purchaseDatePickerDialog = new DatePickerDialog(this, new OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                purchaseDateEtxt.setText(dateFormatter.format(newDate.getTime()));
                foodStorageDataModel.setPurchaseDateValue(newDate.getTime());
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

    /**
     * saves the data for the food storage list
     */
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

    /**
     * loads the food storage list
     */
    protected void loadFile() {

        try{
            File file = new File(getFilesDir(), "foodstorage.txt");
            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);
            String jsonString = reader.readLine();

            Gson gson = new Gson();
            Type collectionType = new TypeToken<ArrayList<FoodStorageDataModel>>(){}.getType();
            ArrayList<FoodStorageDataModel> items = gson.fromJson(jsonString, collectionType);

            foodStorageList = items;

        } catch (Exception e) {
            Log.e("Persistence", "Error saving file: " + e.getMessage());
        }
    }


    /**
     * checks to make sure that the quantity text values starts with a number
     * @return
     */
    private boolean quantityContainsNumber(){

        for(char value : itemQuantityEtxt.getText().toString().toCharArray()){
            if(Character.isDigit(value)){
                return true;
            }
            return false;
        }

        return false;
    }
}
