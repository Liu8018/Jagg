package com.example.jagg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
import android.view.MenuItem;
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
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    String jaggRootPath = Environment.getExternalStorageDirectory() + File.separator + "Jagg";

    FileTool fileTool = new FileTool();

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };
    public static void verifyStoragePermissions(Activity activity) {
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //存储权限
        verifyStoragePermissions(MainActivity.this);

        //设置主界面
        setContentView(R.layout.activity_main);

        //检查应用根目录是否存在，不存在则创建
        File jaggDir = new File(jaggRootPath);
        if(!jaggDir.exists() || !jaggDir.isDirectory()){
            jaggDir.mkdirs();

            //复制assets下的文件到应用根目录（使得可以修改数据）
            String[] fileNames = {"sites.xml","agg_infos.xml","checklist.xml","push_settings.xml"};
            for(String fileName:fileNames) {
                fileTool.copyFilesFassets(this, fileName, jaggRootPath + "/" + fileName);
            }
        }

        //加载网站信息
        loadSites();

        //第一个图标（聚合）的点击事件
        GridLayout grid = (GridLayout) findViewById(R.id.mainPage_layout);
        final RelativeLayout container0 = (RelativeLayout) grid.getChildAt(0);
        container0.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                //启动AggActivity
                Intent  intent=new Intent(MainActivity.this,AggActivity.class);
                startActivity(intent);
            }
        });

        //最后一个图标（添加网站）的点击事件
        final int childCount = grid.getChildCount();
        final RelativeLayout container = (RelativeLayout) grid.getChildAt(childCount-1);
        container.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                //启动addSiteActivity获取信息（图标颜色、网站名、网站链接）
                Intent  intent=new Intent(MainActivity.this,AddSiteActivity.class);
                startActivityForResult(intent,1);
            }
        });

        //左侧菜单中“编辑网站”的点击事件
        RelativeLayout editSiteBt = (RelativeLayout)findViewById(R.id.main_editSites);
        editSiteBt.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                //Toast.makeText(MainActivity.this, "clicked!",Toast.LENGTH_SHORT).show();
                //启动EditSitesActivity
                Intent  intent=new Intent(MainActivity.this,EditSitesActivity.class);
                startActivityForResult(intent,2);
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
        else if(resultCode==2 && requestCode==2){
            boolean[] checkList = data.getBooleanArrayExtra("checkList");
            changeSiteUI(checkList);
        }

    }

    //将网站信息添加到sites.xml
    private void addSiteToXml(String siteName, String siteUrl){
        String xml = fileTool.readFileToString(jaggRootPath+"/sites.xml");

        String newSite = "<site><name>"+siteName+"</name><url>"+siteUrl+"</url></site>";
        xml = xml.substring(0,xml.length()-6) + newSite + "\n</Doc>";

        fileTool.writeStringToFile(jaggRootPath+"/sites.xml",xml);

        //checklist增加一个
        String checklistXml = fileTool.readFileToString(jaggRootPath+"/checklist.xml");
        checklistXml = checklistXml.substring(0,checklistXml.length()-6) + "<checked>0</checked>\n</Doc>";
        fileTool.writeStringToFile(jaggRootPath+"/checklist.xml",checklistXml);
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

    //actionbar按钮响应
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.main_menu_setting) {
            DrawerLayout dl = (DrawerLayout) findViewById(R.id.main_drawerlayout);
            dl.openDrawer(Gravity.RIGHT);
        }
        else if (item.getItemId() == R.id.main_menu_search){

        }

        return super.onOptionsItemSelected(item);
    }

    //移除主界面对应的图标
    void changeSiteUI(boolean[] checkList){
        GridLayout grid = (GridLayout) findViewById(R.id.mainPage_layout);
        int cId = 1;
        for(int i=0;i<checkList.length;i++){
            //删除没有被勾选的网站
            if(!checkList[i]){
                grid.removeViewAt(cId);
            }
            else{
                cId++;
            }
        }
    }
}
