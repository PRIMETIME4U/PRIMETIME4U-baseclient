package it.scripto.primetime4u.cards;

import android.content.Context;

import com.dexafree.materialList.events.BusProvider;

import it.scripto.primetime4u.R;

public class WatchedCard extends TasteCard {
    
    protected String date;

    public WatchedCard(Context context) {
        super(context);
    }

    @Override
    public int getLayout() {
        return R.layout.watched_card_layout;
    }

    public String getDate() {
        return this.date;
    }
    
    public void setDate(String date) {
        this.date = date;
        BusProvider.dataSetChanged();
    }

}
