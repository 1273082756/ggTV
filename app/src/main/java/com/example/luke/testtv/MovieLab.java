package com.example.luke.testtv;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 存储节目信息，采用单例模式（构造方法改为私有）
 */
public class MovieLab {
    private static MovieLab instance;
    private List<String> tvs;

    private MovieLab(){
        init ();
    }
    /**
     * 只返回一个实例，不会创建新的实例
     * @return 单例，表示节目
     */
    public static MovieLab get(){
        if(null==instance){
            instance = new MovieLab ();
        }
        return instance;
    }

    /**
     * 返回仓库中有几个节目
     * @return 节目数量
     */
    public int getSize(){
        return tvs.size ();
    }

    /**
     * 返回节目信息
     * @param n 节目编号，或称为第几个节目
     * @return 节目信息
     */
    public String getTv(int n){
        return  tvs.get ( n );
    }
    public void init(){
        tvs = new ArrayList<String> (  );
        try {
            test(tvs);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        tvs.add ( "CCTV1" );
//        tvs.add ( "CCTV2" );
//        tvs.add ( "CCTV3" );
//        tvs.add ( "CCTV4" );
//        tvs.add ( "CCTV5" );
//        tvs.add ( "CCTV6" );
    }
    public void test(List<String> tvs) throws IOException {//read json file
        InputStream is = MovieLab.this.getClass().getClassLoader().getResourceAsStream("assets/" + "data.json");//android studio
        BufferedReader bufr = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuilder builder = new StringBuilder();
        while ((line = bufr.readLine()) != null) {
            builder.append(line);
        }
        is.close();
        bufr.close();
//        String result = line;
        try {
            JSONObject root = new JSONObject(builder.toString());
            JSONArray array = root.getJSONArray("root");
            for (int i = 0; i < array.length(); i++) {
                JSONObject lan = array.getJSONObject(i);
                Log.d("info","-----------------------");
                Log.d("info",lan.getString("name"));
                Log.d("info",lan.getString("url"));
                Log.d("info",lan.getString("img"));
                tvs.add(lan.getString("name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
