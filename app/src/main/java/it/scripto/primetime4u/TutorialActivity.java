package it.scripto.primetime4u;

import android.accounts.AccountManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

        if (preferences.contains(ACCOUNT)){
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
            
            try {
                Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                        new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, false, null, null, null, null);

                startActivityForResult(intent, TutorialActivity.REQUEST_CODE_EMAIL);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "Impossibile trovare un account associato a questo dispositivo", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void goToMain() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
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
            json.addProperty("userName", "Claudio");
            json.addProperty("userBirthYear", "1993");
            json.addProperty("userGender", "M");

            Ion.with(getApplicationContext())
                    .load("POST", url)
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString(ACCOUNT, accountName);
                            editor.apply();
                        }
                    });
        } else {
            Toast.makeText(this, "Impossibile trovare un account associato a questo dispositivo", Toast.LENGTH_LONG).show();
        }
    }
    
    private class TutorialAdapter extends FragmentPagerAdapter {

        private int pagerCount = 5;

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
                    return FirstFragment.newInstance();
                case 2:
                    return FirstFragment.newInstance();
                case 3:
                    return FirstFragment.newInstance();
                case 4:
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