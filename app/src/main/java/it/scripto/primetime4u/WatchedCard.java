package it.scripto.primetime4u;

import android.content.Context;

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
        // TODO: chose format for date
        this.date = date;
    }
    
}
