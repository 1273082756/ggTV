package com.example.luke.testtv;

import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.android.exoplayer2.util.Util.*;


public class TvAction extends AppCompatActivity {
    private PlayerView playerView;
    private SimpleExoPlayer player;
    private Map<String, String> tvSource;
    private OrientationEventListener mOrientationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String state = null;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_tvaction);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }//返回按钮
        TextView tx = (TextView) findViewById(R.id.tvaction_tx_head);
        tvSourceInit();
        state = checknet(this);
        Toast.makeText(getApplicationContext(), "当前网络模式： " + state,
                Toast.LENGTH_SHORT).show();

        Intent intent = getIntent();
        String getMessage = intent.getStringExtra("name");
        setTitle("当前频道： " + getMessage);
        tx.setText(getMessage + "      [模式：" + state + "]");
        if (getMessage != null) {
            initView();
            initExo(tvSource.get(getMessage));
        }

        mOrientationListener = new OrientationEventListener(this,
                SensorManager.SENSOR_DELAY_NORMAL) {

            @Override
            public void onOrientationChanged(int orientation) {//90逆时针旋转 270顺时针旋转
                TextView textView = findViewById(R.id.tvaction_tx_head);
                ExoPlayer exoPlayer = findViewById(R.id.tvaction_player);
                ImageView imageView = findViewById(R.id.img);
                TextView textView1 = findViewById(R.id.msg1);
                if (orientation == 90 || orientation == 270) {
//                    Toast.makeText(getApplicationContext(), orientation==90?"状态： 向右":"状态： 向左",
//                            Toast.LENGTH_SHORT).show();
                    getSupportActionBar().hide();
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    textView.setVisibility(View.GONE);//显示文本标题
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) playerView.getLayoutParams();
                    params.height = LinearLayout.LayoutParams.MATCH_PARENT;
                    playerView.setLayoutParams(params);//让playerview高铺满屏幕
                    imageView.setLayoutParams(params);
                    textView1.setLayoutParams(params);
                    if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
                        View v = getWindow().getDecorView();
                        v.setSystemUiVisibility(View.GONE);
                    } else if (Build.VERSION.SDK_INT >= 19) {
                        View decorView = getWindow().getDecorView();
                        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                | View.SYSTEM_UI_FLAG_FULLSCREEN;
                        decorView.setSystemUiVisibility(uiOptions);
                    }
                }

            }
        };

        if (mOrientationListener.canDetectOrientation()) {//正常状态
            TextView textView = findViewById(R.id.tvaction_tx_head);
            ExoPlayer exoPlayer = findViewById(R.id.tvaction_player);
//            Toast.makeText(getApplicationContext(), "2",
//                    Toast.LENGTH_SHORT).show();
            getSupportActionBar().show();
            textView.setVisibility(View.VISIBLE);//隐藏文本标题
            if (!(Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19)) {
                //低版本sdk
                View v = getWindow().getDecorView();
                v.setSystemUiVisibility(View.VISIBLE);
            } else if (Build.VERSION.SDK_INT >= 19) {
                View decorView = getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);
            }

            mOrientationListener.enable();
        } else {
            Toast.makeText(getApplicationContext(), "3",
                    Toast.LENGTH_SHORT).show();
            mOrientationListener.disable();
        }
    }

    public String checknet(Context context) {
        String strNetworkType = "";
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // 获取代表联网状态的NetWorkInfo对象 NetworkInfo networkInfo = connManager.getActiveNetworkInfo(); /
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                strNetworkType = "WIFI";
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                String _strSubTypeName = networkInfo.getSubtypeName();

                // TD-SCDMA   networkType is 17
                int networkType = networkInfo.getSubtype();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                        strNetworkType = "2G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                    case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                    case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                        strNetworkType = "3G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                        strNetworkType = "4G";
                        break;
                    default:
                        // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                        if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                            strNetworkType = "3G";
                        } else {
                            strNetworkType = _strSubTypeName;
                        }
                        break;
                }
            }
        }
        return strNetworkType;
    }

    @Override


    public boolean onOptionsItemSelected(MenuItem item) {//返回按钮
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }//返回按钮

    public void tvSourceInit() {
        tvSource = new HashMap<String, String>();
        try {
            test(tvSource);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void test(Map<String, String> tvSource) throws IOException {//read json file
        myJson json =new myJson("data.json");//传文件进去
        JSONObject lan;
        JSONArray array = json.work("root");//指定json根路径
        for (int i = 0; i < array.length(); i++) {
            try {
                lan = array.getJSONObject(i);
                tvSource.put(lan.getString("name"),lan.getString("url"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void initExo(String WebPosition) {
        /**
         * 创建播放器
         */
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();// 得到默认合适的带宽
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);// 创建跟踪的工厂
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);// 创建跟踪器

        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        playerView.setPlayer(player);// 绑定player
        /**
         * 准备player
         */
        // 生成通过其加载媒体数据的DataSource实例
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(TvAction.this, "ExoPlayer"), bandwidthMeter);
        MediaSource mediaSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(WebPosition));// 创建要播放的媒体的MediaSource
        player.prepare(mediaSource);// 准备播放器的MediaSource
        player.setPlayWhenReady(true);// 当准备完毕后直接播放
    }

    private void initView() {
        playerView = (PlayerView) findViewById(R.id.tvaction_player);
    }

    @Override
    protected void onDestroy() {
        mOrientationListener.disable();
        player.release();
        super.onDestroy();
    }

}
