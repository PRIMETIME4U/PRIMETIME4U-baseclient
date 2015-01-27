package it.scripto.primetime4u.cards;

import android.content.Context;

import com.dexafree.materialList.cards.model.BasicButtonsCard;

import it.scripto.primetime4u.R;

public class WelcomeCard extends BasicButtonsCard {

    public WelcomeCard(Context context) {
        super(context);
    }

    @Override
    public int getLayout() {
        return R.layout.welcome_card_layout;
    }


}
