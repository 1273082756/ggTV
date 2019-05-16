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
    }
    public void test(List<String> tvs) throws IOException {//read json file
        myJson json =new myJson("data.json");//传文件进去
        JSONObject lan;
        JSONArray array = json.work("root");//指定json根路径
        for (int i = 0; i < array.length(); i++) {
            try {
                lan = array.getJSONObject(i);
                tvs.add(lan.getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
