package com.kensenter.p2poolwidget;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;
import java.util.concurrent.TimeUnit;

public class p2pWidget extends AppWidgetProvider {
	String strJSONResponse;
    RemoteViews remoteViews;
    Context ctxt1;
    AppWidgetManager appWidMan;
    ComponentName p2pWidget;
    NotificationCompat.Builder mBuilder;
    Intent notificationIntent;
    PendingIntent ContInt;
    NotificationManager nm;
    static String PAYOUT_KEY;
    static Integer DOA_VALUE,ALERT_VALUE,HASH_LEVEL;
    static boolean ALERT_ON,DOA_ON,REMOVE_LINE;
    public static String REFRESH_ACTION = "android.appwidget.action.APPWIDGET_UPDATE";
    public static String REFRESH_ACTION2 = "android.appwidget.action.APPWIDGET_UPDATE2";
    private static final String SHOW_DIALOG_ACTION = "com.kensenter.p2poolwidget.widgetshowdialog";
    public static final String PREFS_NAME = "p2poolwidgetprefs";
    int thisWidgetId=AppWidgetManager.INVALID_APPWIDGET_ID;

    @Override
    public void onReceive(Context context, Intent intent) {
    	super.onReceive(context, intent);

        //Intent intent2 = new Intent(context, this.getClass());
        //intent2.setAction(REFRESH_ACTION);
        //intent2.setAction(SHOW_DIALOG_ACTION);

        Bundle extras = intent.getExtras();

        thisWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

        thisWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID);//extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,-1);

        if (extras != null && thisWidgetId<=0) {
            thisWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,-1);
            if (thisWidgetId<=0){
                int[] appWidgetIds = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);
                if (appWidgetIds != null && appWidgetIds.length > 0) {

                    thisWidgetId = appWidgetIds[0];
                }
            }
        }

        //PendingIntent pendingIntent = PendingIntent.getBroadcast(context, thisWidgetId, intent2, PendingIntent.FLAG_UPDATE_CURRENT);

        //remoteViews = new RemoteViews( context.getPackageName(), R.layout.widget_main );

        //remoteViews.setOnClickPendingIntent(R.id.LL1, pendingIntent);

        if (intent.getAction().equals(SHOW_DIALOG_ACTION)) {


            //int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
            Intent i = new Intent(context, WidgetDialogActivity.class);
            int[] ids = {thisWidgetId};
            i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }

    	if (intent.getAction().equals(REFRESH_ACTION2)) {
    	   
         	CharSequence text = "Refreshing...";
         	int duration = Toast.LENGTH_SHORT;

         	Toast toast = Toast.makeText(context, text, duration);
         	toast.show();

     		RefreshWidget(context,AppWidgetManager.getInstance(context),thisWidgetId);
    	   
    	}

        //if (intent.getAction().equals(REFRESH_ACTION)) {
         //   RefreshWidget(context,AppWidgetManager.getInstance(context),thisWidgetId);
        //}

    }
    
    public int RefreshWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetIds)
    {
        mBuilder = new NotificationCompat.Builder(context)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentTitle("Hashrate dropped below threshold")
        .setLights(0xff0000ff, 100, 2000)
        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        .setContentText("P2Pool Hashrate Below Threshold")
        .setTicker("P2Pool Hashrate Alert!");

        nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    	
    	remoteViews = new RemoteViews( context.getPackageName(), R.layout.widget_main );
        ctxt1 = context;
        p2pWidget = new ComponentName( context, p2pWidget.class );
        appWidMan = appWidgetManager;

        thisWidgetId = appWidgetIds;

    	GetPrefs prefs = new GetPrefs();
        String SERVER_NAME = prefs.GetServer(context,thisWidgetId);
        Integer PORT_VALUE = prefs.getPort(context,thisWidgetId);
        ALERT_VALUE = prefs.getAlertRate(context,thisWidgetId);
        DOA_VALUE = prefs.getDOARate(context,thisWidgetId);
        ALERT_ON = prefs.getAlertOn(context,thisWidgetId);
        DOA_ON = prefs.getDOAOn(context,thisWidgetId);
        REMOVE_LINE = prefs.getRemoveLine(context,thisWidgetId);
        HASH_LEVEL = prefs.getHashLevel(context,thisWidgetId);
        PAYOUT_KEY = prefs.getPayKey(context,thisWidgetId);
        int successful =0;

        if (!(SERVER_NAME=="")) {

            String url = "http://" + SERVER_NAME + ":" + PORT_VALUE.toString() + "/local_stats";
            String url2 = "http://" + SERVER_NAME + ":" + PORT_VALUE.toString() + "/current_payouts";
            String url3 = "http://" + SERVER_NAME + ":" + PORT_VALUE.toString() + "/payout_addr";
            String url4 = "http://" + SERVER_NAME + ":" + PORT_VALUE.toString() + "/global_stats";
            String url5 = "http://" + SERVER_NAME + ":" + PORT_VALUE.toString() + "/recent_blocks";
            DownloadFilesTask task = new DownloadFilesTask();

            int counterx = 0;
            while (counterx<10 && !isNetworkAvailable(context)) {
                try {
                    wait(1000);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
                counterx=counterx+1;
            }
            if (isNetworkAvailable(context)) {
                task.execute(url,url2,url3,Integer.toString(thisWidgetId),url4,url5,null,null);
                successful=1;
            }
        } else {
            successful=0;
        }
        return successful;
    }

    @Override
    public void onUpdate( Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds )
    {
        ComponentName thisWidget = new ComponentName(context,
                p2pWidget.class);
        int[] allwidgetids = appWidgetManager.getAppWidgetIds(thisWidget);




        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int appWidgetId : allwidgetids) {


            Intent intent = new Intent(context, this.getClass());
            //intent.setAction(REFRESH_ACTION);
            intent.setAction(SHOW_DIALOG_ACTION);
            int[] ids = {appWidgetId};
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);

            //intent.setData(Uri.withAppendedPath(Uri.parse("myapp://widget/id/#togetitunique" + appWidgetId), String.valueOf(appWidgetId)));

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, appWidgetId, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            remoteViews = new RemoteViews( context.getPackageName(), R.layout.widget_main );
            ctxt1 = context;
            remoteViews.setOnClickPendingIntent(R.id.LL1, pendingIntent);
            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);

            RefreshWidget(context,appWidgetManager,appWidgetId);

        }

        //for (int appWidgetId : allwidgetids) {
        //    RefreshWidget(context,appWidgetManager,appWidgetId);
        //}
        //notificationIntent = new Intent(context,p2pWidget.getClass());
        //PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //mBuilder.setContentIntent(ContInt);
        
        
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager 
              = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private class DownloadFilesTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            HttpClient client = new DefaultHttpClient();
            String json = "";
            int thiswidgetid = 0;
            try {
                thiswidgetid = Integer.parseInt(urls[3]);
                String line = "";
                HttpGet request = new HttpGet(urls[0]);
                HttpResponse response = client.execute(request);
                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                while ((line = rd.readLine()) != null) {
                    json += line;
                }
                request = null;
                response = null;
                rd.close();
                rd = null;

                json = json.substring(0,json.length()-1) + ", \"current_payouts\":  ";
                request = new HttpGet(urls[1]);
                response = client.execute(request);
                rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                while ((line = rd.readLine()) != null) {
                    json += line;
                }
                request = null;
                response = null;
                rd.close();
                rd = null;
                json=json + "}";

                json = json.substring(0,json.length()-1) + ", \"global_stats\":  ";
                request = new HttpGet(urls[4]);
                response = client.execute(request);
                rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                while ((line = rd.readLine()) != null) {
                    json += line;
                }
                request = null;
                response = null;
                rd.close();
                rd = null;




                json = json + ", \"payout_addr\": ";
                request = new HttpGet(urls[2]);
                response = client.execute(request);
                rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                while ((line = rd.readLine()) != null) {
                    json += line;
                }
                request = null;
                response = null;
                rd.close();
                rd = null;
                json=json + "}";

                json = json.substring(0,json.length()-1) + ", \"recent_blocks\":  ";
                request = new HttpGet(urls[5]);
                response = client.execute(request);
                rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                while ((line = rd.readLine()) != null) {
                    json += line;
                }
                request = null;
                response = null;
                rd.close();
                rd = null;

                json = json + ", \"widgetid\": ";

                json += urls[3];



                json=json + "}";
                
} catch (IllegalArgumentException e1) {
                e1.printStackTrace();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            return json;
        }

        protected void onProgressUpdate(Void... progress) {

        }

        
        protected void onPostExecute(String result) {
        	strJSONResponse = result;
        	 double totalhash = 0;
        	 double doa = 0;
        	 double deadhash = 0;
        	 double pending_payout = 0;
        	 boolean downloadfailed = false;
            String widgetid = "";
        	 JSONObject jsonObject;
     		try {
     			jsonObject = new JSONObject(strJSONResponse);

                widgetid = jsonObject.getString("widgetid");
                thisWidgetId = Integer.parseInt(widgetid);
                GetPrefs prefs = new GetPrefs();
                String SERVER_NAME = prefs.GetServer(ctxt1,thisWidgetId);
                Integer PORT_VALUE = prefs.getPort(ctxt1,thisWidgetId);
                ALERT_VALUE = prefs.getAlertRate(ctxt1,thisWidgetId);
                DOA_VALUE = prefs.getDOARate(ctxt1,thisWidgetId);
                ALERT_ON = prefs.getAlertOn(ctxt1,thisWidgetId);
                DOA_ON = prefs.getDOAOn(ctxt1,thisWidgetId);
                REMOVE_LINE = prefs.getRemoveLine(ctxt1,thisWidgetId);
                HASH_LEVEL = prefs.getHashLevel(ctxt1,thisWidgetId);
                PAYOUT_KEY = prefs.getPayKey(ctxt1,thisWidgetId);

                String payout_addr;
                if (PAYOUT_KEY.length()>0) {
                    payout_addr = PAYOUT_KEY;
                } else {
                    payout_addr = jsonObject.getString("payout_addr");
                }

     			JSONObject object = jsonObject.getJSONObject("miner_hash_rates");
     	        Iterator<?> keys = object.keys();
     	        while (keys.hasNext()){

     	        	String key = (String)keys.next();
                    if (PAYOUT_KEY.length()>0) {
                        if (key.compareTo(payout_addr)==0){
     	        	        totalhash = totalhash + object.getDouble(key);
                        }
                    } else {
                        totalhash = totalhash + object.getDouble(key);
                    }
     	        }
     	        
     	        object = jsonObject.getJSONObject("miner_dead_hash_rates");
     	        keys = object.keys();
     	        while (keys.hasNext()){
     	        	String key = (String)keys.next();
                    if (PAYOUT_KEY.length()>0) {
                        if (key.compareTo(payout_addr)==0){
     	        	        deadhash = deadhash + object.getDouble(key);
                        }
                    } else {
                        deadhash = deadhash + object.getDouble(key);
                    }
     	        }
     	        doa = ((deadhash/totalhash)*100);
     	        




     	        object = jsonObject.getJSONObject("current_payouts");
     	        keys = object.keys();
     	        while (keys.hasNext()){
     	        	String key = (String)keys.next();
     	        	if (key.compareTo(payout_addr)==0){
     	        		pending_payout = pending_payout + object.getDouble(key);
     	        	}
     	        }
     	        doa = ((deadhash/totalhash)*100);



                SharedPreferences settings = ctxt1.getSharedPreferences(PREFS_NAME + widgetid, 0);
                SharedPreferences.Editor editor = settings.edit();

                long attemptstoshare=jsonObject.getLong("attempts_to_share");
                if (totalhash == 0) {
                    editor.putString("toshare", "Infinity");
                } else {
                    double time_to_share = (double) ((double) attemptstoshare / (double) totalhash) / 3600;
                    editor.putString("toshare", String.format("%5f",time_to_share) + " hours");
                }



                String efficiency=jsonObject.getString("efficiency");
                if (efficiency.equals("null"))
                    editor.putString("efficiency", "n/a");
                else
                {
                    double effic = Double.parseDouble(efficiency);
                    int effi = (int) (effic * 100);

                    editor.putString("efficiency", Integer.toString(effi) + "%");
                }

                String blockvalue=jsonObject.getString("block_value");
                editor.putString("blockvalue", blockvalue);


                JSONArray arr1 = jsonObject.getJSONArray("recent_blocks");
                if (arr1.length()>0) {
                object = arr1.getJSONObject(0);
                String ts=object.getString("ts");
                Date expiry = new Date(Long.parseLong(ts) * 1000);
                long diffdate = new Date().getTime() - expiry.getTime();
                long diffSeconds = diffdate / 1000 % 60;
                long diffMinutes = diffdate / (60 * 1000) % 60;
                long diffHours = diffdate / (60 * 1000 * 60) % 60;

                editor.putString("roundtime", Long.toString(diffHours) + "h " + Long.toString(diffMinutes) + "m ");
                } else {
                    editor.putString("roundtime", " unknown ");
                }

                object = jsonObject.getJSONObject("shares");
                String shares=object.getString("total");
                shares = shares + " total (" + object.getString("orphan");
                shares = shares + " orphan, ";
                shares = shares + object.getString("dead");
                shares = shares + " dead)";
                editor.putString("shares", shares);


                long uptime=(long) jsonObject.getDouble("uptime");

                int day = (int) (uptime / 86400);
                int hours = (int) ( (uptime - (day *86400) ) /3600 );
                int minute = (int) ( ((uptime - (day *86400) - (hours *3600) )) /60 );
                int second = (int) ( ((uptime - (day *86400) - (hours *3600)  - (minute * 60) )) );
                String uptimestring = "";
                if (day>0) uptimestring = uptimestring + Integer.toString(day) + " Day ";
                if (day>0 || hours>0) uptimestring = uptimestring + Long.toString(hours) + " Hr ";
                if (day>0 || hours>0 || minute>0) uptimestring = uptimestring + Long.toString(minute) + " Min ";
                if (day>0 || hours>0 || minute>0 || second>0) uptimestring = uptimestring + Long.toString(second) + " Sec ";

                editor.putString("uptime", uptimestring);

                object = jsonObject.getJSONObject("global_stats");
                double PoolRate = object.getDouble("pool_hash_rate");

                double adjustedpoolhash=PoolRate;
                String speedPoolLetter="";
                if (adjustedpoolhash>1000){
                    adjustedpoolhash=adjustedpoolhash/1000;
                    speedPoolLetter="K";
                }
                if (adjustedpoolhash>1000){
                    adjustedpoolhash=adjustedpoolhash/1000;
                    speedPoolLetter="M";
                }
                if (adjustedpoolhash>1000){
                    adjustedpoolhash=adjustedpoolhash/1000;
                    speedPoolLetter="G";
                }
                if (adjustedpoolhash>1000){
                    adjustedpoolhash=adjustedpoolhash/1000;
                    speedPoolLetter="T";
                }


                double attemptstoblock=jsonObject.getDouble("attempts_to_block");
                double time_to_block = (double) ((double) attemptstoblock / (double) PoolRate) / 3600;
                editor.putString("toblock", String.format("%5f",time_to_block) + " hours");

                editor.putString("pool_rate", String.format("%.1f",adjustedpoolhash) + speedPoolLetter+"h/s");


                editor.commit();

     		} catch (JSONException e) {
     			
     			e.printStackTrace();
     			downloadfailed=true;
     		}
     		double adjustedhash=totalhash;
     		String speedLetter="";
     		if (adjustedhash>1000){
     			adjustedhash=adjustedhash/1000;
     			speedLetter="K";
     		}
     		if (adjustedhash>1000){
     			adjustedhash=adjustedhash/1000;
     			speedLetter="M";
     		}
     		if (adjustedhash>1000){
     			adjustedhash=adjustedhash/1000;
     			speedLetter="G";
     		}
            if (adjustedhash>1000){
                adjustedhash=adjustedhash/1000;
                speedLetter="T";
            }
     		
     		if (downloadfailed==false){
     			remoteViews.setTextViewText( R.id.WidgettextView1,"Hash Rate:");
     			remoteViews.setTextViewText( R.id.WidgettextView2,  String.format("%.1f",adjustedhash) + speedLetter+"h/s");
     			remoteViews.setTextViewText( R.id.WidgettextView3,"DOA:");
     			remoteViews.setTextViewText( R.id.WidgettextView4,  String.format("%.0f",doa) + "%");
     			remoteViews.setTextViewText( R.id.WidgettextView5,  "Pending Payout");
     			remoteViews.setTextViewText( R.id.WidgettextView6,  String.format("%.8f",pending_payout));
            	java.text.DateFormat format = SimpleDateFormat.getTimeInstance( SimpleDateFormat.MEDIUM, Locale.getDefault() );
            	if (REMOVE_LINE) {
            		remoteViews.setTextViewText( R.id.WidgettextView7, format.format( new Date()));
            		remoteViews.setTextViewText( R.id.WidgettextView8, "");
            	} else {
            		remoteViews.setTextViewText( R.id.WidgettextView8, format.format( new Date()) );
            		remoteViews.setTextViewText( R.id.WidgettextView7, "");            		
            	}
            	//remoteViews.setViewPadding(R.id.LL1, 0, 0, 0, 0);
            	//appWidMan.updateAppWidget( p2pWidget, remoteViews );
                appWidMan.updateAppWidget(Integer.parseInt(widgetid), remoteViews);
             	double HashMult=1;
            	if (HASH_LEVEL==0) HashMult = 1;
            	if (HASH_LEVEL==1) HashMult = 1000;
            	if (HASH_LEVEL==2) HashMult = 1000000;
            	if (HASH_LEVEL==3) HashMult = 1000000000;
            	double alertval = ALERT_VALUE;
            	if ((ALERT_ON && totalhash <= alertval * HashMult )|| (DOA_ON && doa > DOA_VALUE))
            	{
            		if (DOA_ON && doa > DOA_VALUE)
            		{
            			mBuilder.setContentText("P2Pool DOA Above Threshold");
            			mBuilder.setTicker("P2Pool DOA Alert!");
            			mBuilder.setContentTitle("DOA has exceeded the threshold");
            		} else {
                        mBuilder.setContentText("P2Pool Hashrate Below Threshold");
                        mBuilder.setTicker("P2Pool Hashrate Alert!");
                        mBuilder.setContentTitle("Hashrate dropped below threshold");
            		}
            	
            		nm.notify(1,mBuilder.build());
            	}
     		}
        }

    }


}
