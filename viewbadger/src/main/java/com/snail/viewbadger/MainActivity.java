package com.snail.viewbadger;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.readystatesoftware.viewbadger.BadgeView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView notice = (TextView) findViewById(R.id.tv_notice);

        //通过代码实现
        BadgeView badgeView = new BadgeView(this,notice);
        badgeView.setText("OK");
        badgeView.show();
    }
}
