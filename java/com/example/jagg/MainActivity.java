package com.example.jagg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static java.lang.System.in;

public class MainActivity extends AppCompatActivity {

    String jaggRootPath = Environment.getExternalStorageDirectory() + File.separator + "Jagg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置主界面
        setContentView(R.layout.activity_main);

        //actionbar上的设置键(左边)
        //ActionBar actionBar = getSupportActionBar();
        //actionBar.setHomeButtonEnabled(true);
        //actionBar.setDisplayHomeAsUpEnabled(true);

        //检查应用根目录是否存在，不存在则创建
        File jaggDir = new File(jaggRootPath);
        if(!jaggDir.exists() || !jaggDir.isDirectory()){
            jaggDir.mkdirs();

            //复制assets下的文件到应用根目录（使得可以修改数据）
            copyFilesFassets(this,"sites.xml",jaggRootPath+"/sites.xml");
        }

        //加载预先设定的网站
        loadSites();

        //最后一个图标（添加网站）的点击事件
        GridLayout grid = (GridLayout) findViewById(R.id.mainPage_layout);
        final int childCount = grid.getChildCount();
        final RelativeLayout container = (RelativeLayout) grid.getChildAt(childCount-1);
        container.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                //启动addSiteActivity获取信息（图标颜色、网站名、网站链接）
                Intent  intent=new Intent(MainActivity.this,AddSiteActivity.class);
                startActivityForResult(intent,1);
            }
        });

    }

    //调用menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==1 && requestCode==1){
            //接收返回数据
            final String siteName = data.getStringExtra("siteName");
            final String siteUrl = data.getStringExtra("siteUrl");

            addSite(siteName,siteUrl);
        }

    }

    //添加网站
    private void addSite(final String siteName, final String siteUrl){
        //10dp=1pixels
        float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());

        //ImageView
        ImageView iv = new ImageView(MainActivity.this);
        iv.setId(R.id.reservedId);

        Bitmap bitmap = Bitmap.createBitmap(200,200,Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(Color.parseColor("#e0ffff")); //填充颜色
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setTextSize(100);
        paint.setColor(Color.BLACK);
        canvas.drawText(siteName.substring(0,1),50,130,paint);

        iv.setImageBitmap(bitmap);
        RelativeLayout.LayoutParams imageViewParams = new RelativeLayout.LayoutParams(
                (int)(5*pixels),(int)(5*pixels));
        imageViewParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        imageViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        imageViewParams.setMargins((int)(0.8*pixels),(int)(0.8*pixels),(int)(0.8*pixels),(int)(0.8*pixels));
        iv.setLayoutParams(imageViewParams);

        //TextView
        TextView tv = new TextView(MainActivity.this);
        tv.setText(siteName);
        tv.setContentDescription(siteUrl);
        RelativeLayout.LayoutParams textViewParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        textViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        textViewParams.addRule(RelativeLayout.BELOW,R.id.reservedId);
        tv.setLayoutParams(textViewParams);

        //组合成RelativeLayout
        RelativeLayout rv = new RelativeLayout(MainActivity.this);
        GridLayout.LayoutParams subGridParams = new GridLayout.LayoutParams(
                GridLayout.spec(GridLayout.UNDEFINED,GridLayout.FILL,1f),
                GridLayout.spec(GridLayout.UNDEFINED,GridLayout.FILL,1f));
        subGridParams.setGravity(Gravity.CENTER_HORIZONTAL);
        rv.addView(iv);
        rv.addView(tv);
        rv.setLayoutParams(subGridParams);

        //添加到界面
        GridLayout grid = (GridLayout) findViewById(R.id.mainPage_layout);
        int childCount = grid.getChildCount();
        grid.addView(rv,childCount-1);

        //设置点击事件
        rv.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                //向webActivity发送信息并调用webActivity
                Intent intent = new Intent(MainActivity.this, WebActivity.class);
                intent.putExtra("siteUrl",siteUrl);
                intent.putExtra("siteName",siteName);
                startActivity(intent);
            }
        });
    }

    //复制assets下的文件到另一个位置（使得可以修改数据）
    public void copyFilesFassets(Context context, String oldPath, String newPath) {
        try {
            String fileNames[] = context.getAssets().list(oldPath);
            if (fileNames.length > 0) {//如果是目录
                File file = new File(newPath);
                file.mkdirs();//如果文件夹不存在，则递归
                for (String fileName : fileNames) {
                    copyFilesFassets(context,oldPath + "/" + fileName,newPath+"/"+fileName);
                }
            } else {//如果是文件
                InputStream is = context.getAssets().open(oldPath);
                FileOutputStream fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount=0;
                while((byteCount=is.read(buffer))!=-1) {//循环从输入流读取 buffer字节
                    fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
                }
                fos.flush();//刷新缓冲区
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void loadSites(){
        //从sites.xml读取网站信息
        try {
            File xml = new File(jaggRootPath+"/sites.xml");
            Document doc = Jsoup.parse(xml, "UTF-8", "");
            Elements sites = doc.select("site");
            for(Element site:sites){
                String siteName = site.select("name").text();
                String siteUrl = site.select("url").text();
                addSite(siteName,siteUrl);
            }
        }
        catch (IOException e) {
            Log.e("jsoup error","ioexception");
        }
    }
}
