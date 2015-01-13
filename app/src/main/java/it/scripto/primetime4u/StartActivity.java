package it.scripto.primetime4u;

import android.accounts.AccountManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;

import com.astuetz.PagerSlidingTabStrip;


public class StartActivity extends BaseActivity {

    /**
     * Launcher activity, retrieves users' email in order to subscribe to primetime4u service
     */
    public static final String ACTION_CLOSE = "it.scripto.primetime4u.ACTION_CLOSE";

    private CloseReceiver closeReceiver;

    private static final int REQUEST_CODE_EMAIL = 1;

    private String account;

    class CloseReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_CLOSE)) {
                StartActivity.this.finish();
            }
        }
    }

    @Override
    protected String getTagLog() {
        return "StartActivity";
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_start;
    }

    private void createFrag(){

        ViewPager pager = (ViewPager) findViewById(R.id.start_activity_view_pager);
        pager.setAdapter(new StartAdapter(getSupportFragmentManager()));

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.start_activity_pager_sliding_tab_strip);
        tabs.setViewPager(pager);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_EMAIL && resultCode == RESULT_OK) {
            String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            account=accountName;
            createFrag();

        }
        else{
            Toast.makeText(this, "Impossibile trovare un account associato a questo dispositivo", Toast.LENGTH_LONG).show();
            account="Utente sconosciuto";
            createFrag();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setto l'intent filter per chiudere questa activity dalla mainactivity
        IntentFilter filter = new IntentFilter(ACTION_CLOSE);
        closeReceiver = new CloseReceiver();
        registerReceiver(closeReceiver, filter);

        System.out.println("i'm here, in onCreate");

        Toolbar toolbar = (Toolbar) findViewById(R.id.start_activity_toolbar);
        setSupportActionBar(toolbar);


        //ricavo l'email associata al dispositivo
        try {
            Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                    new String[] { GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE }, false, null, null, null, null);
            System.out.println("i just passed account picker");
            startActivityForResult(intent, REQUEST_CODE_EMAIL);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Impossibile trovare un account associato a questo dispositivo", Toast.LENGTH_LONG).show();
        }



    }



    public String getAccount(){
        if (account!=null)
            return account;
        else return "";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }

    private class StartAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {
                "Benvenuto"
        };

        public StartAdapter(FragmentManager fm) {
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
                    return StartFragment.newInstance();
                default:
                    break;
            }
            return null;
        }
    }
}
