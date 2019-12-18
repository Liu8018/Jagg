package com.example.jagg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;

import java.util.ArrayList;

public class EditStarsActivity extends AppCompatActivity {

    FileTool fileTool = new FileTool();

    ListView listView;
    ArrayList<csElement> csElems = new ArrayList<csElement>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_stars);

        setTitle("编辑收藏夹");

        FileTool fileTool = new FileTool();
        csElems = fileTool.readStarInfos();
        listView = (ListView)findViewById(R.id.editStars_listview);
        CsElemAdapter adapter = new CsElemAdapter(
                EditStarsActivity.this, R.layout.check_site_element, csElems);
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
            int elemCount = csElems.size();
            ArrayList removeIds = new ArrayList();
            for(int i=0;i<elemCount;i++){
                if(!csElems.get(i).checked){
                    //Log.i("debug_ index",i+".");
                    //fileTool.removeStarInfo(i);
                    removeIds.add(i);
                }
            }
            fileTool.removeStarInfo(removeIds);

            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
