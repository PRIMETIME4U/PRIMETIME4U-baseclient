package it.scripto.primetime4u;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import it.scripto.primetime4u.utils.BaseActivity;

public class PreferencesActivity extends BaseActivity {
    @Override
    protected String getTagLog() {
        return "PrefrencesActivity";
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_preferences;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.SettingsTheme);

        Toolbar toolbar = (Toolbar) findViewById(R.id.preferences_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new PreferencesFragment()).commit();
    }
}
