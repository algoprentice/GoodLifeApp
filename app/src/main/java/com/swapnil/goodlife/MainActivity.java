package com.swapnil.goodlife;

import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

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
    private CountDownTimer cdTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("On Create");

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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        user.update();
        setValuesInApp();
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("On Start");

        user.update();
        setValuesInApp();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println("On Restart");
    }

    private long getMillis(Date timeCheckPoint) {
        Date currentTime = new Date();
        long millis = currentTime.getTime() - timeCheckPoint.getTime();
        millis = (24 * 60 * 60 * 1000) - millis;
        return millis;
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("On Resume");

        long millis = getMillis(user.getTimeOfLastUpdate());
        this.cdTimer = giveMeCountDownTimer(millis);
        this.cdTimer.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("On Stop");

        this.cdTimer.cancel();
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("On Pause");
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("So, you broke your streak today ?");
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                user.updateAfterFap();
                setValuesInApp();

                cdTimer.cancel();
                long millis = getMillis(user.getTimeOfLastUpdate());
                cdTimer = giveMeCountDownTimer(millis);
                cdTimer.start();

                Toast.makeText(getApplicationContext(), "Resist Temptations More Next Time!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Glad to hear it!", Toast.LENGTH_SHORT).show();
            }
        });
        
        builder.show();
    }

    private CountDownTimer giveMeCountDownTimer(long millis) {
        return new CountDownTimer(millis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long hrs = millisUntilFinished / (60 * 60 * 1000);
                long minutes = (millisUntilFinished / (60 * 1000)) % 60;
                long secs = (millisUntilFinished / (1000)) % 60;

                String msg = hrs + ":" + minutes + ":" + secs;
                String msg24 = "24/7 Timer: " + msg;

                V_timer.setText(msg24);
                V_timer_big.setText(msg);
            }

            @Override
            public void onFinish() {}
        };
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
