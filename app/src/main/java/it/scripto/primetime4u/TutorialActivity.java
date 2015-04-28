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
import android.os.AsyncTask;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import it.scripto.primetime4u.utils.BaseActivity;
import it.scripto.primetime4u.utils.Utils;
import me.relex.circleindicator.CircleIndicator;


public class TutorialActivity extends BaseActivity {

    public static final int REQUEST_CODE_EMAIL = 1;
    public static final String PREFERENCES = "PRIMETIME4U";
    public static final String ACCOUNT = "ACCOUNT";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 90000;
    private SharedPreferences preferences;
    private String privateKey;
    private GoogleCloudMessaging gcm;
    private String accountName;
    private String account;

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
        privateKey = preferences.getString("privateKey", "");

        if (preferences.contains(ACCOUNT) && !preferences.getString(ACCOUNT,"").equals("invalidUser")){
            goToMain();    
        } else {

            final ViewPager tutorialViewPager = (ViewPager) findViewById(R.id.viewpager_default);
            final CircleIndicator defaultIndicator = (CircleIndicator) findViewById(R.id.indicator_default);
            final TutorialAdapter tutorialAdapter = new TutorialAdapter(getSupportFragmentManager());
            tutorialViewPager.setAdapter(tutorialAdapter);
            defaultIndicator.setViewPager(tutorialViewPager);

            TextView skipTextView = (TextView) findViewById(R.id.skip_text_view);
            TextView nextTextView = (TextView) findViewById(R.id.next_text_view);

            skipTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //register();
                    goToMain();
                }
            });

            nextTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "Position: " + String.valueOf(tutorialViewPager.getCurrentItem()));
                    if (tutorialViewPager.getCurrentItem() == tutorialAdapter.getCount() - 1) {
                        //register();
                        goToMain();
                    } else {
                        tutorialViewPager.setCurrentItem(tutorialViewPager.getCurrentItem() + 1);
                    }
                }
            });

            if(privateKey.equals("")) {
                if(checkPlayServices()) {
                    gcm = GoogleCloudMessaging.getInstance(this);
                    gcmRegistration();
                }
            }else{
                Log.i(TAG, "PrivateKey già presente");
            }

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

    // You need to do the Play Services APK check here too.
    @Override
    public void onResume() {
        super.onResume();
        checkPlayServices();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_EMAIL && resultCode == RESULT_OK) {
            accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(ACCOUNT, accountName);
            setAccount(accountName);
            editor.apply();
            register();

        } else {
            Toast.makeText(this, "Seleziona un account valido", Toast.LENGTH_LONG).show();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(ACCOUNT, "invalidUser");
            editor.apply();
        }
    }

    public void gcmRegistration() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                try {
                    if(gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(TutorialActivity.this);
                    }
                    String gcmKey = gcm.register(Utils.GCM_SENDER_ID);
                    Log.i(TAG, gcmKey);

                    return gcmKey;
                }catch(IOException e) {
                    Log.e(TAG, String.valueOf(e));
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                if(result != null){
                    privateKey = result;
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("privateKey", result);
                    editor.commit();

                }else{
                    gcmRegistration();
                }

            }

        }.execute(null, null, null);
    }

    private void register() {
        // Generate URL
        String url = Utils.SERVER_API + "subscribe/";


        JsonObject json = new JsonObject();
        json.addProperty("userId", accountName);
        json.addProperty("userName", "Utente");
        json.addProperty("privateKey", privateKey);
        json.addProperty("userBirthYear", "1990");
        json.addProperty("userGender", "M");

        Ion.with(getApplicationContext())
                .load("POST", url)
                .setJsonObjectBody(json)
                .asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                Log.i(TAG, "Risposta subscribe: " + result.toString());

                SharedPreferences preferences = getSharedPreferences("it.scripto.primetime4u_preferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("repeat_choice_enable", result.get("repeatChoice").getAsBoolean());
                editor.putBoolean("notification_enabled", result.get("enableNotification").getAsBoolean());
                editor.putLong("time_notification", result.get("timeNotification").getAsLong());
                JsonArray tvTypeList = result.get("tvType").getAsJsonArray();
                Gson converter = new Gson();
                Type type = new TypeToken<List<String>>(){}.getType();
                List<String> list = converter.fromJson(tvTypeList, type);
                for (String tvType : list) {
                    switch (tvType) {
                        case "free":
                            editor.putBoolean("free_enabled", true);
                            break;
                        case "premium":
                            editor.putBoolean("premium_enabled", true);
                            break;
                        case "sky":
                            editor.putBoolean("sky_enabled", true);
                            break;
                    }
                }
                editor.apply();
            }
        });

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
            public void onClick(DialogInterface dialog, int which) {
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

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    public String getAccount(){
        Log.i(TAG, "Getto account: " + account);
        return account;
    }

    public void setAccount(String s){
        Log.i(TAG, "Setto account: " + s);
        this.account=s;
    }

    private class TutorialAdapter extends FragmentPagerAdapter {

        private int pagerCount = 2;

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
                case 1:
                    return SecondFragment.newInstance();
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