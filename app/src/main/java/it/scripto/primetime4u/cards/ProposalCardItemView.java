package it.scripto.primetime4u.cards;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import it.scripto.primetime4u.R;

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

        // Set poster
        ImageView mPoster = (ImageView) findViewById(R.id.imageView);
        Ion.with(mPoster)
                //.placeholder(R.drawable.placeholder_image)
                //.error(R.drawable.error_image)
                //.animateLoad(spinAnimation)
                //.animateIn(fadeInAnimation)
                .load(card.getPoster());
    }
}