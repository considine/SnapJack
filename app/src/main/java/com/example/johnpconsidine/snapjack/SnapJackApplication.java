package com.example.johnpconsidine.snapjack;

import android.app.Application;
import android.support.v4.view.ViewPager;

import com.parse.Parse;

/**
 * Created by johnpconsidine on 10/30/15.
 */
public class SnapJackApplication extends Application {
    // Enable Local Datastore.


    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "XdwexisK32BLUvGR6aLDRb3DInrs6Uanmju5gcSI", "i7nKvZCpzcn3hgiTCHsKehOijNCADSjf0sSmHpgb");



    }

}
