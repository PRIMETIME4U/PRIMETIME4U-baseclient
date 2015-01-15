package it.scripto.primetime4u;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import com.dexafree.materialList.cards.model.ExtendedCard;
import com.dexafree.materialList.events.BusProvider;

import java.io.InputStream;

public class ProposalCard extends ExtendedCard {
    protected String mMovieInfo;
    private Bitmap poster;
   
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

    public void setPoster(String imageUrl){
        new DownloadImageTask().execute(imageUrl);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        protected Bitmap doInBackground(String... urls) {

            Bitmap bitmap = null;

            try {
                InputStream in = new java.net.URL(urls[0]).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("ProposalCard", e.getMessage());
            }

            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            poster = result;
            draw();
        }

    }

    private void draw(){
        Drawable drawable = new BitmapDrawable(this.getResources(), poster);
        this.setDrawable(drawable);
    }
}
