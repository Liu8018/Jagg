package com.example.jagg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddSiteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_site);

        setTitle("添加网站");

        final EditText et_name = (EditText)findViewById(R.id.add_site_name);
        final EditText et_url = (EditText)findViewById(R.id.add_site_url);
        Button bt_cancel = (Button)findViewById(R.id.add_site_cancel);
        Button bt_ok = (Button)findViewById((R.id.add_site_ok));

        //“取消”按钮
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //“确认”按钮
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                intent.putExtra("siteName", et_name.getText().toString());
                intent.putExtra("siteUrl", et_url.getText().toString());
                setResult(1, intent);
                finish();
            }
        });

    }
}
