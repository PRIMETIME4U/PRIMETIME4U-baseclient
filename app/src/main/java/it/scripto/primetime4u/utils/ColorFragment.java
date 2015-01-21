package it.scripto.primetime4u.utils;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class ColorFragment extends BaseFragment {

    public ColorFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        view.setBackgroundColor(getColor());

        return view;
    }

    protected abstract int getColor();
}
