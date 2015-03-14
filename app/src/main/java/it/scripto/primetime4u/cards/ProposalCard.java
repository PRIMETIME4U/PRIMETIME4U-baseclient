package it.scripto.primetime4u.cards;

import android.content.Context;
import android.support.v7.widget.PopupMenu;

import com.dexafree.materialList.cards.model.ExtendedCard;
import com.dexafree.materialList.controller.OnButtonPressListener;
import com.dexafree.materialList.events.BusProvider;

import it.scripto.primetime4u.R;

public class ProposalCard extends ExtendedCard {
    protected String mMovieInfo;
    private String poster;
    protected OnButtonPressListener onButtonPressListener;
    protected PopupMenu.OnMenuItemClickListener onMenuItemClickListener;
   
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

    public void setMovieInfoText(String movieInfo) {
        this.mMovieInfo = movieInfo;
        BusProvider.dataSetChanged();
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
        BusProvider.dataSetChanged();
    }

    public String getTitle() {
        return super.getTitle();
    }

    public OnButtonPressListener getOnImagePressListener(){
        return onButtonPressListener;
    }
    public void setOnImagePressListener(OnButtonPressListener onButtonPressListener){
        this.onButtonPressListener = onButtonPressListener;
        BusProvider.dataSetChanged();
    }

    public PopupMenu.OnMenuItemClickListener getOnMenuItemClickListener(){
        return onMenuItemClickListener;
    }

    public void setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener onMenuItemClickListener){
        this.onMenuItemClickListener = onMenuItemClickListener;
        BusProvider.dataSetChanged();
    }
}
