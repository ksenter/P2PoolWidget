package com.kensenter.p2poolwidget;

import android.content.Context;
import android.content.SharedPreferences;

public class GetPrefs {
    public static final String PREFS_NAME = "p2poolwidgetprefs";

    public String GetWidget(Context ctxt, int WidgetId){
        SharedPreferences settings = ctxt.getSharedPreferences(PREFS_NAME+WidgetId, 0);
        return settings.getString("servername", null);
    }

    public String GetServer(Context ctxt, int WidgetId){
    	SharedPreferences settings = ctxt.getSharedPreferences(PREFS_NAME+WidgetId, 0);
    	return settings.getString("servername", "");
    }

    public String getPayKey(Context ctxt, int WidgetId){
    	SharedPreferences settings = ctxt.getSharedPreferences(PREFS_NAME+WidgetId, 0);
    	return settings.getString("paykey", "");
    }
    
    public Integer getPort(Context ctxt, int WidgetId){
    	SharedPreferences settings = ctxt.getSharedPreferences(PREFS_NAME+WidgetId, 0);
    	return settings.getInt("portnum", 3332);
    }

    public Integer getHashLevel(Context ctxt, int WidgetId){
    	SharedPreferences settings = ctxt.getSharedPreferences(PREFS_NAME+WidgetId, 0);
    	return settings.getInt("hashlevel", 2);
    }

    public Integer getAlertRate(Context ctxt, int WidgetId){
    	SharedPreferences settings = ctxt.getSharedPreferences(PREFS_NAME+WidgetId, 0);
    	return settings.getInt("alertnum", 0);
    }

    public Integer getDOARate(Context ctxt, int WidgetId){
    	SharedPreferences settings = ctxt.getSharedPreferences(PREFS_NAME+WidgetId, 0);
    	return settings.getInt("doanum", 50);
    }

    public String getEfficiency(Context ctxt, int WidgetId){
        SharedPreferences settings = ctxt.getSharedPreferences(PREFS_NAME+WidgetId, 0);
        return settings.getString("efficiency", "");
    }

    public String getUptime(Context ctxt, int WidgetId){
        SharedPreferences settings = ctxt.getSharedPreferences(PREFS_NAME+WidgetId, 0);
        return settings.getString("uptime", "");
    }

    public String getShares(Context ctxt, int WidgetId){
        SharedPreferences settings = ctxt.getSharedPreferences(PREFS_NAME+WidgetId, 0);
        return settings.getString("shares", "");
    }

    public String getTimeToShare(Context ctxt, int WidgetId){
        SharedPreferences settings = ctxt.getSharedPreferences(PREFS_NAME+WidgetId, 0);
        return settings.getString("toshare", "");
    }

    public String getRoundTime(Context ctxt, int WidgetId){
        SharedPreferences settings = ctxt.getSharedPreferences(PREFS_NAME+WidgetId, 0);
        return settings.getString("roundtime", "");
    }
    public String getTimeToBlock(Context ctxt, int WidgetId){
        SharedPreferences settings = ctxt.getSharedPreferences(PREFS_NAME+WidgetId, 0);
        return settings.getString("toblock", "");
    }

    public String getBlockValue(Context ctxt, int WidgetId){
        SharedPreferences settings = ctxt.getSharedPreferences(PREFS_NAME+WidgetId, 0);
        return settings.getString("blockvalue", "");
    }

    public String getPoolRate(Context ctxt, int WidgetId){
        SharedPreferences settings = ctxt.getSharedPreferences(PREFS_NAME+WidgetId, 0);
        return settings.getString("pool_rate", "");
    }

    public boolean getRemoveLine(Context ctxt, int WidgetId){
    	SharedPreferences settings = ctxt.getSharedPreferences(PREFS_NAME+WidgetId, 0);
    	return settings.getBoolean("removeline", false);
    }

    public boolean getAlertOn(Context ctxt, int WidgetId){
    	SharedPreferences settings = ctxt.getSharedPreferences(PREFS_NAME+WidgetId, 0);
    	return settings.getBoolean("alerton", true);
    }

    public boolean getDOAOn(Context ctxt, int WidgetId){
    	SharedPreferences settings = ctxt.getSharedPreferences(PREFS_NAME+WidgetId, 0);
    	return settings.getBoolean("doaon", true);
    }
}
