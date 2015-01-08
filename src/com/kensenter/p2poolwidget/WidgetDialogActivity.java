package com.kensenter.p2poolwidget;

/**
 * Created by Ken on 9/10/13.
 */



        import android.appwidget.AppWidgetManager;
        import android.content.Intent;
        import android.os.Bundle;
        import android.view.View;
        import android.view.Window;
        import android.view.View.OnClickListener;
        import android.widget.Button;
        import android.widget.TextView;
        import android.app.Activity;

public class WidgetDialogActivity extends Activity {

    int thisWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    TextView txt18;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.widget_dialog);
        GetPrefs prefs = new GetPrefs();

        String dialogText = "This is the dialog text";

        getIdOfCurrentWidget(savedInstanceState);

        TextView txt1 = (TextView) findViewById(R.id.DialogtextView1);
        txt1.setText("Efficiency Rating");

        TextView txt2 = (TextView) findViewById(R.id.DialogtextView2);
        String efficiency = prefs.getEfficiency(this.getApplicationContext(),thisWidgetId);
        txt2.setText(efficiency);

        TextView txt3 = (TextView) findViewById(R.id.DialogtextView3);
        //txt3.setText("Number of Clients");
        txt3.setText("Current Round");

        TextView txt4 = (TextView) findViewById(R.id.DialogtextView4);
        String RoundTime = prefs.getRoundTime(this.getApplicationContext(), thisWidgetId);
        txt4.setText(RoundTime);

        TextView txt5 = (TextView) findViewById(R.id.DialogtextView5);
        //txt5.setText("Overall balance");
        txt5.setText("something...");

        TextView txt6 = (TextView) findViewById(R.id.DialogtextView6);
        txt6.setText(dialogText);

        TextView txt7 = (TextView) findViewById(R.id.DialogtextView7);
        txt7.setText("Pool Rate");

        TextView txt8 = (TextView) findViewById(R.id.DialogtextView8);
        String PoolRate = prefs.getPoolRate(this.getApplicationContext(), thisWidgetId);
        txt8.setText(PoolRate);

        TextView txt9 = (TextView) findViewById(R.id.DialogtextView9);
        txt9.setText("Node Uptime");

        TextView txt10 = (TextView) findViewById(R.id.DialogtextView10);
        String Uptime = prefs.getUptime(this.getApplicationContext(), thisWidgetId);
        txt10.setText(Uptime);

        TextView txt11 = (TextView) findViewById(R.id.DialogtextView11);
        txt11.setText("Shares");

        TextView txt12 = (TextView) findViewById(R.id.DialogtextView12);
        String shares = prefs.getShares(this.getApplicationContext(), thisWidgetId);
        txt12.setText(shares);

        TextView txt13 = (TextView) findViewById(R.id.DialogtextView13);
        txt13.setText("Current Block Value");

        TextView txt14 = (TextView) findViewById(R.id.DialogtextView14);
        String blockvalue = prefs.getBlockValue(this.getApplicationContext(), thisWidgetId);
        txt14.setText(blockvalue);

        TextView txt15 = (TextView) findViewById(R.id.DialogtextView15);
        txt15.setText("Expected time to block");

        TextView txt16 = (TextView) findViewById(R.id.DialogtextView16);
        String toblock = prefs.getTimeToBlock(this.getApplicationContext(), thisWidgetId);
        txt16.setText(toblock);

        TextView txt17 = (TextView) findViewById(R.id.DialogtextView17);
        txt17.setText("Expected time to share");

        txt18 = (TextView) findViewById(R.id.DialogtextView18);
        String toshare = prefs.getTimeToShare(this.getApplicationContext(), thisWidgetId);
        txt18.setText(toshare);


        Button dismissbutton = (Button) findViewById(R.id.w_dismiss_btn);
        dismissbutton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                WidgetDialogActivity.this.finish();
            }
        });

        Button refreshbutton = (Button) findViewById(R.id.w_refresh_btn);
        refreshbutton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                // TO DO: Figure out someway to refresh the currently selected widget
                //
                Intent intent = new Intent(getApplicationContext(),p2pWidget.class);
                intent.setAction("android.appwidget.action.APPWIDGET_UPDATE2");
// Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
// since it seems the onUpdate() is only fired on that:
                int[] ids = {thisWidgetId};
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);

                sendBroadcast(intent);
                WidgetDialogActivity.this.finish();
            }
        });

        Button configbutton = (Button) findViewById(R.id.w_config_btn);
        configbutton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                //int[] ids = {thisWidgetId};
                i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, thisWidgetId);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(i);
                WidgetDialogActivity.this.finish();
            }
        });

        //txt18.setText(String.format("%.0f",thisWidgetId));
    }
    /** Get the Id of Current Widget from the intent of the Widget **/
    void getIdOfCurrentWidget(Bundle savedInstanceState) {

        setResult(RESULT_CANCELED);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            thisWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    -3);

            thisWidgetId = getIntent().getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,-2);//extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,-1);

            int[] appWidgetIds = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);
            if (appWidgetIds != null && appWidgetIds.length > 0) {

                thisWidgetId = appWidgetIds[0];
            }

        }

        // If they gave us an intent without the widget id, just bail.
        //if (thisWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
        //    finish();
        //} else {
        //    txt18.setText(String.format("%d",thisWidgetId));
        //}
    }
}
