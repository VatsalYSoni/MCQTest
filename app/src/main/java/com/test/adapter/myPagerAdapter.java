package com.test.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.test.R;
import com.test.model.QuestionsList;

import java.util.ArrayList;
import java.util.HashMap;

public class myPagerAdapter extends PagerAdapter {

    private Context context;
    private QuestionsList data;
    private TextView tvQuestion;
    private LinearLayout llOptions;
    private HashMap<String, String> submit = new HashMap<>();
    private int selected;
    private boolean isReview;

    public myPagerAdapter(Context context, boolean isReview) {
        this.context = context;
        this.isReview = isReview;
    }

    public void setData(QuestionsList data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.getQuestions().size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_view_pager, container, false);
        tvQuestion = view.findViewById(R.id.tvQuestion);
        llOptions = view.findViewById(R.id.llOptions);

        Log.d("Position", String.valueOf(position));
        tvQuestion.setText(data.getQuestions().get(position).getId() + "\n" + data.getQuestions().get(position).getQuestion());
        addRadioButtons(data.getQuestions().get(position).getOptions().size(), position);
        Log.d("TAG", "METHOD CALL");
        container.addView(view);
        return view;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    private void addRadioButtons(int number, final int position) {
        RadioGroup ll = new RadioGroup(context);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setPadding(16, 8, 16, 8);
        final ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            final RadioButton rdbtn = new RadioButton(context);
            rdbtn.setPadding(16, 8, 16, 8);
            rdbtn.setId(View.generateViewId());
            rdbtn.setText(data.getQuestions().get(position).getOptions().get(i));

            if (data.getQuestions().get(position).getAnswer() != null) {
                if (data.getQuestions().get(position).getOptions().get(i).equalsIgnoreCase(data.getQuestions().get(position).getAnswer())) {
                    rdbtn.setChecked(true);
                }
            }
            if (isReview) {
                ll.setClickable(false);
                rdbtn.setClickable(false);
                rdbtn.setEnabled(false);
                if (data.getQuestions().get(position).getTrueAnswer().equals(data.getQuestions().get(position).getOptions().get(i))) {
                    rdbtn.setTextColor(Color.GREEN);
                }
            }

            rdbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (rdbtn.isChecked()) {
                        arrayList.add(rdbtn.getText().toString());
                        data.getQuestions().get(position).setAnswer(rdbtn.getText().toString());
                    } else if (arrayList.contains(rdbtn.getText().toString())) {
                        arrayList.remove(rdbtn.getText().toString());
                    }
//                    addAnswer(data.getQuestions().get(position).getId(), arrayList);
                    data.getQuestions().get(position).setAnswer(arrayList.get(0));
                }
            });
            Log.d("CHECKED", submit.keySet().toString());
            ll.addView(rdbtn);
        }
        llOptions.addView(ll);
    }
}
