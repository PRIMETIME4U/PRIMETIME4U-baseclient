package it.scripto.primetime4u;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

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
        
    }
}
