package com.example.jagg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class MyStarsActivity extends AppCompatActivity {

    FileTool fileTool = new FileTool();

    ArrayList<InfoElement> infoElems = new ArrayList<InfoElement>();

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_stars);

        setTitle("我的收藏");

        infoElems = fileTool.readStarInfos_detail();
        InfoElemAdapter adapter = new InfoElemAdapter(
                MyStarsActivity.this, R.layout.info_element, infoElems);
        listView = (ListView)findViewById(R.id.myStars_listview);
        listView.setAdapter(adapter);

        //设置listView的点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //发送信息给webActivity并调用webActivity
                InfoElement infoElem = infoElems.get(position);

                Intent intent = new Intent(MyStarsActivity.this, WebActivity_dview.class);
                intent.putExtra("siteUrl",infoElem.dUrl);
                intent.putExtra("siteName",infoElem.info);
                startActivity(intent);
            }
        });
    }
}
