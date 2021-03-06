package com.kinitoapps.ngolink;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by HP INDIA on 08-Apr-17.
 */

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {
        private FirebaseAuth mAuth;
        private FirebaseAuth.AuthStateListener mAuthStateListener;
        private boolean isLoggedIn;
    private Context mContext;
    public SimpleFragmentPagerAdapter(Context context , FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return mContext.getString(com.kinitoapps.ngolink.R.string.tab_one);
        } else if (position == 1) {
            return mContext.getString(com.kinitoapps.ngolink.R.string.tab_two);
        } else  {
            return mContext.getString(com.kinitoapps.ngolink.R.string.tab_three);
        }
    }


    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            Log.v("Running", "tab one");
            return new FragmentOne();
        } else if (position == 1) {
            Log.v("Running", "tab two");
            return new FragmentTwo();
        } else {
            mAuth = FirebaseAuth.getInstance();
            if (mAuth.getCurrentUser() == null) {
                return new FragmentThree();
            } else {
                return new FragmentThreeProfile();

            }




        }
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return 3;
    }


}
