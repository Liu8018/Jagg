package com.example.jagg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridLayout grid = (GridLayout) findViewById(R.id.mainPage_layout);
        int childCount = grid.getChildCount();

        for (int i= 0; i < childCount; i++){
            final RelativeLayout container = (RelativeLayout) grid.getChildAt(i);
            container.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view){
                    TextView textView = (TextView) container.getChildAt(1);
                    //Log.i("siteName",textView.getContentDescription().toString());

                    Intent intent = new Intent(MainActivity.this, InfoActivity.class);
                    intent.putExtra("siteName",textView.getContentDescription().toString());
                    startActivity(intent);
                }
            });
        }

    }
}
