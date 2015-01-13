package it.scripto.primetime4u;

import android.content.Context;

import com.dexafree.materialList.cards.model.ExtendedCard;
import com.dexafree.materialList.events.BusProvider;

public class ProposalCard extends ExtendedCard {
    protected String mMovieInfo;
   
    public ProposalCard(Context context) {
        super(context);
    }

    @Override
    public int getLayout() {
        return R.layout.proposal_card_layout;
    }

    public String getMovieInfoText() {
        return mMovieInfo;
    }

    public void setMovieInfoText(int movieInfoId) {
        setDescription(getString(movieInfoId));
    }

    public void setMovieInfoText(String movieInfo) {
        this.mMovieInfo = movieInfo;
        BusProvider.dataSetChanged();
    }
}
