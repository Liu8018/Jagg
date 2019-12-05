package com.example.jagg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class InfoActivity extends AppCompatActivity {
    private String siteName;
    private String siteUrl;
    private String keyWords;
    private int pageId;

    private WebTool webTool = new WebTool();

    ArrayList<InfoElement> infoElems;

    ListView listView;

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置信息列表界面
        setContentView(R.layout.activity_info);

        //变量初始化
        pageId = 0;
        listView = (ListView) findViewById(R.id.listView);

        //接收从webActvity发送的信息（网站链接，搜索关键词）
        Intent intent = getIntent();
        siteName = intent.getStringExtra("siteName");
        siteUrl = intent.getStringExtra("siteUrl");
        keyWords = intent.getStringExtra("keyWords");

        //加载对应的网站信息
        loadInfos();

        //初始化 显示“当前页id/总页数”的textView
        textView = (TextView)findViewById(R.id.infoPage_textView);
        textView.setText((pageId+1) + "/" + webTool.npages);

        //设置listView的点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //发送信息给webActivity并调用webActivity

                InfoElement infoElem = infoElems.get(position);

                Intent intent = new Intent(InfoActivity.this, WebActivity.class);
                intent.putExtra("detailUrl",infoElem.dUrl);
                startActivity(intent);
            }
        });

        //“上一页”按钮点击事件
        Button prevButton = (Button)findViewById(R.id.infoPage_prevPageButton);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageId--;
                if(pageId < 0)
                    pageId = 0;

                loadInfos();
                textView.setText((pageId+1) + "/" + webTool.npages);
            }
        });

        //“下一页”按钮点击事件
        Button nextButton = (Button)findViewById(R.id.infoPage_nextPageButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageId++;
                if(pageId > webTool.npages-1)
                    pageId = webTool.npages-1;

                loadInfos();
                textView.setText((pageId+1) + "/" + webTool.npages);
            }
        });
    }

    //加载对应的网站信息
    private void loadInfos() {
        infoElems = webTool.crawlInfoList(siteUrl,keyWords,pageId);

        InfoElemAdapter adapter = new InfoElemAdapter(
                InfoActivity.this, R.layout.info_element, infoElems);
        listView.setAdapter(adapter);
    }
}

class InfoElemAdapter extends ArrayAdapter<InfoElement> {
    private int index;

    public InfoElemAdapter(Context context, int id, List<InfoElement> objects) {
        super(context,id,objects);
        index = id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        InfoElement infoElem = getItem(position); // 获取当前项的InfoElement实例
        View view;
        if(convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(index, null);
        } else {
            view = convertView;
        }
        TextView info = (TextView) view.findViewById(R.id.infoElem_info);
        TextView date = (TextView) view.findViewById(R.id.infoElem_date);

        info.setText(infoElem.info);
        date.setText(infoElem.date);

        return view;
    }
}