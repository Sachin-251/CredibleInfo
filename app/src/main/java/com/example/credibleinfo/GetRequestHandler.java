package com.example.credibleinfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GetRequestHandler {

    public String sendGetRequest(String requestURL){

        URL url;
        String result="";

        try {
            url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            InputStream input=conn.getInputStream();
            InputStreamReader reader=new InputStreamReader(input);
            int data=reader.read();
            while(data!=-1)
            {
                char current=(char)data;
                result+=current;
                data=reader.read();
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return result;
    }
}
