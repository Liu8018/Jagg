package com.example.jagg;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.ArrayList;

public class WebActivity extends AppCompatActivity {

    FileTool fileTool = new FileTool();

    //显示网页用的
    private WebView webView;
    private ProgressBar webProgressBar;

    //网站链接
    String dUrl;

    //网站名
    String siteName;

    String keyWords;
    int npages;

    ArrayList<InfoElement> infoElems = new ArrayList<InfoElement>();

    private ProgressDialog processDialog;
    private Handler handler =new Handler(){
        @Override
        //当有消息发送出来的时候就执行Handler的这个方法
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            //只要执行到这里就关闭对话框
            processDialog.dismiss();

            //检查是否加载成功
            if(infoElems.size() == 1){
                InfoElement elem = infoElems.get(0);
                if(elem.info.equals("-1") && elem.date.equals("-1") && elem.dUrl.equals("-1")){
                    Toast.makeText(WebActivity.this, "加载失败，请重试",Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            ArrayList<String> infoTitles = new ArrayList<String>();
            ArrayList<String> infoDates = new ArrayList<String>();
            ArrayList<String> infoUrls = new ArrayList<String>();

            for(InfoElement elem:infoElems){
                infoTitles.add(elem.info);
                infoDates.add(elem.date);
                infoUrls.add(elem.dUrl);
            }

            //调用infoActivity
            Intent intent = new Intent(WebActivity.this, InfoActivity.class);
            intent.putExtra("siteName",siteName);
            intent.putExtra("siteUrl",dUrl);
            intent.putExtra("keyWords",keyWords);
            intent.putExtra("npages",npages);
            intent.putStringArrayListExtra("infoTitles",infoTitles);
            intent.putStringArrayListExtra("infoDates",infoDates);
            intent.putStringArrayListExtra("infoUrls",infoUrls);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置主界面
        setContentView(R.layout.activity_web);

        //actionbar上的返回键
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //进度条
        webProgressBar = (ProgressBar)findViewById(R.id.webProgressBar);

        webView = (WebView)findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().getJavaScriptEnabled();
        webView.setWebViewClient(new WebViewClient() {
           @Override
           public boolean shouldOverrideUrlLoading(WebView view, String url) {
               view.loadUrl(url);
               return true;
           }
        });

        //接收来自infoActivity的信息
        Intent intent = getIntent();
        dUrl = intent.getStringExtra("siteUrl");
        siteName = intent.getStringExtra("siteName");
        actionBar.setTitle(siteName);
        //actionBar.setSubtitle("Sub Title");
        webView.loadUrl(dUrl);

        webView.setWebChromeClient(webChromeClient);
        webView.setWebViewClient(webViewClient);

    }

    //WebViewClient主要帮助WebView处理各种通知、请求事件
    private WebViewClient webViewClient=new WebViewClient(){
        @Override
        public void onPageFinished(WebView view, String url) {//页面加载完成
            webProgressBar.setVisibility(view.GONE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {//页面开始加载
            webProgressBar.setVisibility(view.VISIBLE);
        }

    };

    //WebChromeClient主要辅助WebView处理Javascript的对话框、网站图标、网站title、加载进度等
    private WebChromeClient webChromeClient=new WebChromeClient(){
        //不支持js的alert弹窗，需要自己监听然后通过dialog弹窗
        @Override
        public boolean onJsAlert(WebView webView, String url, String message, JsResult result) {
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(webView.getContext());
            localBuilder.setMessage(message).setPositiveButton("确定",null);
            localBuilder.setCancelable(false);
            localBuilder.create().show();

            //注意:
            //必须要这一句代码:result.confirm()表示:
            //处理结果为确定状态同时唤醒WebCore线程
            //否则不能继续点击按钮
            result.confirm();
            return true;
        }

        //获取网页标题
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            //Log.i("ansen","网页标题:"+title);
        }

        //加载进度回调
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            webProgressBar.setProgress(newProgress);
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Log.i("ansen","是否有上一个页面:"+webView.canGoBack());
        if (webView.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK){//点击返回按钮的时候判断有没有上一页
            webView.goBack(); // goBack()表示返回webView的上一页面
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }

    //调用menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.web_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    //menu按钮响应
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.web_menu_search) {
            //Toast.makeText(WebActivity.this, "You clicked item", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(WebActivity.this);
            builder.setTitle("请输入搜索关键词");
            final EditText et = new EditText(this);
            builder.setView(et);
            builder.setPositiveButton("确认" ,  new DialogInterface.OnClickListener() {
                //确定按钮的响应事件
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Toast.makeText(WebActivity.this, dUrl+"\n"+et.getText().toString(), Toast.LENGTH_SHORT).show();

                    //构建一个等待界面
                    processDialog= ProgressDialog.show(WebActivity.this, "", "正在搜索…");
                    new Thread(){
                        public void run(){
                            //在这里执行长耗时方法
                            keyWords = et.getText().toString();
                            WebTool webTool = new WebTool();
                            infoElems = webTool.crawlInfoList(dUrl,keyWords,0);
                            npages = webTool.npages;

                            //执行完毕后给handler发送一个消息
                            handler.sendEmptyMessage(0);
                        }
                    }.start();
                }
            });

            builder.setNegativeButton("返回", null);
            builder.show();
        }

        return super.onOptionsItemSelected(item);
    }

    //返回键响应
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
