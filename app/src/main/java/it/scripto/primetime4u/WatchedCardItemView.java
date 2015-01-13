package it.scripto.primetime4u;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

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
    }
}
