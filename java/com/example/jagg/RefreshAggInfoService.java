package com.example.jagg;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.sql.Ref;
import java.util.ArrayList;

import static android.content.Context.NOTIFICATION_SERVICE;

public class RefreshAggInfoService extends BroadcastReceiver {

    String jaggRootPath = Environment.getExternalStorageDirectory() + File.separator + "Jagg";

    FileTool fileTool = new FileTool();

    ArrayList<csElement> csElems = new ArrayList<csElement>();
    ArrayList<InfoElement> infoElems = new ArrayList<InfoElement>();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("debug_ Alarm Bell","Alarm just fired");

        loadCheckSitesList();
        refreshAggInfos();

        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        //8.0 以后需要加上channelId 才能正常显示
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String channelId = "default";
            String channelName = "默认通知";
            manager.createNotificationChannel(new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH));
        }

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(AggActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context, "default")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Jagg")
                .setContentText("您的关注信息已更新")
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .build();

        manager.notify(1, notification);
    }

    //加载checkSitesList
    void loadCheckSitesList(){
        //从sites.xml读取网站信息
        FileTool fileTool = new FileTool();
        csElems = fileTool.loadCsElems();

        //从checklist.xml读取check信息
        try {
            File xml = new File(jaggRootPath+"/checklist.xml");
            Document doc = Jsoup.parse(xml, "UTF-8", "");
            Elements checklist = doc.select("checked");
            for(int i=0;i<checklist.size();i++){
                if(checklist.get(i).text().equals("0")){
                    csElems.get(i).checked = false;
                }
                else{
                    csElems.get(i).checked = true;
                }
            }
        }
        catch (IOException e) {
            Log.e("jsoup error","ioexception");
        }
    }

    //刷新聚合信息，并写入到agg_infos.xml
    void refreshAggInfos(){
        //先清空infoElems
        infoElems.clear();

        //读取网站链接列表
        String[] siteUrls = new String[csElems.size()];
        try {
            File xml = new File(jaggRootPath+"/sites.xml");
            Document doc = Jsoup.parse(xml, "UTF-8", "");
            Elements sites = doc.select("site");
            for(int i=0;i<sites.size();i++){
                String siteUrl = sites.get(i).select("url").text();
                siteUrls[i] = siteUrl;
            }
        }
        catch (IOException e) {
            Log.e("jsoup error","ioexception");
        }

        //获取关键词
        String keywords = fileTool.readKeywords();

        //遍历网站，找到checked的网站获取信息
        WebTool webTool = new WebTool();
        for(int i=0;i<csElems.size();i++){
            if(csElems.get(i).checked) {
                ArrayList<InfoElement> tmpInfoElems = webTool.crawlInfoList(siteUrls[i],keywords,0);
                infoElems.addAll(tmpInfoElems);
            }
        }

        fileTool.writeAggInfos(infoElems);
    }

}
