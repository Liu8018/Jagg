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
            String jaggRootPath = Environment.getExternalStorageDirectory() + File.separator + "Jagg";
            File sitesXml = new File(jaggRootPath+"/sites.xml");
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
}
