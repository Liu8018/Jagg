package com.example.jagg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;

import java.io.File;
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

        FileTool fileTool = new FileTool();
        csElems = fileTool.loadCsElems();
        listView = (ListView)findViewById(R.id.editSites_listview);
        CsElemAdapter adapter = new CsElemAdapter(
                EditSitesActivity.this, R.layout.check_site_element, csElems);
        listView.setAdapter(adapter);

        //设置listView的点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                csElems.get(position).checked = !csElems.get(position).checked;
                ConstraintLayout cl = (ConstraintLayout) listView.getChildAt(position-listView.getFirstVisiblePosition());
                CheckBox cb = (CheckBox) cl.getChildAt(1);
                cb.setChecked(csElems.get(position).checked);
            }
        });
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
    //删除checklist.xml中对应的网站
    void changeChecklistXml(boolean[] checkList){
        FileTool fileTool = new FileTool();

        String sitesStr =  fileTool.readFileToString(jaggRootPath+"/checklist.xml");
        String newSitesStr = "<Doc>\n";

        String[] lines = sitesStr.split("\n");
        for(int i=0;i<checkList.length;i++){
            if(checkList[i]){
                newSitesStr += lines[1+i] + "\n";
            }
        }

        newSitesStr += "</Doc>";
        fileTool.writeStringToFile(jaggRootPath+"/checklist.xml",newSitesStr);
    }

    //调用menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_sites_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //menu按钮点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.edit_sites_menu_cancel) {
            finish();
        }
        else if (item.getItemId() == R.id.edit_sites_menu_ok){
            //读取check列表，删除sites.xml和checklist.xml中对应的网站
            int elemCount = csElems.size();
            boolean[] checkList = new boolean[elemCount];

            for(int i=0;i<elemCount;i++){
                checkList[i] = csElems.get(i).checked;
            }

            changeSitesXml(checkList);
            changeChecklistXml(checkList);

            Intent intent = getIntent();
            intent.putExtra("checkList", checkList);
            setResult(2, intent);

            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
