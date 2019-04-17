package com.test.design;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.test.R;
import com.test.util.Constant;

public class DashBoard extends AppCompatActivity {

    private TextView tvTakeATest;
    private TextView tvReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        tvTakeATest = (TextView) findViewById(R.id.tvTakeATest);
        tvReview = (TextView) findViewById(R.id.tvReview);

        tvTakeATest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashBoard.this,MainActivity.class);
                intent.putExtra(Constant.FROM_DASHBOARD,Constant.TAKE_A_TEST);
                startActivity(intent);
            }
        });

        tvReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashBoard.this,MainActivity.class);
                intent.putExtra(Constant.FROM_DASHBOARD,Constant.REVIEW);
                startActivity(intent);
            }
        });
    }
}