package it.scripto.primetime4u.cards;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import java.util.Random;

import it.scripto.primetime4u.R;

import static it.scripto.primetime4u.utils.Utils.resizeImageUrl;

public class TasteCardItemView<T extends TasteCard> extends BigImageCardItemView<T> {
    public TasteCardItemView(Context context) {
        super(context);
    }

    public TasteCardItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TasteCardItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void build(final T card) {
        super.build(card);

        // Image View
        final ImageView tasteImageView = (ImageView) findViewById(R.id.taste_image_view);
        tasteImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                card.setTaste(!card.getTaste());
                card.getOnTasteButtonPressedListener().onButtonPressedListener(tasteImageView, card);
            }
        });
        
        // Set image
        if (card.getTaste()) {
            tasteImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_delete_taste));
        } else {
            tasteImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_add_taste));
        }
        
        // Set description
        TextView mDescription = (TextView) findViewById(R.id.descriptionTextView);
        String description = null;
        
        if (card.getType() == TasteCard.MOVIE_TYPE) {
            description = getResources().getString(R.string.movie);    
        } else if (card.getType() == TasteCard.ARTIST_TYPE) {
            description = getResources().getString(R.string.artist);
        } else if (card.getType() == TasteCard.GENRE_TYPE) {
            description = card.getDescription();
        }

        mDescription.setText(description);
        
        // Set width
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        CardView cardView = (CardView) findViewById(R.id.cardView);
        
        // TODO: Resolve problem with width.. the card is cutted
//        if (card.getType() == 1) {
//            cardView.getLayoutParams().width = width / 2 - (int)getResources().getDimension(R.dimen.small_padding);
//        } else {
//            cardView.getLayoutParams().width = width - (int)getResources().getDimension(R.dimen.small_padding);
//        }

        TextView genreText = (TextView) findViewById(R.id.taste_genre_letter);
        if (card.getType() != TasteCard.GENRE_TYPE) {
            genreText.setVisibility(INVISIBLE);
            // Set poster
            ImageView mPoster = (ImageView) findViewById(R.id.imageView);
            Ion.with(mPoster)
                    //.placeholder(R.drawable.placeholder_image)
                    //.error(R.drawable.error_image)
                    //.animateLoad(spinAnimation)
                    //.animateIn(fadeInAnimation)
                    .centerCrop()
                    .load(resizeImageUrl(card.getPoster(), 0));
        } else {
            // Set first letter of the genre
            genreText.setVisibility(VISIBLE);
            genreText.setText(Character.toString(card.getDescription().charAt(0)));
            Random random = new Random();
            genreText.setBackgroundColor((0xff000000 | random.nextInt(0x00ffffff)));
        }
    }
}
