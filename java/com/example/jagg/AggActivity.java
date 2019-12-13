package com.example.jagg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
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

        setTitle("聚合");

        //加载checkSitesList
        loadCheckSitesList();

        listView_left = (ListView)findViewById(R.id.agg_listview_left);
        CsElemAdapter adapter = new CsElemAdapter(AggActivity.this, R.layout.check_site_element_inagg, csElems);
        listView_left.setAdapter(adapter);

        //设置listView_left的点击事件
        listView_left.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                csElems.get(position).checked = !csElems.get(position).checked;
                ConstraintLayout cl = (ConstraintLayout) listView_left.getChildAt(position-listView_left.getFirstVisiblePosition());
                CheckBox cb = (CheckBox) cl.getChildAt(1);
                cb.setChecked(csElems.get(position).checked);
            }
        });

        //若数据库已有爬下来的信息列表，则直接加载
        if(aggInfosExist()){

        }
        //若没有则刷新
        else{

        }
    }

    //加载checkSitesList
    void loadCheckSitesList(){
        //从sites.xml读取网站信息
        FileTool fileTool = new FileTool();
        csElems = fileTool.loadCsElems();

        //从checklist.xml读取check信息
        try {
            File xml = new File(jaggRootPath+"/checklist.xml");
            Document doc = Jsoup.parse(xml, "UTF-8", "");
            Elements checklist = doc.select("checked");
            for(int i=0;i<checklist.size();i++){
                if(checklist.get(i).text().equals("0")){
                    csElems.get(i).checked = false;
                }
                else{
                    csElems.get(i).checked = true;
                }
            }
        }
        catch (IOException e) {
            Log.e("jsoup error","ioexception");
        }
    }

    //检查数据库是否已有爬下来的信息列表
    boolean aggInfosExist(){
        return false;
    }

    //刷新聚合信息
    void refreshAggInfos(){

    }

    //调用menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.agg_menu, menu);
        return super.onCreateOptionsMenu(menu);
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
        tv.setMaxLines(1);
        tv.setEllipsize(TextUtils.TruncateAt.valueOf("END"));//字符串太长就在末尾加省略号

        cb.setChecked(csElem.checked);
        tv.setText(csElem.siteName);

        return view;
    }
}