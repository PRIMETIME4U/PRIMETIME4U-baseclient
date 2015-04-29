package it.scripto.primetime4u;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Toast;

import com.github.machinarius.preferencefragment.PreferenceFragment;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import com.google.gson.JsonArray;

import java.util.Map;

import it.scripto.primetime4u.utils.Utils;

public class PreferencesFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private Context context;
    private Map<String, ?> pref;
    private static final String TAG = "PreferencesFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity();

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        SharedPreferences.OnSharedPreferenceChangeListener listener =
                new SharedPreferences.OnSharedPreferenceChangeListener() {
                    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                        // listener implementation
                    }
                };

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Log.i(TAG, context.getPackageName() + "_preferences");
        sharedPref.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        pref = sharedPreferences.getAll();
        if (!(Boolean) pref.get("free_enabled") && !(Boolean) pref.get("premium_enabled") &&!(Boolean) pref.get("sky_enabled")) {
            Toast.makeText(context, getString(R.string.almost_one_tv_type), Toast.LENGTH_SHORT).show();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("free_enabled", true);
            editor.apply();
        }
        sendSettings();

    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();


        sendSettings();

        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);

    }
    private void sendSettings(){
        if (pref != null) {
            JsonObject json = new JsonObject();
            JsonArray list = new JsonArray();
            json.addProperty("repeatChoice", (Boolean) pref.get("repeat_choice_enable"));
            json.addProperty("timeNotification", (Long) pref.get("time_notification"));
            json.addProperty("enableNotification",(Boolean)  pref.get("notification_enabled"));

            if ((Boolean) pref.get("free_enabled")) {
                list.add(new JsonPrimitive("free"));
            }
            if ((Boolean) pref.get("premium_enabled")) {
                list.add(new JsonPrimitive("premium"));
            }
            if ((Boolean) pref.get("sky_enabled")) {
                list.add(new JsonPrimitive("sky"));
            }

            json.add("tvType", list);

            SharedPreferences preferences = this.getActivity().getSharedPreferences(TutorialActivity.PREFERENCES, Context.MODE_PRIVATE);
            String account = preferences.getString(TutorialActivity.ACCOUNT, null);

            String url = Utils.SERVER_API + "settings/" + account;

            Log.i(TAG, "Invio: " + String.valueOf(json));

            Log.i(TAG, "Faccio chiamata");

            Ion.with(getActivity().getApplicationContext())
                    .load("POST", url)
                    .setJsonObjectBody(json)
                    .asJsonObject();
        }
    }
}
