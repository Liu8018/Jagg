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

import java.util.ArrayList;
import java.util.List;

public class InfoActivity extends AppCompatActivity {
    private String siteName;
    private int pageId;

    private WebTool webTool = new WebTool();

    ArrayList<InfoElement> infoElems;

    ListView listView;

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        pageId = 0;

        Intent intent = getIntent();
        siteName = intent.getStringExtra("siteName");

        listView = (ListView) findViewById(R.id.listView);
        loadInfos();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InfoElement infoElem = infoElems.get(position);

                Intent intent = new Intent(InfoActivity.this, WebActivity.class);
                intent.putExtra("detailUrl",infoElem.dUrl);
                startActivity(intent);
            }
        });

        textView = (TextView)findViewById(R.id.infoPage_textView);
        textView.setText((pageId+1) + "/" + webTool.npages);

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

    private void loadInfos() {
        infoElems = webTool.getInfoList(siteName,pageId);

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