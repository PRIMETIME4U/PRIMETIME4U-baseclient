package it.scripto.primetime4u;

import android.content.Context;

import com.dexafree.materialList.cards.model.BigImageCard;
import com.dexafree.materialList.controller.OnButtonPressListener;
import com.dexafree.materialList.events.BusProvider;

public class TasteCard extends BigImageCard {

    protected OnButtonPressListener onTasteButtonPressedListener;
    protected boolean taste = false;


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
}
