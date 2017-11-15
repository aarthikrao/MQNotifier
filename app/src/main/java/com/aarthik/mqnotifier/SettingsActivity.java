package com.aarthik.mqnotifier;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    Button submit;
    EditText mqip;
    EditText mqport;
    EditText username;
    EditText password;
    EditText queuename;
    EditText textToLook;
    private SharedPreferences mPrefs;
    private SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mPrefs = getSharedPreferences("MQNotifier", MODE_PRIVATE);
        mEditor = mPrefs.edit();
        submit = (Button) findViewById(R.id.submit);
        mqip = (EditText) findViewById(R.id.mqip);
        mqip.setText(mPrefs.getString("mqip","192.168.1.7"));
        mqport = (EditText) findViewById(R.id.mqport);
        mqport.setText(mPrefs.getString("mqport","5672"));
        username = (EditText) findViewById(R.id.username);
        username.setText(mPrefs.getString("username","admin"));
        password = (EditText) findViewById(R.id.password);
        password.setText(mPrefs.getString("password","admin"));
        queuename = (EditText) findViewById(R.id.queuename);
        textToLook = (EditText) findViewById(R.id.textToLook);
        submit.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        mEditor.putString("mqip", mqip.getText().toString());
        mEditor.putString("mqport", mqport.getText().toString());
        mEditor.putString("username", username.getText().toString());
        mEditor.putString("password", password.getText().toString());
        mEditor.putString("queuename", queuename.getText().toString());
        mEditor.putString("textToLook", textToLook.getText().toString());
        mEditor.commit();
        Log.i("Submit", "Connecting to server");
        finish();
    }
}
