package com.example.jagg;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class AggSettingActivity extends AppCompatActivity {

    FileTool fileTool = new FileTool();

    int hour = 0;
    int minute = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agg_setting);

        EditText et_kw = (EditText)findViewById(R.id.aggSetting_et1);
        et_kw.setText(fileTool.readKeywords());

        final TimePicker tp = (TimePicker)findViewById(R.id.aggSetting_tp);
        CheckBox cb = (CheckBox)findViewById(R.id.aggSetting_cb);

        String refreshTime = fileTool.readRefreshTime();
        if(!refreshTime.equals("")){
            String[] hm = refreshTime.split("-");
            hour = Integer.valueOf(hm[0]);
            minute = Integer.valueOf(hm[1]);
            tp.setHour(hour);
            tp.setMinute(minute);

            cb.setChecked(true);
            tp.setVisibility(View.VISIBLE);
        }

        tp.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int h, int m) {
                hour = h;
                minute = m;
            }
        });

        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    tp.setVisibility(View.VISIBLE);

                    hour = tp.getHour();
                    minute = tp.getMinute();
                }else{
                    tp.setVisibility(View.INVISIBLE);
                }
            }
        });
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
            String keywords = et_kw.getText().toString();
            if(keywords.isEmpty()){
                Toast.makeText(AggSettingActivity.this, "关键词不能为空！",Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
            }

            String refreshTime = hour+"-"+minute;

            CheckBox cb = (CheckBox)findViewById(R.id.aggSetting_cb);
            if(!cb.isChecked()){
                refreshTime = "";
                Toast.makeText(AggSettingActivity.this, "已取消定时刷新",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(AggSettingActivity.this, "已设置更新时间为每日"+refreshTime,Toast.LENGTH_SHORT).show();

                //开启定时服务
                Calendar calendar = Calendar.getInstance();
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), hour,minute, 0);
                long timeInMillis = calendar.getTimeInMillis();

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(this, RefreshAggInfoService.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
                alarmManager.setRepeating(AlarmManager.RTC, timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent);
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
