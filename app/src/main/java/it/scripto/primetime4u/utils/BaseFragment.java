package it.scripto.primetime4u.utils;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.koushikdutta.ion.Ion;

import it.scripto.primetime4u.BuildConfig;

/**
 * Base fragment with logging.
 */
public abstract class BaseFragment extends Fragment {

    public final String TAG = getTagLog();
    public Context context;
    public View view;

    protected abstract String getTagLog();

    protected abstract int getLayoutResourceId();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity();

        if (BuildConfig.DEBUG) Log.v(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(getLayoutResourceId(), container, false);

        if (BuildConfig.DEBUG) {
            Ion.getDefault(getActivity()).configure().setLogging(TAG, Log.DEBUG);
            Log.v(TAG, "onCreateView");
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (BuildConfig.DEBUG) Log.v(TAG, "onResume");
    }

    @Override
    public void onActivityCreated(Bundle saved) {
        super.onActivityCreated(saved);
        if (BuildConfig.DEBUG) Log.v(TAG, "onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        if (BuildConfig.DEBUG) Log.v(TAG, "onStart");
    }

    @Override
    public void onSaveInstanceState(Bundle toSave) {
        super.onSaveInstanceState(toSave);
        if (BuildConfig.DEBUG) Log.v(TAG, "onSaveInstanceState");
    }

    @Override
    public void onPause() {
        super.onPause();
        if (BuildConfig.DEBUG) Log.v(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (BuildConfig.DEBUG) Log.v(TAG, "onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (BuildConfig.DEBUG) Log.v(TAG, "onDestroy");
    }
}
