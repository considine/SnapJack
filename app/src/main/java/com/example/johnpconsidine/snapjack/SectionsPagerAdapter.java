package com.example.johnpconsidine.snapjack;

import android.app.FragmentManager;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.Locale;

/**
 * Created by johnpconsidine on 10/31/15.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {


    protected Context mContext;
    public SectionsPagerAdapter(Context context, android.support.v4.app.FragmentManager fragmentManager) {
        super(fragmentManager);
        mContext = context;
    }
    @Override
    public Fragment getItem(int position) {
        return null;
    }

    @Override
    public int getCount() {
        return 0;
    }


}
