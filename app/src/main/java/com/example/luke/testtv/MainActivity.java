package com.example.luke.testtv;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;


public class MainActivity extends AppCompatActivity {
    private RecyclerView movieList;
    private MovieListAdapter movieAdapter;

    public static final String EXTRA_MESSAGE = "com.example.luke.testtv";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("TV-节目列表");
        movieList = findViewById(R.id.main_recycler_movieList);
        movieAdapter = new MovieListAdapter();
        movieList.setLayoutManager(new LinearLayoutManager(this));//设置列表样式
        movieList.setAdapter(movieAdapter);
        movieList.addItemDecoration(
                new DividerItemDecoration(
                        this, DividerItemDecoration.VERTICAL
                )
        );
        movieAdapter.setOnItemClickListener(new MovieListAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {//窗口跳转，跳转到播放页面
                Intent intent = new Intent(MainActivity.this, TvAction.class);
                intent.putExtra("name", MovieLab.get().getTv(position));
                startActivity(intent);
            }
        });
//        Log.d("AAA","------------------------------------------------------------");
//
    }

//    public void test() throws IOException {//read json file
//        InputStream is = MainActivity.this.getClass().getClassLoader().getResourceAsStream("assets/" + "data.json");//android studio
//        BufferedReader bufr = new BufferedReader(new InputStreamReader(is));
//        String line;
//        StringBuilder builder = new StringBuilder();
//        while ((line = bufr.readLine()) != null) {
//            builder.append(line);
//        }
//        is.close();
//        bufr.close();
////        String result = line;
//        try {
//            JSONObject root = new JSONObject(builder.toString());
//            JSONArray array = root.getJSONArray("root");
//            for (int i = 0; i < array.length(); i++) {
//                JSONObject lan = array.getJSONObject(i);
//                Log.d("info","-----------------------");
//                Log.d("info",lan.getString("name"));
//                Log.d("info",lan.getString("url"));
//                Log.d("info",lan.getString("img"));
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
}
