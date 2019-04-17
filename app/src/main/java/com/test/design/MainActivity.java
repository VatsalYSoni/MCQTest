package com.test.design;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

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
    private Button btnNext, btnPrevious, btnSubmit;
    private ProgressBar progressBar;
    private HashMap<String, String> submit = new HashMap<>();
    private boolean isReview;
    private int trueAnswer = 0;
    private TextView tvTimer;
    CountDownTimer cTimer = null;
    private SharedPreferences pref;
    private String getData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vpTest = (ViewPager) findViewById(R.id.vpTest);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnPrevious = (Button) findViewById(R.id.btnPrevious);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tvTimer = (TextView) findViewById(R.id.tvTimer);

        pref = getApplicationContext().getSharedPreferences(Constant.PREF, 0);
        Bundle extra = getIntent().getExtras();

        if (extra != null) {

            getData = extra.getString(Constant.FROM_DASHBOARD, "");

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
                startTimer();
            } else if (getData.equals(Constant.REVIEW)) {
                isReview = true;
                Gson gson = new Gson();
                String json = pref.getString(Constant.QUESTION_LIST, "");
                Log.d("QUESTIONLIST", json);
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
//                    btnNext.setText("SUBMIT");
//                    btnNext.setTag("SUBMIT");
                    if (getData.equals(Constant.TAKE_A_TEST)) {
                        btnNext.setVisibility(View.GONE);
                        btnSubmit.setVisibility(View.VISIBLE);
                    }else {
                        btnNext.setVisibility(View.GONE);
                    }
                } else {
                    btnNext.setVisibility(View.VISIBLE);
                    btnSubmit.setVisibility(View.GONE);
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
//                if (btnNext.getTag().equals("SUBMIT")) {
//                    progressBar.setVisibility(View.VISIBLE);
//                    btnPrevious.setVisibility(View.GONE);
//                    submit = myPagerAdapter.onClick();
//                    Log.d("FINAL_ANSWER", submit.toString());
//                    final Handler handler = new Handler();
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            // Do something after 5s = 5000ms
//
//                            SharedPreferences.Editor prefsEditor = pref.edit();
//                            Gson gson = new Gson();
//                            String json = gson.toJson(data);
//                            prefsEditor.putString(Constant.QUESTION_LIST, json);
//                            prefsEditor.apply();
//
//                            btnNext.setBackgroundColor(Color.GREEN);
//                            btnNext.setTextColor(Color.BLACK);
//                            btnNext.setText("Submitted");
//                            btnNext.setTag("Submitted");
//                            progressBar.setVisibility(View.GONE);
//                        }
//                    }, 3000);
//                }

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
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    private void showDialog() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_submit_test);
        TextView textView = dialog.findViewById(R.id.tvResult);
        Button btnOk = dialog.findViewById(R.id.btnOk);
        textView.setText(trueAnswer + " / " + data.getQuestions().size());
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        if (!isFinishing())
            dialog.show();
    }

    void startTimer() {
        cTimer = new CountDownTimer(15000, 1000) {
            @SuppressLint("SimpleDateFormat")
            public void onTick(long millisUntilFinished) {

                long minutes = (millisUntilFinished / 1000) / 60;
                long seconds = (millisUntilFinished / 1000) % 60;
                tvTimer.setText(minutes + ":" + seconds);
            }

            public void onFinish() {
                submit();
            }
        };
        cTimer.start();
    }


    //cancel timer
    void cancelTimer() {
        if (cTimer != null)
            cTimer.cancel();
    }

    private void submit() {
        cancelTimer();
        trueAnswer = 0;
        SharedPreferences.Editor prefsEditor = pref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(data);
        prefsEditor.putString(Constant.QUESTION_LIST, json);
        prefsEditor.apply();

        for (int i = 0; i < data.getQuestions().size(); i++) {
            if (data.getQuestions().get(i).getTrueAnswer().equals(data.getQuestions().get(i).getAnswer())) {
                trueAnswer++;
            }
        }
        showDialog();
    }
}
