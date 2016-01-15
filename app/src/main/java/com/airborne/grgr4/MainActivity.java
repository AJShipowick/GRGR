package com.airborne.grgr4;

import java.util.Locale;
import java.util.Random;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements ActionBar.TabListener {

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    //todo implement TabLayout instead of this actionbar stuff...
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity allRepData, which implements
            // the TabListener interface, as the userLocationCallback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                                    //.setIcon()
                            .setTabListener(this));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //No menu/action bar currently...
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        setRandomHeaderImages();



        //setRandomHeaderQuotes();
        return true;
    }

    public void setRandomHeaderImages(){
        Random randomNumber = new Random();
        //setLeftAndRightHeaderImages(randomNumber);
        setMainHeaderImage(randomNumber);
    }
    public void setMainHeaderImage(Random randomNumber){
        String imgMainHeaderConstant = "@drawable/home_header_";
        ImageView mainHeaderImage = (ImageView) findViewById(R.id.imgHeader);

        int i = randomNumber.nextInt(3) + 1;
        String randomImage = Integer.toString(i);
        String imageURI = imgMainHeaderConstant + randomImage ;

        int imageResource = getResources().getIdentifier(imageURI, null, getPackageName());
        Drawable res = getResources().getDrawable(imageResource);
        mainHeaderImage.setImageDrawable(res);
    }

//    public void setLeftAndRightHeaderImages(Random randomNumber){
//        String imgMainHeaderSideConstant = "@drawable/home_header_side_";
//        ImageView leftHeaderImages = (ImageView) findViewById(R.id.imgLeftBorder);
//        //ImageView rightHeaderImages = (ImageView) findViewById(R.id.imgRightBorder);
//
//        int i = randomNumber.nextInt(4) + 1;
//        String randomImage = Integer.toString(i);
//        String imageName = imgMainHeaderSideConstant + randomImage;
//
//        int imageResource = getResources().getIdentifier(imageName, null, getPackageName());
//        Drawable res = getResources().getDrawable(imageResource);
//        leftHeaderImages.setImageDrawable(res);
//        //rightHeaderImages.setImageDrawable(res);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());



    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.

            Fragment fragment = new Fragment();
            switch (position) {
                case 0:
                    fragment = FragmentTheMovement.newInstance(position + 1);
                    break;
                case 1:
                    fragment = FragmentYourRights.newInstance(position + 1);
                    break;
                default:
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.tab1_contactReps).toUpperCase(l);
                case 1:
                    return getString(R.string.tab2_yourRights).toUpperCase(l);
            }
            return null;
        }
    }

    // Button click even handler for btnTakeAction
    public void takeAction(View view) {
        Button btnTakeAction = (Button) findViewById(R.id.btnTakeAction);
        ColorDrawable buttonColor = (ColorDrawable) btnTakeAction.getBackground();

        //Change the button color between Red and Blue each time it is clicked.
        if (buttonColor.getColor() == Color.parseColor("#E0162B")){
            btnTakeAction.setBackgroundColor(Color.parseColor("#0052A5"));
        }else{
            btnTakeAction.setBackgroundColor(Color.parseColor("#E0162B"));
        }

        Intent takeAction = new Intent(getApplicationContext(), ActivityLocalReps.class);
        takeAction.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(takeAction);
    }

    public void whyTakeAction(View view){
        startActivity(new Intent(getApplicationContext(), WhyTakeAction.class));
    }

    public void readDeclarationOfIndependence(View view) {
        startActivity(new Intent(getApplicationContext(), ActivityReadDeclarationOfIndependence.class));
    }

    //http://www.usconstitution.net/const.txt
    public void readArticle1(View view) {
        startActivity(new Intent(getApplicationContext(), ActivityReadArticle1.class));
    }
    public void readArticle2(View view) {
        startActivity(new Intent(getApplicationContext(), ActivityReadArticle2.class));
    }
    public void readArticle3(View view) {
        startActivity(new Intent(getApplicationContext(), ActivityReadArticle3.class));
    }
    public void readArticle4(View view) {
        startActivity(new Intent(getApplicationContext(), ActivityReadArticle4.class));
    }
    public void readArticle5(View view) {
        startActivity(new Intent(getApplicationContext(), ActivityReadArticle5.class));
    }
    public void readArticle6(View view) {
        startActivity(new Intent(getApplicationContext(), ActivityReadArticle6.class));
    }
    public void readArticle7(View view) {
        startActivity(new Intent(getApplicationContext(), ActivityReadArticle7.class));
    }
    public void readBillOfRights(View view) {
        startActivity(new Intent(getApplicationContext(), ActivityReadBillOfRights.class));
    }
    public void readOtherAmendments(View view) {
        startActivity(new Intent(getApplicationContext(), ActivityReadOtherAmendments.class));
    }
}
