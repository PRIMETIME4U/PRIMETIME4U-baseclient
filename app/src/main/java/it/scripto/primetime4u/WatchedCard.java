package it.scripto.primetime4u;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;

public class WatchedCard extends TasteCard {
    
    protected String date;

    private Bitmap poster;
    
    public WatchedCard(Context context) {
        super(context);
    }

    @Override
    public int getLayout() {
        return R.layout.watched_card_layout;
    }

    public String getDate() {
        return this.date;
    }
    
    public void setDate(String date) {
        //i will use digit-only format, no translations needed
        if (date.contains(" January ")) date = date.replace(" January ", "-01-");
        if (date.contains(" February ")) date = date.replace(" February ", "-02-");
        if (date.contains(" March ")) date = date.replace(" March ", "-03-");
        if (date.contains(" April ")) date = date.replace(" April ", "-04-");
        if (date.contains(" May ")) date = date.replace(" May ", "-05-");
        if (date.contains(" June ")) date = date.replace(" June ", "-06-");
        if (date.contains(" July ")) date = date.replace(" July ", "-07-");
        if (date.contains(" August ")) date = date.replace(" August ", "-08-");
        if (date.contains(" September ")) date = date.replace(" September ", "-09-");
        if (date.contains(" October ")) date = date.replace(" October ", "-10-");
        if (date.contains(" November ")) date = date.replace(" November ", "-11-");
        if (date.contains(" December ")) date = date.replace(" December ", "-12-");
        this.date = date;
    }

    public void setPoster(String imgurl){
        new DownloadImageTask().execute(imgurl);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {


        protected Bitmap doInBackground(String... urls) {

            String urldisplay = urls[0];
            Bitmap _bitmap = null;

            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                _bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Errore", e.getMessage());
                e.printStackTrace();
            }

            return _bitmap;
        }

        protected void onPostExecute(Bitmap result) {

            poster = result;
            draw();
        }

    }

    private void draw(){
        Drawable d = new BitmapDrawable(this.getResources(),poster);
        this.setDrawable(d);
    }
}
