package com.plaincalculator.android;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.udojava.evalex.Expression;

import java.math.BigDecimal;
import java.util.EmptyStackException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends Activity {
    private enum KeyPadType {
        Data, InsertableOperation
    }

    private Vibrator vibrator;
    private KeyPadType lastKeyPadType;
    private BigDecimal result;
    private boolean isOnProgress;

    @BindView(R.id.expression_view) TextView expressionView;
    @BindView(R.id.result_view) TextView resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @OnClick({R.id.one_button, R.id.two_button, R.id.three_button, R.id.four_button,
            R.id.five_button, R.id.six_button, R.id.seven_button, R.id.eight_button,
            R.id.nine_button, R.id.zero_button, R.id.two_zero_button, R.id.point_button})
    void dataButtonPressed(View button) {
        insertExpression(button.getTag().toString());
        lastKeyPadType = KeyPadType.Data;
        vibrator.vibrate(20);
    }

    @OnClick({R.id.clear_button, R.id.exp_button, R.id.div_button, R.id.mul_button,
            R.id.sub_button, R.id.add_button, R.id.equal_button})
    void operationButtonPressed(View button){
        if (button.getId() == R.id.clear_button) {
            clear();
        } else if (button.getId() == R.id.equal_button) {
            showResult();
        } else {
            if (lastKeyPadType != null && lastKeyPadType != KeyPadType.InsertableOperation) {
                String operation = button.getTag().toString();
                if (!isOnProgress) {
                    setExpression(result.toPlainString());
                    isOnProgress = true;
                }
                insertExpression(operation);
                lastKeyPadType = KeyPadType.InsertableOperation;
            }
        }
        vibrator.vibrate(40);
    }

    private void setExpression(String text) {
        expressionView.setText(text);
    }

    private void insertExpression(String data) {
        expressionView.append(data);
        isOnProgress = true;
    }

    private void showResult() {
        if (expressionView.getTextSize() == 0) {
            return;
        }
        try {
            Expression expression = new Expression(expressionView.getText().toString());
            result = expression.setPrecision(16).eval();
            if (result.toPlainString().length() < 16) {
                resultView.setText(result.toPlainString());
            } else {
                result = expression.setPrecision(4).eval();
                resultView.setText(String.valueOf(result));
            }
            isOnProgress = false;
        } catch (EmptyStackException e) {
            Log.e(MainActivity.class.toString(), e.toString());
        } catch (ArithmeticException e) {
            resultView.setText("---");
        }
    }

    private void clear() {
        expressionView.setText(null);
        resultView.setText(null);
        lastKeyPadType = null;
    }
}
