package com.example.luke.testtv;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
public class myJson {
    private String line;
    private JSONObject lan;
    private StringBuilder builder;
    public myJson(String url) {//构造方法 传文件名,位于asset下
        try {
            InputStream is = myJson.this.getClass().getClassLoader().getResourceAsStream("assets/" + url);//android studio
            BufferedReader bufr = new BufferedReader(new InputStreamReader(is));
            builder = new StringBuilder();
            while ((line = bufr.readLine()) != null) {
                builder.append(line);
            }
            is.close();
            bufr.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JSONArray work(String value) {
        JSONArray array = null;
        try {
            JSONObject root = new JSONObject(builder.toString());
            array = root.getJSONArray(value);
//            for (int i = 0; i < array.length(); i++) {
//                lan = array.getJSONObject(i);
//            }
            return array;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return array;
    }
}
