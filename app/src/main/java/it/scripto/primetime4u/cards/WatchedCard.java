package it.scripto.primetime4u.cards;

import android.content.Context;

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
    
    // TODO: Server pass different date format..
    public void setDate(String date) {
        //i will use digit-only format, no translations needed
        if (date.contains(" January ")) date = date.replace(" January ", "-01-");
        if (date.contains(" February ")) date = date.replace(" February ", "-02-");
        if (date.contains(" March ")) date = date.replace(" March ", "-03-");
        if (date.contains(" April ")) date = date.replace(" April ", "-04-");
        if (date.contains(" May ")) date = date.replace(" May ", "-05-");
        if (date.contains(" June ")) date = date.replace(" June ", "-06-");
        if (date.contains(" July ")) date = date.replace(" July ", "-07-");
        if (date.contains(" August ")) date = date.replace(" August ", "-08-");
        if (date.contains(" September ")) date = date.replace(" September ", "-09-");
        if (date.contains(" October ")) date = date.replace(" October ", "-10-");
        if (date.contains(" November ")) date = date.replace(" November ", "-11-");
        if (date.contains(" December ")) date = date.replace(" December ", "-12-");
        this.date = date;
    }

}
