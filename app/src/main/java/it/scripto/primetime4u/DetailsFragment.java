package it.scripto.primetime4u;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dexafree.materialList.cards.model.BigImageCard;
import com.dexafree.materialList.cards.model.Card;
import com.dexafree.materialList.controller.OnButtonPressListener;
import com.dexafree.materialList.view.MaterialListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import primetime4u.app.AppController;


/**
 * Created by Giovanni on 14/01/2015.
 */
public class DetailsFragment extends BaseFragment{

    //detail di prova, scary movie 2: tt0257106 (non funge, uso serendipity)

    private final String detailsurl="http://hale-kite-786.appspot.com/api/detail/movie/tt0240890";

    private String id;

    private MaterialListView details_material_list_view;

    private Bitmap poster;

    private static HashMap<String,String> film;
    @Override
    protected String getTagLog() {
        return "DetailsFragment";
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_details;
    }

    public DetailsFragment(){

    }

    public static DetailsFragment newInstance(){
        return new DetailsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        DetailsActivity base;
        base = (DetailsActivity) this.getActivity();
        id = "";
        //da "base" ricaverò l'id del film che andrò a cercare nei dettagli
        //id = base.getMovieId()
        //detailsurl = detailsurl + id;

        film = new HashMap<String,String>();

        details_material_list_view = (MaterialListView) view.findViewById(R.id.details_material_list_view);

        //film sara' un id di imdb della forma tt0240890
        String movieId = "tt0240890";

        new JsonRequest().execute(detailsurl);

        Toast.makeText(context,"Caricamento dettagli " + base.getFilm()+" in corso",Toast.LENGTH_LONG).show();
        //fare request e mostrare immagine



        return view;

    }

    class JsonRequest extends AsyncTask<String,Void,Void>{

        @Override
        protected Void doInBackground(String... params) {
            String url = params[0];

            JsonObjectRequest objReq = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>(){

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("DetailsFragment",response.toString());

                            //costruzione JSON di Risposta
                            try{
                                JSONObject data = response.getJSONObject("data");
                                /**
                                 * struttura JSON ricevuto:
                                 * data: {
                                 *      detail: {
                                 *          actors: [
                                 *                 "nnnn999" //id attore
                                 *          ]
                                 *          countries: []
                                 *          genres: []
                                 *          directors:[]
                                 *          keywords: []
                                 *          original_title: stringa
                                 *          plot: stringa
                                 *          poster: stringa
                                 *          rated: stringa
                                 *          run_times: string
                                 *          simple_plot: string
                                 *          title: string
                                 *          trailer: link
                                 *          writers: []
                                 *          year: string
                                 *      }
                                 * }
                                 */
                                JSONObject details = data.getJSONObject("detail");
                                JSONArray actors = details.getJSONArray("actors");
                                //aggiungo gli attori alla hashmap
                                ArrayList<String> temp = new ArrayList<String>();

                                /*for (int i=0; i<actors.length();i++){
                                    temp.add(actors.getString(i));
                                }
                                film.put("actors",temp);
                                temp.clear();
                                //aggiungo i paesi
                                JSONArray countries = details.getJSONArray("countries");
                                for (int i=0; i<countries.length();i++){
                                    temp.add(countries.getString(i));
                                }
                                film.put("countries",temp);
                                temp.clear();
                                //aggiungo i generi
                                JSONArray genres = details.getJSONArray("genres");
                                for (int i=0; i<genres.length();i++){
                                    temp.add(genres.getString(i));
                                }
                                film.put("genres",temp);
                                temp.clear();
                                //aggiungo i registi
                                JSONArray directors = details.getJSONArray("directors");
                                for (int i=0; i<directors.length();i++){
                                    temp.add(directors.getString(i));
                                }
                                film.put("directors",temp);
                                temp.clear();
                                */
                                //titolo, tit originale, trama e copertina
                                String title = details.getString("title");
                                String original_title = details.getString("original_title");
                                String plot = details.getString("plot");
                                String poster = details.getString("poster");

                                System.out.println("titolo: " + title);

                                film.put("title",title);

                                film.put("original_title",original_title);

                                film.put("plot",plot);

                                film.put("poster", poster);

                                new DownloadImageTask().execute(film.get("poster"));



                            }
                            catch(JSONException e){
                                Toast.makeText(context,"Eccezione Json",Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener(){

                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Toast.makeText(context,"Errore nel download dei dettagli film",Toast.LENGTH_LONG).show();
                        }
                    });

            AppController.getInstance().addToRequestQueue(objReq);
            return null;
        }

        protected void onPostExecute() {

        }

    }

    private void drawResult(){

        ProposalCard card = new ProposalCard(context);
        card.setTitle(film.get("title"));
        Drawable d = new BitmapDrawable(this.getResources(),poster);
        card.setDrawable(d);
        card.setDismissible(false);
        card.setDescription("Titolo originale: "+film.get("original_title")+"\n"+film.get("plot"));
        card.setLeftButtonText("Left");
        card.setRightButtonText("Right");
        card.setOnLeftButtonPressedListener(new OnButtonPressListener() {
            @Override
            public void onButtonPressedListener(View view, Card card) {
                Toast.makeText(context, "left", Toast.LENGTH_SHORT).show();
            }
        });
        card.setOnRightButtonPressedListener( new OnButtonPressListener() {
            @Override
            public void onButtonPressedListener(View view, Card card) {
                Toast.makeText(context,"right",Toast.LENGTH_SHORT).show();
            }
        });
        details_material_list_view.add(card);


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

            drawResult();
        }

    }

}
