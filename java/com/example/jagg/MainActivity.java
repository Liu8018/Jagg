package com.example.jagg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置主界面
        setContentView(R.layout.activity_main);

        //图标和文字放在GridLayout中
        GridLayout grid = (GridLayout) findViewById(R.id.mainPage_layout);
        int childCount = grid.getChildCount();

        for (int i= 0; i < childCount; i++){
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
