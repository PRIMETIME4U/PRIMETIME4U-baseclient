package it.scripto.primetime4u.utils;


import android.content.Context;

import com.dexafree.materialList.controller.MaterialListViewAdapter;

public class ProposalListAdapter extends MaterialListViewAdapter {
    private int count;

    public ProposalListAdapter(Context context) {
        super(context);
        count = 3;
    }

    @Override
    public int getCount() {
        return (super.getCount() < count) ? super.getCount() : count;
    }

    public void increaseCount() {
        int newCount = count + 3;
        count = newCount <= super.getCount() ? newCount : super.getCount();
        notifyDataSetChanged();
    }

    public int getSize() {
        return super.getCount();
    }

    public void setCount(int count) {
        this.count = count;
    }
}
