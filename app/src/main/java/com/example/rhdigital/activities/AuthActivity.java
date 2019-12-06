package com.example.rhdigital.activities;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rhdigital.R;
import com.example.rhdigital.adapters.SectionsStatePagerAdapter;
import com.example.rhdigital.fragments.SignInFragment;
import com.example.rhdigital.view.CustomViewPager;

public class AuthActivity extends AppCompatActivity {

    //Components
    CustomViewPager mCustomViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCustomViewPager = findViewById(R.id.container_main);
        setUpViewPager(mCustomViewPager);
    }

    private void setUpViewPager(CustomViewPager customViewPager){
        SectionsStatePagerAdapter sectionsStatePagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        sectionsStatePagerAdapter.addFragment("Sign In Fragment", new SignInFragment());
        customViewPager.setAdapter(sectionsStatePagerAdapter);
    }

    public String
}