package com.example.credibleinfo;

import android.os.AsyncTask;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;

public class EduFetch extends AsyncTask<String, Void, String> {

    String data="";
    String parseData="";
    String singleParse="";

    @Override
    protected String doInBackground(String... urls) {

        try {
            URL url=new URL("http://139.59.65.145:9090/test");
            HttpURLConnection con= (HttpURLConnection) url.openConnection();
            InputStream input=con.getInputStream();
            BufferedReader read=new BufferedReader(new InputStreamReader(input));
            String line="";
            while(line != null)
            {
                line=read.readLine();
                data=data+line;
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } /*catch (JSONException e) {
            e.printStackTrace();
        }*/
        return data;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try {
            JSONObject obj=new JSONObject(result);
            //String res=obj.getString("server_name");
            //res=res+obj.getString("method");
            PublicView.test.setText("Server :"+obj.getString("server_name"));
            PublicView.test.append("\nMethod  :"+obj.getString("method"));
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

    }
}
