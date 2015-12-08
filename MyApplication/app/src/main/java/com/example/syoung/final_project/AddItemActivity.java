package com.example.syoung.final_project;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

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
    //private Button submitButton;

    private DatePickerDialog purchaseDatePickerDialog;
    private DatePickerDialog expirationDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    //private OnClickListener clearButtonListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        dateFormatter = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
        findViewsById();
        setDateTimeField();
        //setButtonField();
    }

    public void ClearButtonOnClick(View v){
        itemNameEtxt.setText("");
    }


    private void findViewsById(){
        purchaseDateEtxt = (EditText) findViewById(R.id.purchaseDateField);
        purchaseDateEtxt.setInputType(InputType.TYPE_NULL);
        expirationDateEtxt = (EditText) findViewById(R.id.expirationDateTextField);
        expirationDateEtxt.setInputType(InputType.TYPE_NULL);
        purchaseDateButton = findViewById(R.id.datePurchaseButton);
        expirationsButton = findViewById(R.id.dateExpirationButton);
        clearButton = (Button)findViewById(R.id.clearButton);
        //submitButton = (Button)findViewById(R.id.submitButton);
        itemNameEtxt = (EditText) findViewById(R.id.addItemTextField);
        //itemNameEtxt.setInputType(InputType.TYPE_NULL); //may not need
        itemQuantityEtxt = (EditText) findViewById(R.id.quantityTextField);
        //itemQuantityEtxt.setInputType(InputType.TYPE_NULL); //may not need
    }

    private void setButtonField() {
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClearButtonOnClick(v);
            }
        });
        //submitButton.setOnClickListener();


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
            }
        }, newCal.get(Calendar.YEAR), newCal.get(Calendar.MONTH), newCal.get(Calendar.DAY_OF_MONTH));

        purchaseDatePickerDialog = new DatePickerDialog(this, new OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                purchaseDateEtxt.setText(dateFormatter.format(newDate.getTime()));
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
}
