package com.example.syoung.final_project;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements OnItemClickListener {

    private ListView mainListView;
    private LinearLayout mainLinearLayout;
    private String mainListArray[]={"Food Storage", "Budget", "Settings"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //mainLinearLayout = (LinearLayout)findViewById(R.id.mainMenuLayout);
        mainListView = (ListView)findViewById(R.id.listView);
        mainListView.setAdapter(new ArrayAdapter<String>(this, R.layout.menu_text, mainListArray));
        mainListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
                Toast.LENGTH_SHORT).show();
    }
}
