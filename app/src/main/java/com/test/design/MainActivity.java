package com.test.design;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.test.R;
import com.test.adapter.myPagerAdapter;
import com.test.model.QuestionsList;
import com.test.util.Constant;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ViewPager vpTest;
    private QuestionsList data;
    private myPagerAdapter myPagerAdapter;
    private Button btnNext, btnPrevious;
    private ProgressBar progressBar;
    private HashMap<String, String> submit = new HashMap<>();
    private boolean isReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vpTest = (ViewPager) findViewById(R.id.vpTest);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnPrevious = (Button) findViewById(R.id.btnPrevious);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        final SharedPreferences pref = getApplicationContext().getSharedPreferences(Constant.PREF, 0);
        Bundle extra = getIntent().getExtras();

        if (extra != null) {

            String getData = extra.getString(Constant.FROM_DASHBOARD, "");

            if (getData.equals(Constant.TAKE_A_TEST)) {

                String json = null;
                try {
                    isReview = false;
                    InputStream is = this.getAssets().open("test.json");
                    int size = is.available();
                    byte[] buffer = new byte[size];
                    is.read(buffer);
                    is.close();
                    json = new String(buffer, "UTF-8");

                    data = new QuestionsList();
                    Gson gson = new Gson();
                    data = gson.fromJson(json, QuestionsList.class);

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (getData.equals(Constant.REVIEW)) {
                isReview = true;
                Gson gson = new Gson();
                String json = pref.getString(Constant.QUESTION_LIST, "");
                Log.d("QUESTIONLIST",json);
                data = gson.fromJson(json, QuestionsList.class);
            }
        }

        myPagerAdapter = new myPagerAdapter(this, isReview);
        myPagerAdapter.setData(data);
        vpTest.setAdapter(myPagerAdapter);
        vpTest.beginFakeDrag();

        vpTest.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                if (vpTest.getCurrentItem() == 0) {
                    btnPrevious.setVisibility(View.GONE);
                } else {
                    btnPrevious.setVisibility(View.VISIBLE);
                }
                if (vpTest.getCurrentItem() + 1 == myPagerAdapter.getCount()) {
                    btnNext.setText("SUBMIT");
                    btnNext.setTag("SUBMIT");
                }

                Log.d("PAGE_CHANGED", String.valueOf(vpTest.getCurrentItem()));
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnNext.getTag().equals("SUBMIT")) {
                    progressBar.setVisibility(View.VISIBLE);
                    btnPrevious.setVisibility(View.GONE);
//                    submit = myPagerAdapter.onClick();
                    Log.d("FINAL_ANSWER", submit.toString());
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms

                            SharedPreferences.Editor prefsEditor = pref.edit();
                            Gson gson = new Gson();
                            String json = gson.toJson(data);
                            prefsEditor.putString(Constant.QUESTION_LIST, json);
                            prefsEditor.apply();

                            btnNext.setBackgroundColor(Color.GREEN);
                            btnNext.setTextColor(Color.BLACK);
                            btnNext.setText("Submitted");
                            btnNext.setTag("Submitted");
                            progressBar.setVisibility(View.GONE);
                        }
                    }, 3000);
                }

                if (vpTest.getCurrentItem() < vpTest.getAdapter().getCount()) {
                    vpTest.setCurrentItem(vpTest.getCurrentItem() + 1);
                }
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vpTest.getCurrentItem() != 0) {
                    vpTest.setCurrentItem(vpTest.getCurrentItem() - 1);
                }
            }
        });
    }
}
