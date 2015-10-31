package com.example.johnpconsidine.snapjack;

import android.app.Application;
import android.widget.Button;
import android.widget.EditText;

import com.parse.Parse;
import com.parse.ParseObject;

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
