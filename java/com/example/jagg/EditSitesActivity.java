package com.example.jagg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.ListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class EditSitesActivity extends AppCompatActivity {

    String jaggRootPath = Environment.getExternalStorageDirectory() + File.separator + "Jagg";

    ListView listView;
    ArrayList<csElement> csElems = new ArrayList<csElement>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_sites);

        setTitle("编辑网站");

        Button bt_back = (Button)findViewById(R.id.editSite_buttonBack);
        Button bt_ok = (Button)findViewById((R.id.editSite_buttonOk));

        //“返回”按钮
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //“确认”按钮
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //读取check列表，删除sites.xml中对应的网站
                int cbCount = listView.getChildCount();

                boolean[] checkList = new boolean[cbCount];

                for(int i=0;i<cbCount;i++){
                    ConstraintLayout cl = (ConstraintLayout) listView.getChildAt(i);
                    CheckBox cb = (CheckBox) cl.getChildAt(1);
                    checkList[i] = cb.isChecked();
                }

                changeSitesXml(checkList);


                Intent intent = getIntent();
                intent.putExtra("checkList", checkList);
                setResult(2, intent);

                finish();
            }
        });

        loadCsElems();
        listView = (ListView)findViewById(R.id.editSites_listview);
        CsElemAdapter adapter = new CsElemAdapter(
                EditSitesActivity.this, R.layout.check_site_element, csElems);
        listView.setAdapter(adapter);
    }

    //将网站信息装入csElems列表中
    void loadCsElems(){
        try {
            File sitesXml = new File(jaggRootPath+"/sites.xml");
            Document doc = Jsoup.parse(sitesXml, "UTF-8", "");
            Elements sites = doc.select("site");
            for(Element site:sites){
                String siteName = site.select("name").text();

                csElement csElem = new csElement();
                csElem.checked = true;
                csElem.siteName = siteName;
                csElems.add(csElem);
            }
        }
        catch (IOException e) {
            Log.e("jsoup error","ioexception");
        }
    }

    //删除sites.xml中对应的网站
    void changeSitesXml(boolean[] checkList){
        FileTool fileTool = new FileTool();

        String sitesStr =  fileTool.readFileToString(jaggRootPath+"/sites.xml");
        String newSitesStr = "<Doc>\n";

        String[] lines = sitesStr.split("\n");
        for(int i=0;i<checkList.length;i++){
            if(checkList[i]){
                newSitesStr += lines[1+i] + "\n";
            }
        }

        newSitesStr += "</Doc>";
        fileTool.writeStringToFile(jaggRootPath+"/sites.xml",newSitesStr);
    }
}
