package com.swapnil.goodlife;

import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;

import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private User user;
    private TextView V_eff;
    private TextView V_eff_big;
    private TextView V_lstreak;
    private TextView V_loss;
    private TextView V_cstreak;
    private TextView V_challenge_time;
    private TextView V_timer;
    private TextView V_timer_big;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            user = new User(getApplicationContext());
        } catch (Exception e) {
            System.out.println("user error");
            e.printStackTrace();
        }

        V_eff = (TextView) findViewById(R.id.eff);
        V_eff_big = (TextView) findViewById(R.id.eff_big);
        V_lstreak = (TextView) findViewById(R.id.lstreak);
        V_loss = (TextView) findViewById(R.id.loss);
        V_cstreak = (TextView) findViewById(R.id.cstreak);
        V_challenge_time = (TextView) findViewById(R.id.challenge_time);
        V_timer = (TextView) findViewById(R.id.timer);
        V_timer_big = (TextView) findViewById(R.id.timer_big);

    }

    private void playWithCountDownTimer(Date timeOfTimer) {
        Date currentTime = new Date();
        long millis = currentTime.getTime() - timeOfTimer.getTime();
        millis = (24 * 60 * 60 * 1000) - millis;

        new CountDownTimer(millis, 1000) {
            public void onTick(long millisUntilFinished) {
                long hrs = millisUntilFinished / (60*60*1000);
                long mins = (millisUntilFinished / (60 * 1000)) % 60;
                long secs = (millisUntilFinished / (1000)) % 60;

                V_timer.setText("24/7 Timer: "+ hrs + ":" + mins + ":" + secs);
                V_timer_big.setText(hrs + ":" + mins + ":" + secs);
            }

            public void onFinish() {
               playWithCountDownTimer(user.getTimeOfLastUpdate());
            }
        }.start();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        user.update();
        setValuesInApp();

        //playWithCountDownTimer(user.getTimeOfLastUpdate());
    }

    @Override
    protected void onStart() {
        super.onStart();

        user.update();
        setValuesInApp();
        playWithCountDownTimer(user.getTimeOfLastUpdate());
    }

    private void setValuesInApp() {
        V_eff.setText("Your Efficiency: " + user.getEfficiency() + "%");
        V_eff_big.setText(user.getEfficiency() + "%");
        V_lstreak.setText("Longest Streak: " + user.getLongestStreak());
        V_challenge_time.setText("Total Days Passed: " + user.daysPassedSinceStart());
        V_loss.setText("Previous Loss in Efficiency: " + user.getLoss() + "%");
        V_cstreak.setText("Current Streak: " + user.getCurrentStreak());
    }

    public void onClickingBustedButton(View view) {
        user.updateAfterFap();
        setValuesInApp();


        //playWithCountDownTimer(user.getTimeOfLastUpdate());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            user.onShutdown(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
