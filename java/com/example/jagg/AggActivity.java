package com.example.jagg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import java.util.Random;

public class AggActivity extends AppCompatActivity {

    String jaggRootPath = Environment.getExternalStorageDirectory() + File.separator + "Jagg";

    FileTool fileTool = new FileTool();

    ListView listView_left;
    ListView listView_right;

    ArrayList<csElement> csElems = new ArrayList<csElement>();
    ArrayList<InfoElement> infoElems = new ArrayList<InfoElement>();

    private ProgressDialog processDialog;
    private Handler handler =new Handler(){
        @Override
        //当有消息发送出来的时候就执行Handler的这个方法
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            //只要执行到这里就关闭对话框
            processDialog.dismiss();

            loadAggInfos();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agg);

        setTitle("聚合");

        //显示关键词的textView
        TextView textview = (TextView)findViewById(R.id.agg_keywordText);
        textview.setText("关键词："+fileTool.readKeywords());

        //加载checkSitesList
        loadCheckSitesList();

        listView_left = (ListView)findViewById(R.id.agg_listview_left);
        CsElemAdapter adapter = new CsElemAdapter(AggActivity.this, R.layout.check_site_element_inagg, csElems);
        listView_left.setAdapter(adapter);

        //设置listView_left的点击事件
        listView_left.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //改变csElems
                csElems.get(position).checked = !csElems.get(position).checked;
                //改变checkbox列表
                ConstraintLayout cl = (ConstraintLayout) listView_left.getChildAt(position-listView_left.getFirstVisiblePosition());
                CheckBox cb = (CheckBox) cl.getChildAt(1);
                cb.setChecked(csElems.get(position).checked);

                //写入checklist.xml
                changeChecklistXml(position,csElems.get(position).checked);
            }
        });

        //若数据库还没有爬下来的信息列表，则刷新
        infoElems = fileTool.readAggInfos();
        if(infoElems.isEmpty()){
            refreshAggInfos();
        }
        //加载信息列表
        loadAggInfos();

        //设置listView_right的点击事件
        listView_right.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //发送信息给webActivity并调用webActivity
                InfoElement infoElem = infoElems.get(position);

                Intent intent = new Intent(AggActivity.this, WebActivity_dview.class);
                intent.putExtra("siteUrl",infoElem.dUrl);
                intent.putExtra("siteName",infoElem.info);
                startActivity(intent);
            }
        });
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

    //刷新聚合信息，并写入到agg_infos.xml
    void refreshAggInfos(){
        //先清空infoElems
        infoElems.clear();

        //读取网站链接列表
        String[] siteUrls = new String[csElems.size()];
        try {
            File xml = new File(jaggRootPath+"/sites.xml");
            Document doc = Jsoup.parse(xml, "UTF-8", "");
            Elements sites = doc.select("site");
            for(int i=0;i<sites.size();i++){
                String siteUrl = sites.get(i).select("url").text();
                siteUrls[i] = siteUrl;
            }
        }
        catch (IOException e) {
            Log.e("jsoup error","ioexception");
        }

        //获取关键词
        String keywords = fileTool.readKeywords();

        //遍历网站，找到checked的网站获取信息
        ArrayList<ArrayList<InfoElement>> allSiteInfos = new ArrayList<>();
        int maxSiteInfosLen=0;
        WebTool webTool = new WebTool();
        for(int i=0;i<csElems.size();i++){
            ArrayList<InfoElement> tmpInfoElems = new ArrayList<>();
            if(csElems.get(i).checked)
                tmpInfoElems = webTool.crawlInfoList(siteUrls[i],keywords,0);
            allSiteInfos.add(tmpInfoElems);

            if(tmpInfoElems.size() > maxSiteInfosLen)
                maxSiteInfosLen = tmpInfoElems.size();
        }

        for(int i=0;i<maxSiteInfosLen;i++){
            for(int sId=0;sId<allSiteInfos.size();sId++){
                if(allSiteInfos.get(sId).size() > i) {
                    InfoElement elem = allSiteInfos.get(sId).get(i);
                    if(elem.info.equals("-1"))
                        continue;
                    infoElems.add(elem);
                }
            }
        }

        fileTool.writeAggInfos(infoElems);
    }

    //加载聚合信息
    void loadAggInfos(){
        listView_right = (ListView)findViewById(R.id.agg_listview_right);
        InfoElemAdapter_inagg adapter = new InfoElemAdapter_inagg(AggActivity.this, R.layout.info_element_inagg, infoElems);
        listView_right.setAdapter(adapter);
    }

    //改动写入checklist.xml
    void changeChecklistXml(int position, boolean checked){
        FileTool fileTool = new FileTool();
        String xml = fileTool.readFileToString(jaggRootPath+"/checklist.xml");
        String[] lines = xml.split("\n");
        if(checked){
            lines[1+position] = "<checked>1</checked>";
        }
        else{
            lines[1+position] = "<checked>0</checked>";
        }
        String newXml = "";
        for(String line:lines){
            newXml += line + "\n";
        }
        fileTool.writeStringToFile(jaggRootPath+"/checklist.xml",newXml);
    }

    //调用menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.agg_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //menu按钮点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.agg_menu_setting) {
            //启动AggSettingActivity
            Intent  intent=new Intent(AggActivity.this,AggSettingActivity.class);
            startActivityForResult(intent,1);
        }
        else if (item.getItemId() == R.id.agg_menu_refresh){

            //构建一个等待界面
            processDialog= ProgressDialog.show(AggActivity.this, "", "正在刷新…");
            new Thread(){
                public void run(){
                    //在这里执行长耗时方法
                    refreshAggInfos();

                    //执行完毕后给handler发送一个消息
                    handler.sendEmptyMessage(0);
                }
            }.start();

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==1 && requestCode==1){
            TextView textview = (TextView) findViewById(R.id.agg_keywordText);
            textview.setText("关键词：" + fileTool.readKeywords());

            //构建一个等待界面
            processDialog= ProgressDialog.show(AggActivity.this, "", "正在刷新…");
            new Thread(){
                public void run(){
                    //在这里执行长耗时方法
                    refreshAggInfos();

                    //执行完毕后给handler发送一个消息
                    handler.sendEmptyMessage(0);
                }
            }.start();
        }

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

class InfoElemAdapter_inagg extends ArrayAdapter<InfoElement> {
    private int index;

    public InfoElemAdapter_inagg(Context context, int id, List<InfoElement> objects) {
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
        TextView info = (TextView) view.findViewById(R.id.infoElem_info_inagg);
        TextView date = (TextView) view.findViewById(R.id.infoElem_date_inagg);

        info.setText(infoElem.info);
        date.setText(infoElem.date);

        if(date.getText().equals("")){
            date.setHeight(0);
        }

        return view;
    }
}