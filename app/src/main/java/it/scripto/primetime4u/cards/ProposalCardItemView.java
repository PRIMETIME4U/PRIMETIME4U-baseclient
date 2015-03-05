package it.scripto.primetime4u.cards;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import it.scripto.primetime4u.R;

import static it.scripto.primetime4u.utils.Utils.resizeImageUrl;

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
        final ImageView mPoster = (ImageView) findViewById(R.id.imageView);
        mPoster.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                card.getOnImagePressListener().onButtonPressedListener(mPoster,card);
            }
        });
        Ion.with(mPoster)
                //.placeholder(R.drawable.placeholder_image)
                //.error(R.drawable.error_image)
                //.animateLoad(spinAnimation)
                //.animateIn(fadeInAnimation)
                .centerCrop()
                .load(resizeImageUrl(card.getPoster(), 0));
    }
}