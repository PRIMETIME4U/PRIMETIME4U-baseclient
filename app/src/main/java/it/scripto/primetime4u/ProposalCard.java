package it.scripto.primetime4u;

import android.content.Context;

import com.dexafree.materialList.cards.model.BigImageButtonsCard;

public class ProposalCard extends BigImageButtonsCard {
    
    public ProposalCard(Context context) {
        super(context);
    }

    @Override
    public int getLayout() {
        return R.layout.proposal_card_layout;
    }
}
