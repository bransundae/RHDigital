package com.example.rhdigital.activities.auth;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rhdigital.R;
import com.example.rhdigital.ui.adapters.SectionsStatePagerAdapter;
import com.example.rhdigital.activities.auth.fragments.SignInFragment;
import com.example.rhdigital.activities.auth.fragments.SignUpFragment;
import com.example.rhdigital.ui.view.CustomViewPager;

public class AuthActivity extends AppCompatActivity {

    //Components
    CustomViewPager mCustomViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mCustomViewPager = findViewById(R.id.container_auth);
        setUpViewPager(mCustomViewPager);
    }

    private void setUpViewPager(CustomViewPager customViewPager){
        SectionsStatePagerAdapter sectionsStatePagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        sectionsStatePagerAdapter.addFragment(new SignInFragment());
        sectionsStatePagerAdapter.addFragment(new SignUpFragment());
        customViewPager.setAdapter(sectionsStatePagerAdapter);
    }

    public int getViewPager(){
        return mCustomViewPager.getCurrentItem();
    }

    public void setViewPager(int position){
        mCustomViewPager.setCurrentItem(position);
    }
}
