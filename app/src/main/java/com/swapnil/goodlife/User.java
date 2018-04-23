package com.swapnil.goodlife;

import android.content.Context;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class User {
    private int longestStreak;
    private int efficiency;
    private int loss;
    private Date timeOfInstall;
    private Date timeOfLastFap;
    private Date timeOfLastUpdate;
    private String fileName = "challenge.txt";

    public User(Context context) throws Exception {

        File appFile = new File(context.getFilesDir(), this.fileName);
        if(!appFile.exists()) {
            this.longestStreak = 0;
            this.efficiency = 0;
            this.loss = 0;
            this.timeOfLastFap = new Date();
            this.timeOfInstall = new Date();
            this.timeOfLastUpdate = new Date();

            writeInFile(context);
            System.out.println("wif");
        } else {
            readFromFile(context);
            System.out.println("rff");
        }
    }

    public void update() {
        System.out.println("update");
        Date currentTime = new Date();
        Calendar calendar = Calendar.getInstance();

        long duration = currentTime.getTime() - this.timeOfLastUpdate.getTime();

        if(duration > 0) {
            //Debug Code
            long durationInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
            if(durationInMinutes > 0) {
                this.efficiency += durationInMinutes;

                calendar.setTime(this.timeOfLastUpdate);
                calendar.add(Calendar.MINUTE, (int) durationInMinutes);
                this.timeOfLastUpdate = calendar.getTime();
            }
            //Debug Code

        }
            /*
            long durationInDays = TimeUnit.MILLISECONDS.toDays(duration);
            if(durationInDays > 0) {
                this.efficiency += durationInDays;

                calendar.setTime(this.timeOfLastUpdate);
                calendar.add(Calendar.DATE, (int) durationInDays);
                this.timeOfLastUpdate = calendar.getTime();
            }
            */
    }

    public void updateAfterFap() {
        System.out.println("update-fap");

        //Updating longestStreak
        int currentStreak = getCurrentStreak();
        this.longestStreak = Math.max(currentStreak, this.longestStreak);

        //Updating loss & efficiency
        int prevEfficiency = this.efficiency;

        //Debug Code
        if(currentStreak >= 3) {
            this.efficiency = Math.max(this.efficiency - 1, 0);
        } else {
            this.efficiency = Math.max(this.efficiency - (3 - currentStreak), 0);
        }
        //Debug Code

        /*
        if(currentStreak >= 7) {
            this.efficiency = Math.max(this.efficiency - 1, 0);
        } else {
            this.efficiency = Math.max(this.efficiency - (7 - currentStreak), 0);
        }
        */

        this.loss = prevEfficiency - this.efficiency;

        //Updating timeOfLastFap
        this.timeOfLastFap = new Date();

        //Updating timeofLastUpdate
        this.timeOfLastUpdate = this.timeOfLastFap;
    }

    public int getCurrentStreak() {
        Date currentTime = new Date();
        long duration = currentTime.getTime() - this.timeOfLastFap.getTime();

        //Debug Code
        long durationInMins = TimeUnit.MILLISECONDS.toMinutes(duration);
        return (int)durationInMins;
        //Debug Code

        /*
        long durationInDays = TimeUnit.MILLISECONDS.toDays(duration);
        return (int)durationInDays;
        */
    }

    public int daysPassedSinceStart() {
        Date currentTime = new Date();
        long duration = currentTime.getTime() - this.timeOfInstall.getTime();

        //Debug Code
        long durationInMins = TimeUnit.MILLISECONDS.toMinutes(duration);
        return (int)durationInMins;
        //Debug Code

        /*
        long durationInDays = TimeUnit.MILLISECONDS.toDays(duration);
        return (int)durationInDays;
        */
    }

    public Date getTimeOfLastFap() { return this.timeOfLastFap; }

    public int getEfficiency() { return this.efficiency; }

    public int getLoss() { return this.loss; }

    public int getLongestStreak() { return this.longestStreak = Math.max(longestStreak, getCurrentStreak()); }

    public Date getTimeOfLastUpdate() { return this.timeOfLastUpdate; }

    public void onShutdown(Context context) throws Exception {
        writeInFile(context);
        System.out.println("wif-destroy");
    }

    public void writeInFile(Context context) throws Exception {
        FileOutputStream fileout = context.openFileOutput(this.fileName, Context.MODE_PRIVATE);
        OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
        BufferedWriter bWriter = new BufferedWriter(outputWriter);

        bWriter.write(this.longestStreak + "\n");
        bWriter.write(this.efficiency + "\n");
        bWriter.write(this.loss + "\n");

        SimpleDateFormat dateFormatter = new SimpleDateFormat("y:M:d H:mm:ss a");
        bWriter.write(dateFormatter.format(this.timeOfInstall).toString() + "\n");
        bWriter.write(dateFormatter.format(this.timeOfLastFap).toString() + "\n");
        bWriter.write(dateFormatter.format(this.timeOfLastUpdate).toString() + "\n");

        bWriter.close();
        outputWriter.close();
        fileout.close();
    }

    public void readFromFile(Context context) throws Exception {
        InputStreamReader ir = new InputStreamReader(context.openFileInput(this.fileName));
        BufferedReader br = new BufferedReader(ir);

        String line;
        line = br.readLine();
        this.longestStreak = Integer.parseInt(line);

        line = br.readLine();
        this.efficiency = Integer.parseInt(line);

        line = br.readLine();
        this.loss = Integer.parseInt(line);

        SimpleDateFormat dateFormatter = new SimpleDateFormat("y:M:d H:mm:ss a");

        line = br.readLine();
        this.timeOfInstall = dateFormatter.parse(line);

        line = br.readLine();
        this.timeOfLastFap = dateFormatter.parse(line);

        line = br.readLine();
        this.timeOfLastUpdate = dateFormatter.parse(line);

        br.close();
        ir.close();
    }
}
