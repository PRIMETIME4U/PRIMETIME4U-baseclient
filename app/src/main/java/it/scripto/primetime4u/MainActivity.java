package it.scripto.primetime4u;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;

public class MainActivity extends BaseActivity {

    private String account;

    private SharedPreferences preferences;

    private SharedPreferences.Editor editor;

    public MainAdapter adapter;

    private ViewPager pager;

    private Toolbar main_activity_toolbar;

    private PagerSlidingTabStrip tabs;

    //boolean which acts like a refresh alert
    public boolean shouldIRefresh;

    //boolean which says if i'm in the Tastes tab
    public boolean tasteTab;

    @Override
    protected String getTagLog() {
        return "MainActivity";
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //I close the login activity
        Intent myIntent = new Intent(StartActivity.ACTION_CLOSE);
        sendBroadcast(myIntent);
        shouldIRefresh = false;

        preferences = getPreferences(Context.MODE_PRIVATE);

        //ricavo l'email passata dalla startActivity e la salvo in maniera permanente
        if (!preferences.contains("ACCOUNT")){
            account = getIntent().getExtras().getString("email");
            editor = preferences.edit();
            editor.putString("ACCOUNT",account);
            editor.commit();
        }
        else {
            account = preferences.getString("ACCOUNT","");
        }
        // Get and set toolbar as action bar
        main_activity_toolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(main_activity_toolbar);
 
        // Initialize the ViewPager and set an adapter
        pager = (ViewPager) findViewById(R.id.main_activity_view_pager);
        adapter = new MainAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);

        // Bind the tabs to the ViewPager
        tabs = (PagerSlidingTabStrip) findViewById(R.id.main_activity_pager_sliding_tab_strip);
        tabs.setViewPager(pager);

        tabs.setOnPageChangeListener( new ViewPager.OnPageChangeListener() {
            private int current;
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                current = position;
            }

            @Override
            public void onPageSelected(int position) {
                if (position==1) {
                    //change toolbar style
                    tasteTab = true;
                    invalidateOptionsMenu();
                }
                else{
                    tasteTab = false;
                    invalidateOptionsMenu();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == pager.SCROLL_STATE_IDLE){
                    if (shouldIRefresh){
                        //if alert of refreshing has been activated, i reload fragments
                        refreshTastes();
                        shouldIRefresh = false;
                    }
                }
            }
        });





    }
    public void refreshTastes(){
       adapter.notifyDataSetChanged();
    }

    public String getAccount(){
        if (account!=null)
            return account;
        else return "";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_nosearch, menu);

        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        //to dynamically change menu
        if (tasteTab){
            getMenuInflater().inflate(R.menu.menu_main,menu);
            MenuItem searchItem = menu.findItem(R.id.searchButton);
            if (searchItem!=null){
                SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
                searchView.setQueryHint("Search movie/artist, es: Matrix, Di Caprio");
                //TODO: i listener sulle ricerche vanno settati su questa searchView
                MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        Toast.makeText(getBaseContext(),"Search has been expanded",Toast.LENGTH_LONG).show();
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        //collapse action view
                        return true;
                    }
                });
            }

        }
        else {
            getMenuInflater().inflate(R.menu.menu_main_nosearch,menu);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
            //gestione del tasto impostazioni, modificare impostazioni su notifiche
        }
        if (id == R.id.searchButton){

            //gestione in onPrepareOptionMenu

        }

        return super.onOptionsItemSelected(item);
    }


    private class MainAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {
                getResources().getString(R.string.proposal_tab),
                getResources().getString(R.string.tastes_tab),
                getResources().getString(R.string.watched_tab)};

        public MainAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:

                    return ProposalFragment.newInstance();
                case 1:

                    return TastesFragment.newInstance();
                case 2:

                    return WatchedFragment.newInstance();
                default:
                    break;
            }
            return null;
        }

        @Override
        public int getItemPosition(Object object){
            return POSITION_NONE;
        }


    }
    
}
