package it.scripto.primetime4u;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class ProposalCardItemView<T extends ProposalCard> extends BaseButtonsCardItemView<T> {
    
    public ProposalCardItemView(Context context) {
        super(context);
    }

    public ProposalCardItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProposalCardItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void build(final T card) {
        super.build(card);

        // Movie info
        TextView movieInfoText = (TextView) findViewById(R.id.movie_info_text_view);
        movieInfoText.setText(card.getMovieInfoText());
    }
}