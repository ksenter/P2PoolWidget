package com.kensenter.p2poolwidget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RemoteViews;

public class MainActivity extends Activity {
    public EditText ServerText = null;
    public EditText PortText = null;
    public EditText AlertRate = null;
    public EditText DOARate = null;
    public EditText PayKey = null;
    public CheckBox AlertOn = null;
    public CheckBox DOAOn = null;
    public CheckBox RemoveLine = null;
    public Spinner HashLevel = null;
    static String SERVER_NAME,PAY_KEY;
    static Integer PORT_VALUE,DOA_VALUE,ALERT_VALUE,HASH_LEVEL;
    static boolean ALERT_ON,DOA_ON,REMOVE_LINE;
    public static final String PREFS_NAME = "p2poolwidgetprefs";
    Button button ;

    int thisWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.button1);

        setResult(RESULT_CANCELED); //in case its closed before done configuring

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertRate = (EditText) findViewById(R.id.editText3);
                HashLevel = (Spinner) findViewById(R.id.spinner1);

            	if (Integer.parseInt(AlertRate.getText().toString()) >79 && HashLevel.getSelectedItemPosition()>=3)
            	{
                 	CharSequence text = "Hashrate alert only works with 79GH or less";
                 	int duration = Toast.LENGTH_LONG;

                 	Toast toast = Toast.makeText(MainActivity.this, text, duration);
                 	toast.show();

            	} else {
            		//finish();
                    //setResultDataToWidget(RESULT_OK);

                    // We need an Editor object to make preference changes.
                    // All objects are from android.context.Context
                    SharedPreferences settings = getSharedPreferences(PREFS_NAME+thisWidgetId, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("servername", ServerText.getText().toString());
                    editor.putInt("portnum", Integer.parseInt(PortText.getText().toString()));
                    editor.putInt("alertnum", Integer.parseInt(AlertRate.getText().toString()));
                    editor.putInt("doanum", Integer.parseInt(DOARate.getText().toString()));
                    editor.putBoolean("alerton", AlertOn.isChecked());
                    editor.putBoolean("doaon", DOAOn.isChecked());
                    editor.putString("paykey", PayKey.getText().toString());
                    editor.putInt("hashlevel", HashLevel.getSelectedItemPosition());
                    editor.putBoolean("removeline", RemoveLine.isChecked());

                    // Commit the edits!
                    editor.commit();

                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
                    RemoteViews views = new RemoteViews(getApplicationContext().getPackageName(),
                            R.layout.widget_main );

                    appWidgetManager.updateAppWidget(thisWidgetId, views);

                    Intent intent = new Intent(getApplicationContext(),p2pWidget.class);
                    intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
// Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
// since it seems the onUpdate() is only fired on that:
                    int[] ids = {thisWidgetId};
                    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);

                    sendBroadcast(intent);

                    setResultDataToWidget(RESULT_OK);
            	}
            }
        });

        getIdOfCurrentWidget(savedInstanceState);
        
        TextView t4 = (TextView) findViewById(R.id.textView4);
        t4.setMovementMethod(LinkMovementMethod.getInstance());
        
     // Restore preferences
        GetPrefs prefs = new GetPrefs();
        
        SERVER_NAME = prefs.GetServer(this.getApplicationContext(),thisWidgetId);
        PORT_VALUE = prefs.getPort(this.getApplicationContext(),thisWidgetId);
        ALERT_VALUE = prefs.getAlertRate(this.getApplicationContext(),thisWidgetId);
        DOA_VALUE = prefs.getDOARate(this.getApplicationContext(),thisWidgetId);
        ALERT_ON = prefs.getAlertOn(this.getApplicationContext(),thisWidgetId);
        DOA_ON = prefs.getDOAOn(this.getApplicationContext(),thisWidgetId);
        PAY_KEY = prefs.getPayKey(this.getApplicationContext(),thisWidgetId);
        HASH_LEVEL = prefs.getHashLevel(this.getApplicationContext(),thisWidgetId);
        REMOVE_LINE = prefs.getRemoveLine(this.getApplicationContext(),thisWidgetId);
        
        ServerText = (EditText) findViewById(R.id.editText1);
        PortText = (EditText) findViewById(R.id.editText2);
        AlertRate = (EditText) findViewById(R.id.editText3);
        DOARate = (EditText) findViewById(R.id.editText4);
        AlertOn = (CheckBox) findViewById(R.id.checkBox1);
        DOAOn = (CheckBox) findViewById(R.id.checkBox2);
        PayKey = (EditText) findViewById(R.id.editText12);
        HashLevel = (Spinner) findViewById(R.id.spinner1);
        RemoveLine = (CheckBox) findViewById(R.id.checkBox3);
        
        
        ServerText.setText(SERVER_NAME,EditText.BufferType.EDITABLE);
        PortText.setText(PORT_VALUE.toString(),EditText.BufferType.EDITABLE);
        AlertRate.setText(ALERT_VALUE.toString(),EditText.BufferType.EDITABLE);
        DOARate.setText(DOA_VALUE.toString(),EditText.BufferType.EDITABLE);
        AlertOn.setChecked(ALERT_ON);
        DOAOn.setChecked(DOA_ON);
        PayKey.setText(PAY_KEY,EditText.BufferType.EDITABLE);
        HashLevel.setSelection(HASH_LEVEL);
        RemoveLine.setChecked(REMOVE_LINE);
        
    }



    void setResultDataToWidget(int result) {
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, thisWidgetId);
        setResult(result, resultValue);
        finish();
    }


    /** Get the Id of Current Widget from the intent of the Widget **/
    void getIdOfCurrentWidget(Bundle savedInstanceState) {

        setResult(RESULT_CANCELED);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            thisWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            if (getWidgetData("Widget" + thisWidgetId) != null) {
                button.setText("Update");
                //ed.append(getWidgetData("Widget" + thisWidgetId));
            }

            //widgetId.setText("Widget ID = " + thisWidgetId);
        }

        // If they gave us an intent without the widget id, just bail.
        if (thisWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {

            Toast toast = Toast.makeText(this.getApplicationContext(), "Please add a P2Pool widget to the home screen", 18);
            toast.show();
            finish();
        }

    }


    public String getWidgetData(String file_name) {
        GetPrefs prefs = new GetPrefs();
        return (prefs.GetWidget(this.getApplicationContext(), thisWidgetId));
    }

    @Override
    protected void onStop(){
       super.onStop();


    }

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    
    }
