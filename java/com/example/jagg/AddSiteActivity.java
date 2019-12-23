package com.example.jagg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
                String siteName = et_name.getText().toString();
                String siteUrl = et_url.getText().toString();

                if(siteName.isEmpty()){
                    Toast.makeText(AddSiteActivity.this, "网站名不能为空！",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(siteUrl.isEmpty()){
                    Toast.makeText(AddSiteActivity.this, "网站链接不能为空！",Toast.LENGTH_SHORT).show();
                    return;
                }

                //此处应该检查一下用户设定的网站能否正常访问
                if (!Patterns.WEB_URL.matcher(siteUrl).matches()) {
                    Toast.makeText(AddSiteActivity.this, "链接无效！",Toast.LENGTH_SHORT).show();
                    return;
                } else{
                    if(!urlHasAcceptableScheme(siteUrl)){
                        Toast.makeText(AddSiteActivity.this, "链接必须以http或https开头！",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                Intent intent = getIntent();
                intent.putExtra("siteName", siteName);
                intent.putExtra("siteUrl", siteUrl);
                setResult(1, intent);
                finish();
            }
        });

    }

    private static final String acceptableSchemes[] = {
            "http:",
            "https:",
            "file:"
    };

    private static boolean urlHasAcceptableScheme(String url) {
        if (url == null) {
            return false;
        }

        for (int i = 0; i < acceptableSchemes.length; i++) {
            if (url.startsWith(acceptableSchemes[i])) {
                return true;
            }
        }
        return false;
    }
}
