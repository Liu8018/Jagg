package com.example.jagg;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

    String jaggRootPath = Environment.getExternalStorageDirectory() + File.separator + "Jagg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置主界面
        setContentView(R.layout.activity_main);

        //actionbar上的设置键(左边)
        Drawable drawable= getResources().getDrawable(R.drawable.threelines);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        int actionBarHeight=0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }
        actionBarHeight *= 0.6;
        Drawable newdrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(
                bitmap, actionBarHeight,actionBarHeight, true));
        //newdrawable.setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(newdrawable);

        //检查应用根目录是否存在，不存在则创建
        File jaggDir = new File(jaggRootPath);
        if(!jaggDir.exists() || !jaggDir.isDirectory()){
            jaggDir.mkdirs();

            //复制assets下的文件到应用根目录（使得可以修改数据）
            copyFilesFassets(this,"sites.xml",jaggRootPath+"/sites.xml");
        }

        //加载网站信息
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

            addSiteToUI(siteName,siteUrl);
            addSiteToXml(siteName,siteUrl);
        }

    }

    //将网站信息添加到sites.xml
    private void addSiteToXml(String siteName, String siteUrl){
        String xml = readFileToString(jaggRootPath+"/sites.xml");

        String newSite = "<site><name>"+siteName+"</name><url>"+siteUrl+"</url></site>";
        xml = xml.substring(0,xml.length()-6) + newSite + "\n\n</Doc>";

        writeStringToFile(jaggRootPath+"/sites.xml",xml);
    }

    //读取文件内容为一个String
    public String readFileToString(String fileName) {
        String encoding = "UTF-8";
        File file = new File(fileName);
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return new String(filecontent, encoding);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    //写入String到一个文件
    void writeStringToFile(String filePath, String str){
        try{
            FileOutputStream fos = new FileOutputStream(filePath);
            // 把数据写入到输出流
            fos.write(str.getBytes());
            // 关闭输出流
            fos.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    //添加网站到界面
    private void addSiteToUI(final String siteName, final String siteUrl){
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
        tv.setEllipsize(TextUtils.TruncateAt.valueOf("END"));//字符串太长就在末尾加省略号
        tv.setMaxLines(2);//限制最大行数
        tv.setMaxWidth((int)(8*pixels));//限制最大宽度
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

    //从sites.xml读取网站信息
    void loadSites(){
        try {
            File xml = new File(jaggRootPath+"/sites.xml");
            Document doc = Jsoup.parse(xml, "UTF-8", "");
            Elements sites = doc.select("site");
            for(Element site:sites){
                String siteName = site.select("name").text();
                String siteUrl = site.select("url").text();
                addSiteToUI(siteName,siteUrl);
            }
        }
        catch (IOException e) {
            Log.e("jsoup error","ioexception");
        }
    }

    //返回键响应
    @Override
    public boolean onSupportNavigateUp() {
        DrawerLayout dl = (DrawerLayout) findViewById(R.id.main_drawerlayout);
        dl.openDrawer(Gravity.LEFT);
        return super.onSupportNavigateUp();
    }
}
