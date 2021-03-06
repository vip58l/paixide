package com.paixide.getHandler;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.paixide.Util.checkagent;
import com.paixide.Util.config;
import com.paixide.listener.Paymnets;
import com.paixide.getlist.okhttp;
import com.tencent.qcloud.tim.tuikit.live.BuildConfig;

import java.io.File;
import java.util.Map;

import okhttp3.RequestBody;

public class PostModule extends Handler {
    private Paymnets paymnets;

    private final String TAG = PostModule.class.getSimpleName();

    public PostModule(String path, Paymnets paymnets) {
        this.paymnets = paymnets;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String response = okhttp.get(path);
                    Message message = new Message();
                    message.obj = response;
                    message.what = config.sussess;
                    sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public PostModule(String path, RequestBody requestBody) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    String date = okhttp.POST(path, requestBody);
                    Message message = new Message();
                    message.what = config.sussess;
                    message.obj = date;
                    sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    sendEmptyMessage(config.fail);
                }
            }
        }.start();
    }

    public PostModule(Map<String, String> hashMap, File file) {

        new Thread() {
            @Override
            public void run() {
                super.run();
                okhttp.okhttp_image(hashMap, file, PostModule.this);
            }
        }.start();
    }

    public PostModule(Map<String, String> hashMap, RequestBody requestBody) {

        new Thread() {
            @Override
            public void run() {
                super.run();
                okhttp.okhttpimage(hashMap, requestBody, PostModule.this);
            }
        }.start();
    }

    /**
     * get????????????
     *
     * @param path
     * @param paymnets
     */
    public static void getModule(String path, Paymnets paymnets) {
        if (!BuildConfig.isWifiProxy) {
            //????????????????????????????????????????????????
            if (!config.isNetworkAvailable() || checkagent.isWifiProxy1()) {
                paymnets.isNetworkAvailable();
                return;
            }
        }
        PostModule module = new PostModule(path, paymnets);
        module.paymnets = paymnets;
    }

    /**
     * ??????POST??????
     *
     * @param path
     * @param requestBody
     * @param paymnets
     */
    public static void postModule(String path, RequestBody requestBody, Paymnets paymnets) {
        if (!BuildConfig.isWifiProxy) {
            //????????????????????????????????????????????????
            if (!config.isNetworkAvailable() || checkagent.isWifiProxy1()) {
                paymnets.isNetworkAvailable();
                return;
            }
        }


        PostModule module = new PostModule(path, requestBody);
        module.paymnets = paymnets;
    }

    /**
     * ??????????????????
     *
     * @param file
     * @param hashMap
     * @param paymnet
     */
    public static void okhttpimage(Map<String, String> hashMap, File file, Paymnets paymnet) {
        /**
         * ?????????????????????
         */
        if (!BuildConfig.isWifiProxy) {
            if (checkagent.isWifiProxy1()) {
                return;
            }
        }
        PostModule module = new PostModule(hashMap, file);
        module.paymnets = paymnet;

    }

    /**
     * ??????????????????
     *
     * @param hashMap
     * @param requestBody
     * @param paymnet
     */
    public static void okhttpimage(Map<String, String> hashMap, RequestBody requestBody, Paymnets paymnet) {
        if (!config.isNetworkAvailable()) {
            paymnet.isNetworkAvailable();
            return;
        }
        if (!BuildConfig.isWifiProxy) {
            //????????????????????????????????????????????????
            if (checkagent.isWifiProxy1()) {
                return;
            }
        }
        PostModule module = new PostModule(hashMap, requestBody);
        module.paymnets = paymnet;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case config.sussess:
                String s = msg.obj.toString();
                if (!TextUtils.isEmpty(s)) {
                    paymnets.success(s);
                }
                break;
            case config.fail:
                paymnets.fall(400);
                break;
        }

    }

}
