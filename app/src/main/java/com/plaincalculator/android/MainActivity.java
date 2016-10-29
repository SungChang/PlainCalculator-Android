package com.plaincalculator.android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.one_button, R.id.two_button, R.id.three_button, R.id.four_button,
            R.id.five_button, R.id.six_button, R.id.seven_button, R.id.eight_button,
            R.id.nine_button, R.id.zero_button, R.id.two_zero_button, R.id.point_button})
    void dataButtonPressed(View button) {
        Log.e("Test", "" + button.getTag());
    }

    @OnClick({R.id.clear_button, R.id.exp_button, R.id.div_button, R.id.mul_button,
            R.id.sub_button, R.id.add_button, R.id.equal_button})
    void operationButtonPressed(View view) {

    }
}
