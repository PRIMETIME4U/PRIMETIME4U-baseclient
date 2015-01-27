package it.scripto.primetime4u;

import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import it.scripto.primetime4u.utils.BaseActivity;
import it.scripto.primetime4u.utils.Utils;
import me.relex.circleindicator.CircleIndicator;


public class TutorialActivity extends BaseActivity {

    public static final int REQUEST_CODE_EMAIL = 1;
    public static final String PREFERENCES = "PRIMETIME4U";
    public static final String ACCOUNT = "ACCOUNT";
    private SharedPreferences preferences;

    @Override
    protected String getTagLog() {
        return "TutorialActivity";
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_tutorial;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);

        if (preferences.contains(ACCOUNT) && !preferences.getString(ACCOUNT,"").equals("invalidUser")){
            goToMain();    
        } else {
            final ViewPager tutorialViewPager = (ViewPager) findViewById(R.id.viewpager_default);
            CircleIndicator defaultIndicator = (CircleIndicator) findViewById(R.id.indicator_default);
            final TutorialAdapter tutorialAdapter = new TutorialAdapter(getSupportFragmentManager());
            tutorialViewPager.setAdapter(tutorialAdapter);
            defaultIndicator.setViewPager(tutorialViewPager);

            TextView skipTextView = (TextView) findViewById(R.id.skip_text_view);
            TextView nextTextView = (TextView) findViewById(R.id.next_text_view);

            skipTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToMain();
                }
            });

            nextTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "Position: " + String.valueOf(tutorialViewPager.getCurrentItem()));
                    if (tutorialViewPager.getCurrentItem() == tutorialAdapter.getCount() - 1) {
                        goToMain();
                    } else {
                        tutorialViewPager.setCurrentItem(tutorialViewPager.getCurrentItem() + 1);
                    }
                }
            });

            if (verifyConnection()) {
                try {
                    Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                            new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, false, null, null, null, null);

                    startActivityForResult(intent, TutorialActivity.REQUEST_CODE_EMAIL);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, "Impossibile trovare un account associato a questo dispositivo", Toast.LENGTH_LONG).show();
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(ACCOUNT, "invalidUser");
                    editor.apply();

                }
            }
            else {
                showSettingsAlert();
            }
        }
    }

    private void goToMain() {
        if (!preferences.getString(ACCOUNT,"").equals("invalidUser")) {
            if (verifyConnection()) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
            else {
                showSettingsAlert();
            }
        }
        else{
            if (verifyConnection()) {
                try {
                    Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                            new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, false, null, null, null, null);

                    startActivityForResult(intent, TutorialActivity.REQUEST_CODE_EMAIL);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, "Impossibile trovare un account associato a questo dispositivo", Toast.LENGTH_LONG).show();
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(ACCOUNT, "invalidUser");
                    editor.apply();

                }
            }
            else {
                showSettingsAlert();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_EMAIL && resultCode == RESULT_OK) {
            final String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            
            // Generate URL
            String url = Utils.SERVER_API + "subscribe/";

            // TODO: generify request
            JsonObject json = new JsonObject();
            json.addProperty("userId", accountName);
            json.addProperty("userName", "Utente");
            json.addProperty("userBirthYear", "1990");
            json.addProperty("userGender", "M");

            Ion.with(getApplicationContext())
                    .load("POST", url)
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            SharedPreferences.Editor editor = preferences.edit();
                            if (preferences.contains("ACCOUNT")){
                                editor.remove("ACCOUNT");
                            }
                            editor.putString(ACCOUNT, accountName);
                            editor.apply();
                        }
                    });
        } else {
            Toast.makeText(this, "Seleziona un account valido", Toast.LENGTH_LONG).show();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(ACCOUNT, "invalidUser");
            editor.apply();
        }
    }

    private boolean verifyConnection(){
        Context _context=getApplicationContext();
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo anInfo : info) {
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }

        }
        return false;
    }

    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // titolo dialog
        alertDialog.setTitle("Status connessione");

        // messaggio dialog
        alertDialog.setMessage("Connessione internet assente, vuoi accedere alle impostazioni?");

        // bottone impostazioni
        alertDialog.setPositiveButton("Impostazioni", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivity(intent);
                finish();
            }
        });

        // annulla
        alertDialog.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });
        alertDialog.show();
    }

    private class TutorialAdapter extends FragmentPagerAdapter {

        private int pagerCount = 1;

        public TutorialAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public int getCount() {
            return pagerCount;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return FirstFragment.newInstance();
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