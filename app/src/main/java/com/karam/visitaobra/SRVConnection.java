package com.karam.visitaobra;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;


import javax.net.ssl.HttpsURLConnection;



public class SRVConnection{

    private final TaskListener taskListener;
    private String functionality;
    private URL url;
    private HttpURLConnection conn;
    final String TAG = "SRVCONNECTION";
    //functionality (request , response , reqres)
    public SRVConnection(Activity activity, @NonNull String functionality, final String... strings){
        taskListener = (TaskListener) activity;
        this.functionality = functionality;
        CompletableFuture.supplyAsync(() -> doInBackground(strings)).thenAccept(s -> activity.runOnUiThread(() -> onPostExecute(s)));
    }


    protected String doInBackground(String... strings) {
        setConn(strings);
        switch (functionality)
        {
            case "request":
                return request(strings);
            case "response":
                return response(strings);
            default:
                return null;
        }
    }




    protected void onPostExecute(String s) {
        if(taskListener!=null){
            taskListener.onTaskFinish(s);
        }
    }


    private String setConn(String... strings){
        try
        {
            url = new URL(strings[0]);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(10000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            return "ok";
        }catch (Exception e)
        {
            return e.getMessage();
        }
    }

    private String request(String... strings){
        try
        {
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(strings[1]);
            writer.flush();
            writer.close();
            os.close();
            return conn.getResponseMessage();
        }
        catch (Exception e)
        {
            return e.getMessage();
        }
    }

    private String response(String...strings)
    {
        StringBuffer sb = new StringBuffer("");
        try
        {
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(strings[1]);
            writer.flush();
            writer.close();
            os.close();
            int responseCode = conn.getResponseCode(); // To Check for 200
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = "";
                while ((line = in.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                in.close();
                return String.valueOf(sb).trim();
            }
            else
            {
                return conn.getResponseMessage().trim();
            }
        }catch(Exception e)
        {
            return e.getMessage();
        }
    }
}