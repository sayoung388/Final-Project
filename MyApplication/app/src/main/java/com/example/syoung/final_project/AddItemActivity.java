package com.example.syoung.final_project;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
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
        dateFormatter = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
        foodStorageDataModel = new FoodStorageDataModel();
        findViewsById();
        setDateTimeField();
        setButtonField();
    }

    private void ClearButtonOnClick(View v){
        itemNameEtxt.setText("");
        itemQuantityEtxt.setText("");
        purchaseDateEtxt.setText("");
        expirationDateEtxt.setText("");
    }


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

    private void SubmitButtonOnClick(View v) {
        if("".equals(itemNameEtxt.getText().toString())){
            Toast.makeText(getApplicationContext(), "Insert Item Name",
                    Toast.LENGTH_SHORT).show();
        }
        else if("".equals(itemQuantityEtxt.getText().toString())){
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
            foodStorageDataModel.setItemName(itemNameEtxt.getText().toString());
            foodStorageDataModel.setItemQuantity(itemQuantityEtxt.getText().toString());

            //todo store data into file
            ClearButtonOnClick(v); //reset the buttons
            Toast.makeText(getApplicationContext(), "Item Submitted",
                    Toast.LENGTH_SHORT).show();
        }
    }

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
                foodStorageDataModel.setExpirationDate(newDate.getTime());
            }
        }, newCal.get(Calendar.YEAR), newCal.get(Calendar.MONTH), newCal.get(Calendar.DAY_OF_MONTH));

        purchaseDatePickerDialog = new DatePickerDialog(this, new OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                purchaseDateEtxt.setText(dateFormatter.format(newDate.getTime()));
                foodStorageDataModel.setPurchaseDate(newDate.getTime());
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

//    private void resave() {
//        try {
//            File file = new File(getFilesDir(), "currentLevel.txt");
//            if (file.exists()) {
//                file.delete();
//            }
//
//            FileWriter writer = new FileWriter(file);
//            writer.append(currentLevel);
//            writer.flush();
//            writer.close();
//        } catch (IOException e) {
//            Log.e("Save", "IOException Error: " + e);
//        }
//    }
}
