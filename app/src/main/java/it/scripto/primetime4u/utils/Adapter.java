package it.scripto.primetime4u.utils;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;

/**
 * Created by claudio on 24 Jan 2015.
 */
public class Adapter extends AdapterView {
    public Adapter(Context context) {
        super(context);
    }

    @Override
    public android.widget.Adapter getAdapter() {
        return null;
    }

    @Override
    public void setAdapter(android.widget.Adapter adapter) {

    }

    @Override
    public View getSelectedView() {
        return null;
    }

    @Override
    public void setSelection(int i) {

    }
}
