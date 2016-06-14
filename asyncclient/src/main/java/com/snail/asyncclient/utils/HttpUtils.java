package com.snail.asyncclient.utils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import javax.crypto.Cipher;

/**
 * Created by ping on 2016/5/20.
 */
public class HttpUtils {

    private static AsyncHttpClient client;

    static {
        client = new AsyncHttpClient();
        client.setConnectTimeout(5000);
        client.setResponseTimeout(5000);
    }

    public static void get(String url, RequestParams params, ResponseHandlerInterface responseHandlerInterface){
        client.addHeader("apikey", "419422c362c538613c1d317c71d090b7");
        client.get(url,params,responseHandlerInterface);
    }

}
