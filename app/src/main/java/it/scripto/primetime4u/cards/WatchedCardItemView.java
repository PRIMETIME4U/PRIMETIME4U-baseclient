package it.scripto.primetime4u.cards;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import it.scripto.primetime4u.R;

public class WatchedCardItemView<T extends WatchedCard> extends TasteCardItemView<T> {
    
    public WatchedCardItemView(Context context) {
        super(context);
    }

    public WatchedCardItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WatchedCardItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void build(T card) {
        super.build(card);

        // Set description
        TextView mDescription = (TextView) findViewById(R.id.descriptionTextView);
        mDescription.setText(String.format(getResources().getString(R.string.watched_text), card.getDate()));
        
        // Set poster
        ImageView mPoster = (ImageView) findViewById(R.id.imageView);
        Ion.with(mPoster)
                //.placeholder(R.drawable.placeholder_image)
                //.error(R.drawable.error_image)
                //.animateLoad(spinAnimation)
                //.animateIn(fadeInAnimation)
                .centerCrop()
                .load(card.getPoster());
    }
}
