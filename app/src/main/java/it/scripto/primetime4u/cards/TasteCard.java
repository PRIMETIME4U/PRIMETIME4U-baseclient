package it.scripto.primetime4u.cards;

import android.content.Context;

import com.dexafree.materialList.cards.model.BigImageCard;
import com.dexafree.materialList.controller.OnButtonPressListener;
import com.dexafree.materialList.events.BusProvider;

import it.scripto.primetime4u.R;

public class TasteCard extends BigImageCard {

    public static final int MOVIE_TYPE = 0;
    public static final int ARTIST_TYPE = 1;
    
    protected OnButtonPressListener onTasteButtonPressedListener;
    protected boolean taste = false;
    protected int type = MOVIE_TYPE;

    private String poster;

    public TasteCard(Context context) {
        super(context);
    }

    @Override
    public int getLayout() {
        return R.layout.taste_card_layout;
    }

    public OnButtonPressListener getOnTasteButtonPressedListener() {
        return onTasteButtonPressedListener;
    }

    public void setOnTasteButtonPressedListener(OnButtonPressListener onTasteButtonPressedListener) {
        this.onTasteButtonPressedListener = onTasteButtonPressedListener;
    }

    public boolean getTaste() {
        return this.taste;
    }
    
    public void setTaste(boolean taste) {
        this.taste = taste;
        BusProvider.dataSetChanged();
    }
    
    public void setType(int type) {
        this.type = type;
    } 
    
    public int getType() {
        return this.type;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }
}
