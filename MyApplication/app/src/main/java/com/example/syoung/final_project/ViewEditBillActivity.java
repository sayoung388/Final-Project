package com.example.syoung.final_project;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
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
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;

public class ViewEditBillActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private SearchView searchView;
    private ArrayList<BillModel> bills;
    private ArrayList<BillModel> partialBills;
    private ListView itemListView;
    private AdapterView.OnItemClickListener listener;
    private Context context = this;
    private EditText income_textEtxt;
    private EditText biller_NameEtxt;
    private EditText bill_AmountEtxt;
    private EditText bill_DueDateEtxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_edit_bill);
        loadBills();
        partialBills = new ArrayList<>();
        partialBills = bills;
        findViewsById();
        setSearch();
        setListAdapter();
    }


    private void findViewsById(){
        searchView = (SearchView) findViewById(R.id.bill_search_button);
        itemListView = (ListView) findViewById(R.id.bill_item_list);
    }

    private void setListAdapter(){

        listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String selectedItemName = ((TextView) view).getText().toString();
                editBill(selectedItemName);

            }
        };
        ArrayList<String> itemNames = new ArrayList<String>();
        String itemNamesArray[] = new String[0];
        if(partialBills.size() > 0) {
            for (BillModel model : partialBills) {
                itemNames.add(model.getBillerName());
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

    private void editBill(String selectedItem) {
        setUpEditBillDialog(selectedItem);
    }

    private void setUpEditBillDialog(String selectedItem) {
        Dialog editBillDialog = new Dialog(context);
        editBillDialog.setContentView(R.layout.add_bill_layout);
        editBillDialog.setTitle("Add Bill");
        setUpEditBillComponents(editBillDialog, selectedItem);
    }

    private void setUpEditBillComponents(final Dialog editBillDialog, String selectedItemName) {
        biller_NameEtxt = (EditText) editBillDialog.findViewById(R.id.billerName);
        bill_AmountEtxt = (EditText) editBillDialog.findViewById(R.id.billAmount);
        bill_AmountEtxt.setInputType(InputType.TYPE_CLASS_NUMBER);
        bill_DueDateEtxt = (EditText) editBillDialog.findViewById(R.id.billDueDateEText);
        bill_DueDateEtxt.setInputType(InputType.TYPE_CLASS_NUMBER);
        Button bill_submit_button = (Button) editBillDialog.findViewById(R.id.bill_submit_button);
        Button bill_cancel_button = (Button) editBillDialog.findViewById(R.id.bill_cancel_button);

        biller_NameEtxt.setEnabled(false); //disable so it can't be changed
        for(BillModel model : bills){
            if(model.getBillerName().equals(selectedItemName)){
                biller_NameEtxt.setText(model.getBillerName());
                NumberFormat formatDouble = new DecimalFormat("#0.00");
                bill_AmountEtxt.setText("$" + formatDouble.format(model.getBillAmount()));
                bill_DueDateEtxt.setText(model.getBillDueDateString());
                break;
            }
        }

        bill_AmountEtxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //check to see if the input matches our format requirement.
                //if it doesn't then make it match
                if (!s.toString().matches("^\\$(\\d{1,3}(\\,\\d{3})*|(\\d+))(\\.\\d{2})?$")) {
                    //replace any none number characters with empty
                    String editTextInput = "" + s.toString().replaceAll("[^\\d]", "");
                    StringBuilder netIncomeSB = new StringBuilder(editTextInput);

                    //get rid of leading 0s
                    while (netIncomeSB.length() > 3 && netIncomeSB.charAt(0) == '0') {
                        netIncomeSB.deleteCharAt(0);
                    }

                    //if it is smaller than 3 insert a 0 in front
                    //for decimal places
                    while (netIncomeSB.length() < 3) {
                        netIncomeSB.insert(0, '0');
                    }
                    //insert the . where the decimal location should be
                    netIncomeSB.insert(netIncomeSB.length() - 2, '.');
                    bill_AmountEtxt.removeTextChangedListener(this);
                    bill_AmountEtxt.setText(netIncomeSB.toString());
                    bill_AmountEtxt.setTextKeepState("$" + netIncomeSB.toString());
                    Selection.setSelection(bill_AmountEtxt.getText(), netIncomeSB.toString().length() + 1);
                    bill_AmountEtxt.addTextChangedListener(this);

                }
            }
        });


        bill_submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ("".equals(bill_AmountEtxt.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Enter a bill amount",
                            Toast.LENGTH_SHORT).show();
                } else if ("".equals(bill_DueDateEtxt.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Enter a bill due date",
                            Toast.LENGTH_SHORT).show();
                } else {
                    BillModel newBill = new BillModel();
                    newBill.setBillerName(biller_NameEtxt.getText().toString());
                    Double billAmount = Double.parseDouble(bill_AmountEtxt.getText().toString().replace("$", ""));
                    newBill.setBillAmount(billAmount);
                    newBill.setBillDueDateString(bill_DueDateEtxt.getText().toString());
                    for(int i = 0; i < bills.size(); i++){
                        if(bills.get(i).getBillerName().equals(newBill.getBillerName())){
                            bills.remove(i);
                            break;
                        }
                    }
                    bills.add(newBill);
                    saveBill();
                    editBillDialog.dismiss();
                }
            }
        });

        bill_cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editBillDialog.dismiss();
            }
        });

        editBillDialog.show();
    }

    private void setSearch(){

        searchView.setIconified(false);
        searchView.setOnQueryTextListener(this);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("Search Here");

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


    @Override
    public boolean onQueryTextSubmit(String query) {
        callSearch(query);
        return true;
    }

    private void callSearch(String query) {
        partialBills = new ArrayList<>();
        for (BillModel itemValue : bills) {
            if(itemValue.getBillerName().contains(query)){
                partialBills.add(itemValue);
            }
        }
        setListAdapter();
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if("".equals(newText)){
            partialBills = bills;
            setListAdapter();
        }
        return true;
    }


    private void saveBill() {
        File file = new File(getFilesDir(), "bills.txt");
        Gson gson = new Gson();
        String saveList = gson.toJson(bills);
        try {
            FileWriter writer = new FileWriter(file);
            BufferedWriter buf = new BufferedWriter(writer);
            buf.write(saveList); //does this need to be append or write?
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void loadBills() {

        try{
            File file = new File(getFilesDir(), "bills.txt");
            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);
            String jsonString = reader.readLine();

            Gson gson = new Gson();
            Type collectionType = new TypeToken<ArrayList<BillModel>>(){}.getType();
            ArrayList<BillModel> items = gson.fromJson(jsonString, collectionType);
            bills = new ArrayList<>();
            bills = items;

        } catch (Exception e) {
            Log.e("Persistence", "Error saving file: " + e.getMessage());
        }
    }

}
