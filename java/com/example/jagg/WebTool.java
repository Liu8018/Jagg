package com.example.jagg;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

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

            //Log.i("test urlsec",urlList[i]);
            //Log.i("res",resList[i]+"");
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

    Site_jwc() {
        pageUrls[0] = url.replace("{i}","");
        for(int i=1;i<npages;i++) {
            pageUrls[i] = url.replace("{i}",i+"");
        }
    }

    public String[] getInfos(int pageId) {
        final String pageUrl = pageUrls[pageId];

        new Thread() {
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
                        //Log.i("tl", tls.get(i).text());
                    }

                    Elements dts = els.select("span");
                    String[] str_dts = new String[dts.size()];
                    for(int i=0;i<dts.size();i++) {
                        str_dts[i] = dts.get(i).text();
                        //Log.i("dt", dts.get(i).text());
                    }

                    for(int i=0;i<tls.size();i++) {
                        String dus = tls.get(i).attr("href");
                        //Log.i("dus", dus);
                        //Log.i("dus2",funcs.getTrueUrlPath(pageUrl,dus));

                        
                    }

                }
                catch (IOException e) {
                    Log.e("jsoup error","ioexception");
                }
            }
        }.start();

        return pageUrls;
    }
}

public class WebTool {

    public Site_jwc jwc = new Site_jwc();

    WebTool() {
        for(int i=0;i<jwc.npages;i++) {
            Log.i("WebTool",jwc.pageUrls[i]);
        }

        jwc.getInfos(0);
    }


}
