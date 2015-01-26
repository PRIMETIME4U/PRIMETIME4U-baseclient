package it.scripto.primetime4u.utils;

import android.content.Context;

import com.dexafree.materialList.controller.MaterialListViewAdapter;

public class MaterialListAdapter extends MaterialListViewAdapter {
    public MaterialListAdapter(Context context) {
        super(context);
    }

    // TODO: they don't work...
    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }
}
