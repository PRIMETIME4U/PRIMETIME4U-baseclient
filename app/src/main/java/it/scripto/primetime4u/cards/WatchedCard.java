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
        this.date = !date.equals("01-01-1900") ? String.format(getResources().getString(R.string.watched_text), date) : getResources().getString(R.string.undefined) ;
        BusProvider.dataSetChanged();
    }

}