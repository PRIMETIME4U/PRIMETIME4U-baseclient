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
        // TODO: chose format for date
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
