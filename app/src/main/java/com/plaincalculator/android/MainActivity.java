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
        Data, InsertableOperation, Point
    }

    private Vibrator vibrator;
    private BigDecimal result;
    private boolean isEquationChanged;

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
        if (button.getId() == R.id.point_button && !canPutPoint()) {
            return;
        }
        insertExpression(button.getTag().toString());
        vibrator.vibrate(20);
    }

    @OnClick({R.id.clear_button, R.id.exp_button, R.id.div_button, R.id.mul_button,
            R.id.sub_button, R.id.add_button, R.id.equal_button})
    void operationButtonPressed(View button){
        if (button.getId() == R.id.clear_button) {
            clear();
        } else if (button.getId() == R.id.equal_button) {
            showResult();
        } else if (getLastKeyPadType() == KeyPadType.Point) {
            return;
        } else {
            KeyPadType lastKeyPadType = getLastKeyPadType();
            if (lastKeyPadType != null && lastKeyPadType != KeyPadType.InsertableOperation) {
                if (!isEquationChanged) {
                    setExpression(result.toPlainString());
                }
                String operation = button.getTag().toString();
                insertExpression(operation);
            }
        }
        vibrator.vibrate(40);
    }

    @OnClick(R.id.back_button)
    void backButtonPressed() {
        if (expressionView.length() > 0) {
            String currentEquation = expressionView.getText().toString();
            String newEquation = currentEquation.substring(0, currentEquation.length() - 1);
            expressionView.setText(newEquation);
            isEquationChanged = true;
        }
        vibrator.vibrate(40);
    }
    private KeyPadType getLastKeyPadType() {
        if (expressionView.length() == 0) {
            return null;
        }
        char lastChar = expressionView.getText().charAt(expressionView.length() - 1);
        return getKeyPadType(lastChar);
    }

    private KeyPadType getKeyPadType(char c) {
        switch (c) {
            case '+':
            case '-':
            case '*':
            case '/':
            case '^':
                return KeyPadType.InsertableOperation;
            case '.':
                return KeyPadType.Point;
            default:
                return KeyPadType.Data;
        }
    }

    private void setExpression(String text) {
        expressionView.setText(text);
    }

    private void insertExpression(String data) {
        expressionView.append(data);
        isEquationChanged = true;
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
            isEquationChanged = false;
        } catch (EmptyStackException e) {
            Log.e(MainActivity.class.toString(), e.toString());
        } catch (ArithmeticException e) {
            resultView.setText("---");
        }
    }

    private void clear() {
        expressionView.setText(null);
        resultView.setText(null);
    }

    private boolean canPutPoint() {
        KeyPadType lastKeyPadType = getLastKeyPadType();
        if (lastKeyPadType == null || lastKeyPadType == KeyPadType.InsertableOperation) {
            return false;
        }

        boolean pointAllowed = true;
        String expression = expressionView.getText().toString();
        for (int i = 0; i < expression.length(); i++) {
            KeyPadType currentKeyPadType = getKeyPadType(expression.charAt(i));
            if (currentKeyPadType == KeyPadType.Point) {
                pointAllowed = false;
            } else if (currentKeyPadType == KeyPadType.InsertableOperation) {
                pointAllowed = true;
            }
        }
        return pointAllowed;
    }
}
