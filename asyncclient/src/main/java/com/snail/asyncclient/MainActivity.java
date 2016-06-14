package com.snail.asyncclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {

    private WebView mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mView = (WebView) findViewById(R.id.wb_load);
        mView.getSettings().setJavaScriptEnabled(true);
        mView.loadUrl("file:///android_asset/index.html");

        mView.addJavascriptInterface(new Test(), "test");
    }

    private final class Test {

        @JavascriptInterface
        public void showData() {
            System.out.println("显示数据");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //使用原生的jsonObject
                   /* try {
                        JSONObject json=new JSONObject();
                        json.put("count",100);
                        json.put("title","你大爷的还不行");
                        //String json="你妹啊";
                        System.out.println(json);
                        mView.loadUrl("javascript:showData('" + json.toString() + "')");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
                    //使用gson
                    Student student=new Student();
                    student.setCount(100);
                    student.setTitle("你大爷的");
                    Gson gson=new Gson();
                    mView.loadUrl("javascript:showData('" + gson.toJson(student) + "')");
                }
            });
        }

        @JavascriptInterface
        public void showToast() {
            Toast.makeText(MainActivity.this, "哈哈", Toast.LENGTH_LONG).show();
        }

        @JavascriptInterface
        public void shotInfo(String json) {
            Toast.makeText(MainActivity.this, json, Toast.LENGTH_LONG).show();
        }
    }

    private final class Student{
        private String title;
        private int count;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }


}
