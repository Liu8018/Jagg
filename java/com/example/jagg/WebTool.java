package com.example.jagg;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class WebTool {
    int npages;

    public ArrayList<InfoElement> crawlInfoList(String siteUrl, String keyWords, int pageId) {
        ArrayList<InfoElement> infos = new ArrayList<InfoElement>();

        Site_baidu baidu = new Site_baidu();
        baidu.getInfos(siteUrl,keyWords,pageId);
        infos = baidu.infos;
        npages = baidu.npages;

        return infos;
    }

}

class InfoElement {
    String info;
    String date;
    String dUrl;
}

class funcs {
    public static String getTrueUrlPath(String siteUrl, String subUrl) {
        if(subUrl.startsWith("/"))
            subUrl = subUrl.substring(1, subUrl.length()-1);

        String url = siteUrl.substring(0,siteUrl.lastIndexOf("/")+1) + subUrl;

        String[] urlList = url.split("/");
        boolean[] resList = new boolean[urlList.length];

        for(int i=0;i<urlList.length;i++) {
            resList[i] = true;
            if(urlList[i].contains("..")) {
                resList[i] = false;
                resList[i-1] = false;
            }

        }

        url = "";
        for(int i=0;i<urlList.length;i++) {
            if(resList[i] == false)
                continue;

            if(i==urlList.length-1)
                url += urlList[i];
            else
                url += urlList[i] + "/";
        }

        return url;
    }
}

class Site_baidu {
    public int npages = 1;
    public ArrayList<InfoElement> infos = new ArrayList<InfoElement>();

    public void getInfos(String siteUrl, String keyWords, final int pageId){
        siteUrl = siteUrl.replace("https://","");
        siteUrl = siteUrl.replace("http://","");

        keyWords += " site:"+siteUrl;

        final String pageUrl = "https://www.baidu.com/s?pn="+pageId*10+"&wd="+keyWords;

        //Log.i("pageUrl", pageUrl);

        Thread th = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    //百度搜索结果需要多次爬取才能成功
                    //此处应设置一个最大尝试次数
                    Elements els;
                    Document doc;
                    int codeLength = 0;

                    int maxTryTimes = 50;
                    int tryTimes = 0;
                    do {
                        //获取源码
                        doc = Jsoup.connect(pageUrl)
                                //.data("query", "Java")
                                //.userAgent("Mozilla/4.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)")
                                .get();
                        els = doc.select("div[class=result c-container ]");

                        //Log.i("debug_code",doc.html());
                        //Log.i("debug_code length", String.valueOf(doc.html().length()));
                        codeLength = doc.html().length();
                        if(codeLength > 8000){
                            break;
                        }

                        tryTimes++;

                    } while(tryTimes < maxTryTimes);

                    //若没有搜索成功
                    if(tryTimes == maxTryTimes){
                        InfoElement elem = new InfoElement();
                        elem.info = "-1";
                        elem.date = "-1";
                        elem.dUrl = "-1";
                        infos.add(elem);
                        return;
                    }

                    //Log.i("debug element 结果数",String.valueOf(els.size()));
                    //Log.i("element內容 第一个", els.get(0).toString());

                    //FileTool fileTool = new FileTool();
                    //fileTool.writeStringToFile(Environment.getExternalStorageDirectory()+"/Jagg/debug_html.txt",doc.html());

                    //获取搜索结果页数
                    Elements pageIds = doc.select("span[class=pc]");
                    if(!pageIds.isEmpty()) {
                        String npagestr = pageIds.get(pageIds.size() - 1).text().toString();
                        if (!npagestr.equals(""))
                            npages = Integer.valueOf(npagestr);
                        else
                            npages = 1;
                    }
                    else
                        npages = 1;

                    //获取每条信息标题
                    String[] str_tls = new String[els.size()];
                    for(int i=0;i<els.size();i++) {
                        str_tls[i] = els.get(i).select("a").get(0).text();
                        //Log.i("title_test", str_tls[i]);
                    }

                    //获取每条信息简介

                    //获取每条信息的url
                    String[] str_dus = new String[els.size()];
                    for(int i=0;i<els.size();i++) {
                        str_dus[i] = els.get(i).select("a").attr("href");
                        //str_dus[i] = funcs.getTrueUrlPath(pageUrl,str_dus[i]);
                        //Log.i("detailUrl",str_dus[i]);
                    }

                    //获取每条信息时间
                    String[] str_dts = new String[els.size()];
                    for(int i=0;i<els.size();i++) {
                        str_dts[i] = els.get(i).select("span[class= newTimeFactor_before_abs m]").text();
                        //Log.i("date_test", str_dts[i]);
                    }

                    for(int i=0;i<str_tls.length;i++) {
                        InfoElement infoElem = new InfoElement();
                        infoElem.info = str_tls[i];
                        infoElem.date = str_dts[i];
                        infoElem.dUrl = str_dus[i];

                        infos.add(infoElem);
                    }
                }
                catch (IOException e) {
                    Log.e("jsoup error","ioexception");
                }
            }
        };

        th.start();
        try {
            th.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

