package com.example.syoung.final_project;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class BudgetActivity extends Activity implements AdapterView.OnItemClickListener {

    private String[] budgetMenu = {"Net Income", "Add Bill", "View/Edit Bill", "Budget Overview"};
    private ListView budgetList;
    private EditText income_textEtxt;
    private EditText budgetOverview_incomeEtxt;
    private EditText budgetOverview_BillsEtxt;
    private EditText budgetOverview_TotalEtxt;
    private EditText biller_NameEtxt;
    private EditText bill_AmountEtxt;
    private EditText bill_DueDateEtxt;
    private Context context = this;
    private IncomeModel netIncomeAmount;
    private ArrayList<BillModel> bills;
    private BillModel newBill;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);
        budgetList = (ListView)findViewById(R.id.budgetListView);
        budgetList.setAdapter(new ArrayAdapter<String>(this, R.layout.menu_text, budgetMenu));
        budgetList.setOnItemClickListener(this);
        newBill = new BillModel();
        loadIncome();
        loadBills();

        if(netIncomeAmount == null || netIncomeAmount.getStringIncome() == null){
            setUpIncomeDialog();
        }
    }


    private void setUpIncomeDialog() {
        final Dialog incomeDialog = new Dialog(context);
        incomeDialog.setContentView(R.layout.incomealertlayout);
        incomeDialog.setTitle("Net Monthly Income");
        setUpIncomeDialogComponents(incomeDialog);
    }

    private void setUpAddBillDialog() {
        Dialog addBillDialog = new Dialog(context);
        addBillDialog.setContentView(R.layout.add_bill_layout);
        addBillDialog.setTitle("Add Bill");
        setUpAddBillComponents(addBillDialog);

    }

    private void setUpBudgetOverviewDialog() {
        Dialog budgetOverview = new Dialog(context);
        budgetOverview.setContentView(R.layout.budget_overview_layout);
        budgetOverview.setTitle("Budget Overview");
        setUpBudgetOverviewComponents(budgetOverview);

    }

    private void setUpBudgetOverviewComponents(final Dialog budgetOverviewDialog) {
        budgetOverview_incomeEtxt = (EditText) budgetOverviewDialog.findViewById(R.id.billoverviewincome);
        NumberFormat formatDouble = new DecimalFormat("#0.00");
        budgetOverview_incomeEtxt.setText("$" + formatDouble.format(netIncomeAmount.getIncome()));
        budgetOverview_incomeEtxt.setEnabled(false);
        budgetOverview_incomeEtxt.setTextColor(Color.BLACK);

        budgetOverview_BillsEtxt = (EditText) budgetOverviewDialog.findViewById(R.id.billsoverview);
        double billTotalAmount = getBillsTotal();
        budgetOverview_BillsEtxt.setText("$" + formatDouble.format(billTotalAmount));
        budgetOverview_BillsEtxt.setEnabled(false);
        budgetOverview_BillsEtxt.setTextColor(Color.BLACK);

        budgetOverview_TotalEtxt = (EditText) budgetOverviewDialog.findViewById(R.id.leftoverincome);
        budgetOverview_TotalEtxt.setText("$" + formatDouble.format(getAvailableFunds(billTotalAmount)));
        budgetOverview_TotalEtxt.setEnabled(false);
        budgetOverview_TotalEtxt.setTextColor(Color.BLACK);

        Button okButton = (Button) budgetOverviewDialog.findViewById(R.id.budgetoverviewokbutton);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                budgetOverviewDialog.dismiss();
            }
        });

        budgetOverviewDialog.show();

    }

    private double getAvailableFunds(double billTotalAmount) {

        double availableFunds = netIncomeAmount.getIncome() - billTotalAmount;
        return availableFunds;
    }

    private double getBillsTotal() {
        double billTotal = 0.00;

        for(BillModel bill : bills){
            double tempBill = bill.getBillAmount();
            billTotal += tempBill;
        }

        return billTotal;
    }

    private void setUpAddBillComponents(final Dialog addBillDialog) {
        biller_NameEtxt = (EditText) addBillDialog.findViewById(R.id.billerName);
        bill_AmountEtxt = (EditText) addBillDialog.findViewById(R.id.billAmount);
        bill_AmountEtxt.setInputType(InputType.TYPE_CLASS_NUMBER);
        bill_DueDateEtxt = (EditText) addBillDialog.findViewById(R.id.billDueDateEText);
        bill_DueDateEtxt.setInputType(InputType.TYPE_CLASS_NUMBER);
        Button bill_submit_button = (Button) addBillDialog.findViewById(R.id.bill_submit_button);
        Button bill_cancel_button = (Button) addBillDialog.findViewById(R.id.bill_cancel_button);


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

                if ("".equals(biller_NameEtxt.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Enter a biller name",
                            Toast.LENGTH_SHORT).show();
                } else if (checkNameAlreadyExisting()) {
                    Toast.makeText(getApplicationContext(), "Biller already exists. Update existing or create a unique name.",
                            Toast.LENGTH_SHORT).show();
                } else if ("".equals(bill_AmountEtxt.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Enter a bill amount",
                            Toast.LENGTH_SHORT).show();
                } else if ("".equals(bill_DueDateEtxt.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Enter a bill due date",
                            Toast.LENGTH_SHORT).show();
                } else {

                    newBill.setBillerName(biller_NameEtxt.getText().toString());
                    Double billAmount = Double.parseDouble(bill_AmountEtxt.getText().toString().replace("$", ""));
                    newBill.setBillAmount(billAmount);
                    newBill.setBillDueDateString(bill_DueDateEtxt.getText().toString());
                    if (bills == null) {
                        bills = new ArrayList<BillModel>();
                    }
                    bills.add(newBill);
                    saveBill();
                    newBill = new BillModel();
                    addBillDialog.dismiss();
                }
            }
        });

        bill_cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBillDialog.dismiss();
            }
        });

        addBillDialog.show();

    }

    private boolean checkNameAlreadyExisting() {

        for(BillModel bill : bills){
            if(biller_NameEtxt.getText().toString().equals(bill.getBillerName())){
                return true;
            }
        }

        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
                Toast.LENGTH_SHORT).show();
        if("Net Income".equals(((TextView) view).getText())){
            setUpIncomeDialog();

        }
        else if("Add Bill".equals(((TextView) view).getText())){
            setUpAddBillDialog();
        }
        else if("View/Edit Bill".equals(((TextView) view).getText())){
            Intent editViewBill = new Intent(getApplicationContext(), ViewEditBillActivity.class);
            editViewBill.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            editViewBill.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(editViewBill);
        }
        else if("Budget Overview".equals(((TextView) view).getText())){
            setUpBudgetOverviewDialog();
        }
    }


    private void setUpIncomeDialogComponents(final Dialog incomeDialog){
        income_textEtxt = (EditText) incomeDialog.findViewById(R.id.netincome);
        income_textEtxt.setInputType(InputType.TYPE_CLASS_NUMBER);
        Button income_submit_button = (Button) incomeDialog.findViewById(R.id.income_submit_button);
        Button income_cancel_button = (Button) incomeDialog.findViewById(R.id.income_cancel_button);


        income_textEtxt.addTextChangedListener(new TextWatcher() {
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
                    income_textEtxt.removeTextChangedListener(this);
                    income_textEtxt.setText(netIncomeSB.toString());
                    income_textEtxt.setTextKeepState("$" + netIncomeSB.toString());
                    Selection.setSelection(income_textEtxt.getText(), netIncomeSB.toString().length() + 1);
                    income_textEtxt.addTextChangedListener(this);

                }
            }
        });

        income_submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!"".equals(income_textEtxt.getText().toString())) {
                    Double amount = Double.parseDouble(income_textEtxt.getText().toString().replace("$" ,""));
                    netIncomeAmount = new IncomeModel();
                    netIncomeAmount.setIncome(amount);
                    saveIncome();
                    incomeDialog.dismiss();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Please add your net monthly income",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        income_cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(netIncomeAmount == null || netIncomeAmount.getStringIncome() == null){
                    incomeDialog.dismiss();
                    Intent mainmenu = new Intent(getApplicationContext(), MainActivity.class);
                    mainmenu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mainmenu.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(mainmenu);
                }

                incomeDialog.dismiss();
            }
        });

        incomeDialog.show();

    }

    private void saveIncome() {
        File file = new File(getFilesDir(), "net_income.txt");
        Gson gson = new Gson();
        String saveList = gson.toJson(netIncomeAmount);
        try {
            FileWriter writer = new FileWriter(file);
            BufferedWriter buf = new BufferedWriter(writer);
            buf.write(saveList); //does this need to be append or write?
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    protected void loadIncome() {

        try{
            File file = new File(getFilesDir(), "net_income.txt");
            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);
            String jsonString = reader.readLine();

            Gson gson = new Gson();
            Type collectionType = new TypeToken<IncomeModel>(){}.getType();
            IncomeModel items = gson.fromJson(jsonString, collectionType);

            netIncomeAmount = items;

        } catch (Exception e) {
            Log.e("Persistence", "Error saving file: " + e.getMessage());
        }
    }
}
