package com.example.jagg;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

public class WebActivity_dview extends AppCompatActivity {

    FileTool fileTool = new FileTool();

    //显示网页用的
    private WebView webView;
    private ProgressBar webProgressBar;

    //网站链接
    String dUrl;

    //网站名
    String siteName;

    MenuItem starItem;

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

            dUrl = url;

            if(fileTool.isStarred(url)){
                starItem.setIcon(R.drawable.star_set);
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {//页面开始加载
            webProgressBar.setVisibility(view.VISIBLE);

            //Log.i("debug_ url start",url);
            dUrl = url;
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
        inflater.inflate(R.menu.web_menu_dview, menu);

        starItem = menu.findItem(R.id.web_menu_addStar);

        return super.onCreateOptionsMenu(menu);
    }



    //menu按钮响应
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.web_menu_addStar) {

            if(!fileTool.isStarred(dUrl)) {
                InfoElement elem = new InfoElement();
                elem.info = siteName;
                elem.date = "";
                elem.dUrl = dUrl;
                fileTool.addStarInfo(elem);

                Toast.makeText(WebActivity_dview.this, "已加入收藏", Toast.LENGTH_SHORT).show();

                item.setIcon(R.drawable.star_set);
            } else{
                fileTool.unStarInfo(dUrl);

                Toast.makeText(WebActivity_dview.this, "已取消收藏", Toast.LENGTH_SHORT).show();

                item.setIcon(R.drawable.star_unset);
            }
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
