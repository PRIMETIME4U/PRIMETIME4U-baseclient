package it.scripto.primetime4u;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;

import java.util.List;

import it.scripto.primetime4u.utils.BaseActivity;
import it.scripto.primetime4u.utils.RefreshFragment;

public class MainActivity extends BaseActivity implements WatchedFragment.onTasteChangeListener, 
        TastesFragment.onTasteChangeListener {

    private String account;

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

        SharedPreferences preferences = getSharedPreferences(TutorialActivity.PREFERENCES, Context.MODE_PRIVATE);

        account = preferences.getString(TutorialActivity.ACCOUNT, null);

        // Get and set toolbar as action bar
        Toolbar main_activity_toolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(main_activity_toolbar);
 
        // Initialize the ViewPager and set an adapter
        ViewPager pager = (ViewPager) findViewById(R.id.main_activity_view_pager);
        MainAdapter adapter = new MainAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.main_activity_pager_sliding_tab_strip);
        tabs.setViewPager(pager);
    }
    
    /**
     *
     */
    public String getAccount() {
        return account;
    }
    
    /**
     *
     */
    @Override
    public void onTasteChanged() {
        List<Fragment> allFragments = getSupportFragmentManager().getFragments();
        if (allFragments != null) {
            for (Fragment fragment : allFragments) {
                try {
                    RefreshFragment refreshFragment = (RefreshFragment) fragment;
                    refreshFragment.refresh();
                } catch (ClassCastException ignored) {
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        for(int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            SpannableString spanString = new SpannableString(menu.getItem(i).getTitle().toString());
            spanString.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0, spanString.length(), 0); //fix the color to white
            item.setTitle(spanString);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        //GESTIONE PREFERENZE PROGRAMMAZIONE
        //TODO: impostazioni sarà un'altra activity
        switch (item.getItemId()) {

            case R.id.action_free:
                Toast.makeText(this,"Hai scelto la programmazione free",Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_sky:
                Toast.makeText(this,"Hai scelto la programmazione sky",Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_premium:
                Toast.makeText(this,"Hai scelto la programmazione premium",Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_settings:
                Toast.makeText(this,"Hai scelto impostazioni",Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_about:
                Toast.makeText(this,"Claudio, Dorel, Giovanni e Marius ti salutano",Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
