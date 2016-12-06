package com.jy.datewheel;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.jy.datewheel.util.DateUtils;
import com.jy.datewheel.vo.DateItemVO;
import com.jy.datewheel.widget.DateWheel;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    DateWheel mDateWheel;
    private ArrayList<DateItemVO> mDateItemVOs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mDateWheel = (DateWheel) findViewById(R.id.twDate);

        final TextView tvValue = (TextView) findViewById(R.id.tvValue);

        mDateWheel.setValueChangeListener(new DateWheel.OnValueChangeListener() {
            @Override
            public void onValueChange(float value) {
                if((int) value < mDateItemVOs.size() && value >= 0) {
                    tvValue.setText(mDateItemVOs.get((int) value).showToast);
                }
            }
        });

        mDateItemVOs = DateUtils.getHalfYearDates();

        mDateWheel.initViewParam(179, mDateItemVOs.size() - 1, DateWheel.MOD_TYPE_ONE, mDateItemVOs);
    }

    @Override
    protected void onDestroy() {
        mDateWheel.recycle();
        super.onDestroy();
    }


}
