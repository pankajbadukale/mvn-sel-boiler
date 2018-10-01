package com.mvnboiler.app;

import com.mvnboiler.selenium.Frontend;
import com.mvnboiler.selenium.TestRecorder;

/**
 * Hello world!
 *
 */
public class App 
{
    public static Frontend ui;
    public static TestRecorder recorder;

    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        ui = Frontend.getInstance();
        recorder = TestRecorder.getInstance();

        try {
            recorder.startRecording();
            ui.open("https://paytm.com/");
            recorder.stopRecording();
            Thread.sleep(5000);
            ui.printLog();
            ui.close();
        } catch (Exception e) {
			e.printStackTrace();	
        }
    }
}
