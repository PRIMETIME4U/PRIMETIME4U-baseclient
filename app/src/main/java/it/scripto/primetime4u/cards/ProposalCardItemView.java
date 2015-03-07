package it.scripto.primetime4u.cards;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.util.AttributeSet;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.ion.Ion;

import it.scripto.primetime4u.R;

import static it.scripto.primetime4u.utils.Utils.resizeImageUrl;

public class ProposalCardItemView<T extends ProposalCard> extends BaseButtonsCardItemView<T> {

    private Context context;

    public ProposalCardItemView(Context context) {
        super(context);
        this.context = context;
    }

    public ProposalCardItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public ProposalCardItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
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

        ImageView menu = (ImageView) findViewById(R.id.menu_image_view);
        menu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getContext(), v);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.menu_proposal, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        // TODO: send negative taste to server and use snackbar
                        Toast.makeText(context, "Non ti piace!", Toast.LENGTH_LONG).show();
                        return true;
                    }
                });
                popup.show();
            }
        });

    }
}