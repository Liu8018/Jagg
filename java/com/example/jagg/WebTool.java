package com.example.jagg;

import android.icu.text.IDNA;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

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

class Site_jwc {
    public int npages = 30;
    private String url = "http://jwc.bit.edu.cn/tzgg/index{i}.htm";
    public String[] pageUrls = new String[npages];

    public ArrayList<InfoElement> infos = new ArrayList<InfoElement>();

    Site_jwc() {
        pageUrls[0] = url.replace("{i}","");
        for(int i=1;i<npages;i++) {
            pageUrls[i] = url.replace("{i}",i+"");
        }
    }

    public void getInfos(int pageId) {
        infos.clear();
        final String pageUrl = pageUrls[pageId];

        Thread th = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Document doc = Jsoup.connect(pageUrl).get();

                    Elements els = doc.select("div.crules_con");
                    //Log.i("element內容", els.toString());

                    Elements tls = els.select("a");
                    String[] str_tls = new String[tls.size()];
                    for(int i=0;i<tls.size();i++) {
                        str_tls[i] = tls.get(i).text();
                        //Log.i("tl", str_tls[i]);
                    }

                    Elements dts = els.select("span");
                    String[] str_dts = new String[dts.size()];
                    for(int i=0;i<dts.size();i++) {
                        str_dts[i] = dts.get(i).text();
                        //Log.i("dt", str_dts[i]);
                    }

                    String[] str_dus = new String[tls.size()];
                    for(int i=0;i<tls.size();i++) {
                        str_dus[i] = tls.get(i).attr("href");
                        str_dus[i] = funcs.getTrueUrlPath(pageUrl,str_dus[i]);
                        //Log.i("du",str_dus[i]);
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

public class WebTool {

    private Site_jwc jwc = new Site_jwc();

    WebTool() {

    }

    public ArrayList<InfoElement> getInfoList(String siteName, int pageId) {
        jwc.getInfos(pageId);

        ArrayList<InfoElement> infos = new ArrayList<InfoElement>();

        if(siteName.equals("jwc"))
            infos = jwc.infos;

        return infos;
    }
}
