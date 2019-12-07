package com.example.jagg;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置主界面
        setContentView(R.layout.activity_main);

        //actionbar上的设置键
        //ActionBar actionBar = getSupportActionBar();
        //actionBar.setHomeButtonEnabled(true);
        //actionBar.setDisplayHomeAsUpEnabled(true);

        //图标和文字放在GridLayout中
        GridLayout grid = (GridLayout) findViewById(R.id.mainPage_layout);

        //GridLayout的子布局数目
        final int childCount = grid.getChildCount();

        for (int i= 0; i < childCount-1; i++){
            //找到第i个图标
            final RelativeLayout container = (RelativeLayout) grid.getChildAt(i);

            //设置鼠标点击事件
            container.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view){
                    //向webActivity发送信息并调用webActivity

                    TextView textView = (TextView) container.getChildAt(1);
                    //Log.i("detailUrl",textView.getContentDescription().toString());

                    Intent intent = new Intent(MainActivity.this, WebActivity.class);
                    intent.putExtra("siteUrl",textView.getContentDescription().toString());
                    intent.putExtra("siteName",textView.getText());
                    startActivity(intent);
                }
            });
        }

        //最后一个图标（添加网站）的点击事件
        final RelativeLayout container = (RelativeLayout) grid.getChildAt(childCount-1);
        container.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                //10dp=1pixels
                float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());

                //ImageView
                ImageView iv = new ImageView(MainActivity.this);
                iv.setId(R.id.reservedId);
                iv.setBackgroundColor(Color.YELLOW);
                RelativeLayout.LayoutParams imageViewParams = new RelativeLayout.LayoutParams(
                        (int)(5*pixels),(int)(5*pixels));
                imageViewParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                imageViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                imageViewParams.setMargins((int)(0.8*pixels),(int)(0.8*pixels),(int)(0.8*pixels),(int)(0.8*pixels));
                iv.setLayoutParams(imageViewParams);

                //TextView
                TextView tv = new TextView(MainActivity.this);
                tv.setText("TEST");
                RelativeLayout.LayoutParams textViewParams = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                textViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                textViewParams.addRule(RelativeLayout.BELOW,R.id.reservedId);
                tv.setLayoutParams(textViewParams);

                //组合成RelativeLayout
                RelativeLayout rv = new RelativeLayout(MainActivity.this);
                GridLayout.LayoutParams subGridParams = new GridLayout.LayoutParams(
                        GridLayout.spec(GridLayout.UNDEFINED,GridLayout.FILL,1f),
                        GridLayout.spec(GridLayout.UNDEFINED,GridLayout.FILL,1f));
                subGridParams.setGravity(Gravity.CENTER_HORIZONTAL);
                rv.addView(iv);
                rv.addView(tv);
                rv.setLayoutParams(subGridParams);

                //添加
                GridLayout grid = (GridLayout) findViewById(R.id.mainPage_layout);
                grid.addView(rv,childCount-1);
            }
        });

    }

    //调用menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
