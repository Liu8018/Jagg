package com.example.jagg;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

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
import java.util.ArrayList;

public class FileTool {

    String jaggRootPath = Environment.getExternalStorageDirectory() + File.separator + "Jagg";

    String aggSettingsXmlPath = jaggRootPath + "/agg_settings.xml";
    String sitesXmlPath = jaggRootPath+"/sites.xml";
    String aggInfosXmlPath = jaggRootPath+"/agg_infos.xml";
    String starInfosXmlPath = jaggRootPath+"/star_infos.xml";

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

    //将网站信息装入csElems列表中
    ArrayList<csElement> loadCsElems(){
        ArrayList<csElement> csElems = new ArrayList<csElement>();

        try {
            File sitesXml = new File(sitesXmlPath);
            Document doc = Jsoup.parse(sitesXml, "UTF-8", "");
            Elements sites = doc.select("site");
            for(Element site:sites){
                String siteName = site.select("name").text();

                csElement csElem = new csElement();
                csElem.checked = true;
                csElem.siteName = siteName;
                csElems.add(csElem);
            }
        }
        catch (IOException e) {
            Log.e("jsoup error","ioexception");
        }

        return csElems;
    }

    //写入关键词信息
    void writeKeyword(String keywords){
        String xml = readFileToString(aggSettingsXmlPath);
        String[] lines = xml.split("\n");
        lines[1] = "<keywords>"+keywords+"</keywords>";
        String newXml = "";
        for(String line:lines){
            newXml += line + "\n";
        }
        writeStringToFile(aggSettingsXmlPath,newXml);
    }

    //读取关键词信息
    String readKeywords(){
        try {
            File xml = new File(aggSettingsXmlPath);
            Document doc = Jsoup.parse(xml, "UTF-8", "");
            Elements keywords = doc.select("keywords");
            return keywords.text();
        }
        catch (IOException e) {
            Log.e("jsoup error","ioexception");
            return "";
        }
    }

    //写入刷新时间
    void writeRefreshTime(String refreshTime){
        String xml = readFileToString(aggSettingsXmlPath);
        String[] lines = xml.split("\n");
        lines[2] = "<refreshTime>"+refreshTime+"</refreshTime>";
        String newXml = "";
        for(String line:lines){
            newXml += line + "\n";
        }
        writeStringToFile(aggSettingsXmlPath,newXml);
    }

    //读取刷新时间
    String readRefreshTime(){
        try {
            File xml = new File(aggSettingsXmlPath);
            Document doc = Jsoup.parse(xml, "UTF-8", "");
            Elements refreshTime = doc.select("refreshTime");
            return refreshTime.text();
        }
        catch (IOException e) {
            Log.e("jsoup error","ioexception");
            return "";
        }
    }

    //写入infos
    void writeAggInfos(ArrayList<InfoElement> infoElems){
        String aggInfoXml = "<Doc>\n";
        for(InfoElement elem:infoElems){
            String line = "<info>"+"<title>"+elem.info+"</title>"+"<date>"+elem.date+"</date>"+"<dUrl>"+elem.dUrl+"</dUrl>"+"</info>\n";
            aggInfoXml += line;
        }
        aggInfoXml += "</Doc>";

        writeStringToFile(aggInfosXmlPath,aggInfoXml);
    }

    //读取infos
    ArrayList<InfoElement> readAggInfos(){
        ArrayList<InfoElement> infoElems = new ArrayList<InfoElement>();

        try {
            File xml = new File(aggInfosXmlPath);
            Document doc = Jsoup.parse(xml, "UTF-8", "");
            Elements infos = doc.select("info");
            for(Element info:infos){
                String title = info.select("title").text();
                String date = info.select("date").text();
                String dUrl = info.select("dUrl").text();
                InfoElement infoElem = new InfoElement();
                infoElem.info = title;
                infoElem.date = date;
                infoElem.dUrl = dUrl;
                infoElems.add(infoElem);
            }
        }
        catch (IOException e) {
            Log.e("jsoup error","ioexception");
        }

        return infoElems;
    }

    //检查网页是否已经收藏
    boolean isStarred(String url){
        boolean isStar = false;

        try {
            File xml = new File(starInfosXmlPath);
            Document doc = Jsoup.parse(xml, "UTF-8", "");
            Elements infos = doc.select("info");
            for(Element info:infos){
                if(info.select("dUrl").text().equals(url)){
                    isStar = true;
                    break;
                }
            }
        }
        catch (IOException e) {
            Log.e("jsoup error","ioexception");
        }

        return isStar;
    }

    //删除一个收藏
    void unStarInfo(String url){
        Log.i("debug_url",url+".");
        int id=0;

        try {
            File xml = new File(starInfosXmlPath);
            Document doc = Jsoup.parse(xml, "UTF-8", "");
            Elements infos = doc.select("info");
            for(Element info:infos){
                Log.i("debug_infourl",info.select("dUrl").text()+".");
                if(info.select("dUrl").text().equals(url))
                    break;
                id++;
            }
        }
        catch (IOException e) {
            Log.e("jsoup error","ioexception");
        }

        String starInfoXml = readFileToString(starInfosXmlPath);
        String[] lines = starInfoXml.split("\n");
        lines[1+id] = "";

        String newXml = "";
        for(String line:lines){
            if(line.equals(""))
                continue;
            newXml += line + "\n";
        }

        writeStringToFile(starInfosXmlPath,newXml);
    }


    //读取收藏夹
    ArrayList<csElement> readStarInfos(){
        ArrayList<csElement> csElems = new ArrayList<csElement>();

        try {
            File xml = new File(starInfosXmlPath);
            Document doc = Jsoup.parse(xml, "UTF-8", "");
            Elements infos = doc.select("info");
            for(Element info:infos){
                String title = info.select("title").text();

                csElement elem = new csElement();
                elem.checked = true;
                elem.siteName = title;
                csElems.add(elem);
            }
        }
        catch (IOException e) {
            Log.e("jsoup error","ioexception");
        }

        return csElems;
    }
    ArrayList<InfoElement> readStarInfos_detail(){
        ArrayList<InfoElement> infoElems = new ArrayList<InfoElement>();

        try {
            File xml = new File(starInfosXmlPath);
            Document doc = Jsoup.parse(xml, "UTF-8", "");
            Elements infos = doc.select("info");
            for(Element info:infos){
                String title = info.select("title").text();
                String date = info.select("date").text();
                String dUrl = info.select("dUrl").text();
                InfoElement infoElem = new InfoElement();
                infoElem.info = title;
                infoElem.date = date;
                infoElem.dUrl = dUrl;
                infoElems.add(infoElem);
            }
        }
        catch (IOException e) {
            Log.e("jsoup error","ioexception");
        }

        return infoElems;
    }

    //将一个网页添加进收藏
    void addStarInfo(InfoElement elem){
        String starInfoXml = readFileToString(starInfosXmlPath);
        String line = "<info>"+"<title>"+elem.info+"</title>"+"<date>"+elem.date+"</date>"+"<dUrl>"+elem.dUrl+"</dUrl>"+"</info>\n";
        starInfoXml = starInfoXml.substring(0,starInfoXml.length()-6) + line + "</Doc>";

        writeStringToFile(starInfosXmlPath,starInfoXml);
    }

    //删除某一行的收藏
    void removeStarInfo(ArrayList ids){
        String xml = readFileToString(starInfosXmlPath);
        String[] lines = xml.split("\n");
        for(Object id:ids) {
            //Log.i("debug_ index",(int)id+".");
            lines[1 + (int)id] = "";
        }
        String newXml = "";
        for(String line:lines){
            if(!line.equals(""))
                newXml += line + "\n";
        }
        writeStringToFile(starInfosXmlPath,newXml);
    }

    //写入时间限制
    void writeTimeLimit(String timeLimit){
        String xml = readFileToString(aggSettingsXmlPath);
        String[] lines = xml.split("\n");
        lines[3] = "<timeLimit>"+timeLimit+"</timeLimit>";
        String newXml = "";
        for(String line:lines){
            newXml += line + "\n";
        }
        writeStringToFile(aggSettingsXmlPath,newXml);
    }

    //读取时间限制
    String readTimeLimit(){
        try {
            File xml = new File(aggSettingsXmlPath);
            Document doc = Jsoup.parse(xml, "UTF-8", "");
            Elements timeLimit = doc.select("timeLimit");
            return timeLimit.text();
        }
        catch (IOException e) {
            Log.e("jsoup error","ioexception");
            return "";
        }
    }
}
