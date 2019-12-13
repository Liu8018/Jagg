package com.example.jagg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AggActivity extends AppCompatActivity {

    String jaggRootPath = Environment.getExternalStorageDirectory() + File.separator + "Jagg";

    ListView listView_left;
    ListView listView_right;

    ArrayList<csElement> csElems;
    ArrayList<InfoElement> infoElems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agg);

        //加载checkSitesList
        loadCheckSitesList();

        //若数据库已有爬下来的信息列表，则直接加载
        if(aggInfosExist()){

        }
        //若没有则刷新
        else{

        }
    }

    //加载checkSitesList
    void loadCheckSitesList(){
        //从checklist.xml读取check信息

    }

    //检查数据库是否已有爬下来的信息列表
    boolean aggInfosExist(){
        return false;
    }

    //刷新聚合信息
    void refreshAggInfos(){

    }
}

class csElement {
    boolean checked;
    String siteName;
}

class CsElemAdapter extends ArrayAdapter<csElement> {
    private int index;

    public CsElemAdapter(Context context, int id, List<csElement> objects) {
        super(context,id,objects);
        index = id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        csElement csElem = getItem(position); // 获取当前项的csElement实例
        View view;
        if(convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(index, null);
        } else {
            view = convertView;
        }
        CheckBox cb = (CheckBox) view.findViewById(R.id.csElem_checkBox);
        TextView tv = (TextView) view.findViewById(R.id.csElem_siteName);

        cb.setChecked(csElem.checked);
        tv.setText(csElem.siteName);

        return view;
    }
}