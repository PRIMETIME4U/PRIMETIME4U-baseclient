package it.scripto.primetime4u;

import android.app.DownloadManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.JsonObjectRequest;
import com.dexafree.materialList.model.BasicButtonsCard;
import com.dexafree.materialList.controller.OnButtonPressListener;
import com.dexafree.materialList.model.BasicImageButtonsCard;
import com.dexafree.materialList.model.BigImageButtonsCard;
import com.dexafree.materialList.model.Card;
import com.dexafree.materialList.model.SmallImageCard;
import com.dexafree.materialList.view.MaterialListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;


import java.util.ArrayList;
import java.util.List;

import primetime4u.app.AppController;
import primetime4u.model.Movie;

public class ProposalFragment extends BaseFragment {

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment ProposalFragment.
     */
    //URL for free channels suggestions
    private static final String freeurl = "http://hale-kite-786.appspot.com/schedule/free/today";
    //list of movies
    private List<Movie> movieList;
    private MaterialListView proposal_material_list_view;

    public static ProposalFragment newInstance() {
        return new ProposalFragment();
    }

    public ProposalFragment() {
        // Required empty public constructor
    }

    @Override
    protected String getTagLog() {
        return "ProposalFragment";
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_proposal;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        //setting up the materiallistview and movie array
        proposal_material_list_view = (MaterialListView) view.findViewById(R.id.proposal_material_list_view);
        movieList = new ArrayList<Movie>();


        //asynchronous task for http request
        new JsonRequest().execute(freeurl);

        /*NOTE:
        Siccome usiamo a quanto pare due versioni diverse di materialList, ho riportato quella che avevo nel vecchio progetto
        La personalizzata "ProposalCard" genera errori di casting, c'è qualche problema nell'ereditarietà dalla BigImageButtonCard

        Se uso le BigImageButtonCard il titolo, visualizzato in bianco, non compare correttamente:
        - non compare se non ci sono immagini allegate
        - se uso un'immagine a fondo bianco, il titolo esce in bianco e se lungo, va troppo in alto e viene tagliato
        - lascio la prima card per farvi vedere cosa intendo


        Ho usato una BasicImageButtonCard per l'elenco dei film

        Giovanni - 16.05, 11.1.2015

        */

        BigImageButtonsCard card = new BigImageButtonsCard();
        card.setTitle("Your title");
        card.setBitmap(context,R.drawable.ic_launcher);
        card.setDescription("Your description");
        card.setLeftButtonText("Detail");
        card.setRightButtonText("I'll watch it");
        card.setDismissible(false);

        card.setOnRightButtonPressedListener(new OnButtonPressListener() {
            @Override
            public void onButtonPressedListener(TextView t) {
                Toast.makeText(context, "You have pressed the right button", Toast.LENGTH_SHORT).show();
            }
        });

        card.setOnLeftButtonPressedListener(new OnButtonPressListener() {
            @Override
            public void onButtonPressedListener(TextView t) {
                Toast.makeText(context, "You have pressed the left button", Toast.LENGTH_SHORT).show();

            }
        });

        proposal_material_list_view.add(card);

        return view;
    }

    private void drawResult() {
        //System.out.println("Size is: "+ movieList.size());
        for (int i = 0; i < movieList.size(); i++) {
            BasicImageButtonsCard currentcard = new BasicImageButtonsCard();
            Movie currentmovie = movieList.get(i);

            String title = currentmovie.getTitle();


            if (!currentmovie.getOriginalTitle().equals("null"))
                currentcard.setDescription("Titolo originale: " + currentmovie.getOriginalTitle() + "\n" + currentmovie.getTime() + "\n" + currentmovie.getChannel());
            else
                currentcard.setDescription(currentmovie.getTime() + "\n" + currentmovie.getChannel());

            currentcard.setTitle(title);
            currentcard.setBitmap(context,R.drawable.ic_launcher);
            currentcard.setLeftButtonText("Detail");
            currentcard.setRightButtonText("I'll watch it");
            currentcard.setDismissible(false);
            currentcard.setOnRightButtonPressedListener(new OnButtonPressListener() {
                @Override
                public void onButtonPressedListener(TextView t) {
                    Toast.makeText(context, "You have pressed the right button", Toast.LENGTH_SHORT).show();
                }
            });

            currentcard.setOnLeftButtonPressedListener(new OnButtonPressListener() {
                @Override
                public void onButtonPressedListener(TextView t) {
                    Toast.makeText(context, "You have pressed the left button", Toast.LENGTH_SHORT).show();

                }
            });
            proposal_material_list_view.add(currentcard);

        }
    }


    class JsonRequest extends AsyncTask<String, Void, List<Movie>> {

        //asynchronous task, http request and json parsing by using volley library

        List<Movie> movieList2 = new ArrayList<Movie>();

        @Override
        protected List<Movie> doInBackground(String... params) {


            final String TAG = ProposalFragment.class.getSimpleName();

            JsonObjectRequest movieReq = new JsonObjectRequest(Request.Method.GET, freeurl, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, response.toString());
                            System.out.println("I'm in the asynctask here2");

                            try {

                                JSONObject obj = response.getJSONObject("data");
                                JSONArray obj2 = obj.getJSONArray("schedule");
                                for (int j = 0; j < obj2.length(); j++) {
                                    JSONObject obj3 = obj2.getJSONObject(j);
                                    Movie movie = new Movie();
                                    movie.setTitle(obj3.getString("title"));
                                    movie.setOriginalTitle(obj3.getString("originalTitle"));
                                    movie.setChannel(obj3.getString(("channel")));
                                    movie.setTime(obj3.getString("time"));
                                    movieList2.add(movie);

                                }

                                movieList = movieList2;

                                drawResult();
                                //movie.setRating(((Number) obj.get("rating"))
                                //        .doubleValue());
                                //movie.setYear(obj.getInt("releaseYear"));

                                // Genre is json array
                                /*JSONArray genreArry = obj.getJSONArray("genre");
                                ArrayList<String> genre = new ArrayList<String>();
                                for (int j = 0; j < genreArry.length(); j++) {
                                    genre.add((String) genreArry.get(j));
                                }
                                movie.setGenre(genre);*/

                                // adding movie to movies array


                            } catch (JSONException e) {
                                e.printStackTrace();

                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());

                }
            });

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(movieReq);

            return movieList2;
        }

        protected void onPostExecute(List<Movie> movies) {

        }

    }
}