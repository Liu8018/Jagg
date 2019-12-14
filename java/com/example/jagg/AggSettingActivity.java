package com.example.jagg;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class AggSettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agg_setting);
    }

    //调用menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.agg_setting_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //menu按钮响应
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.agg_setting_menu_cancel) {
            finish();
        }
        else if (item.getItemId() == R.id.agg_setting__menu_ok){
            final EditText et_kw = (EditText)findViewById(R.id.aggSetting_et1);
            final EditText et_rt = (EditText)findViewById(R.id.aggSetting_et2);
            String keywords = et_kw.getText().toString();
            String refreshTime = et_rt.getText().toString();

            if(keywords.isEmpty()){
                Toast.makeText(AggSettingActivity.this, "关键词不能为空！",Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
            }
            if(refreshTime.isEmpty()){
                refreshTime = "0";
            }

            FileTool fileTool = new FileTool();
            fileTool.writeKeyword(keywords);
            fileTool.writeRefreshTime(refreshTime);

            setResult(1);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
