package it.scripto.primetime4u;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dexafree.materialList.cards.model.Card;
import com.dexafree.materialList.controller.OnButtonPressListener;
import com.dexafree.materialList.view.IMaterialView;
import com.dexafree.materialList.view.MaterialListView;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import primetime4u.app.AppController;
import primetime4u.model.Movie;



public class ProposalFragment extends BaseFragment {

    

    //URL for free and paytv channels suggestions
    private static final String freeurl = "http://hale-kite-786.appspot.com/api/schedule/free/today";
    private static final String skyurl = "http://hale-kite-786.appspot.com/api/schedule/sky/today";
    private static String proposalurl = "http://hale-kite-786.appspot.com/api/proposal/";
    //list of movies
    private List<Movie> movieList;
    private MaterialListView proposal_material_list_view;
    boolean first=true;  //mi serve per controllare se la lista film è stata riempita già prima

    private WelcomeCard card = null;
    private WelcomeCard cardLastMovie = null;

    //mi serve per ricordare l'ultima scelt fatta dall'utente, se free o sky
    private String lastChoosen = "";

    //mi serve per controllare se è il primo accesso al fragment da parte dell'utente
    private boolean firstExec = true;


    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment ProposalFragment.
     */
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
        /**
         * PER ORA NIENTE ANIMAZIONI DI CARD, PROBLEMI NELLE LIBRERIE
         * proposal_material_list_view.setCardAnimation(IMaterialView.CardAnimation.SWING_BOTTOM_IN);
         */


        movieList = new ArrayList<Movie>();

        MainActivity base = (MainActivity) this.getActivity();
        String account = base.getAccount();
        proposalurl = proposalurl + account;

        //welcome card scorso film, compare solo alla prima esecuzione
        if (firstExec) {
            cardLastMovie = new WelcomeCard(context);
            cardLastMovie.setFullWidthDivider(true);
            cardLastMovie.setDividerVisible(true);
            cardLastMovie.setTitle(getResources().getString(R.string.welcome_text)+" "+account );
            cardLastMovie.setDescription(String.format(getResources().getString(R.string.feedback_text), "The Blues Brothers"));
            cardLastMovie.setLeftButtonText(getString(R.string.no_text));
            cardLastMovie.setRightButtonText(getString(R.string.yes_text));
            cardLastMovie.setDismissible(false);
            cardLastMovie.setOnLeftButtonPressedListener(new OnButtonPressListener() {
                @Override
                public void onButtonPressedListener(View view, Card card) {
                    Toast.makeText(context, "You pressed No", Toast.LENGTH_SHORT).show();
                    //non è piaciuto il film scorso
                    cardLastMovie.setDismissible(true);
                    cardLastMovie.dismiss();
                    firstExec=false;
                }
            });
            cardLastMovie.setOnRightButtonPressedListener(new OnButtonPressListener() {
                @Override
                public void onButtonPressedListener(View view, Card card) {
                    Toast.makeText(context, "You pressed Yes", Toast.LENGTH_SHORT).show();
                    //è piaciuto il film scorso
                    cardLastMovie.setDismissible(true);
                    cardLastMovie.dismiss();
                    firstExec=false;
                }
            });
        }

        //TODO: asyncTask come in DetailsFragment, ottengo info film in una ProposalCard, e lancio DetailsActivity se premo su tasto dettagli
        //dopo i suggerimenti, metto la card per proporre altri film!



        card = new WelcomeCard(context);
        card.setDescription("Per vedere gli altri film in programmazione, clicca su Free o Pay-tv");
        card.setFullWidthDivider(true);
        card.setDividerVisible(true);
        card.setLeftButtonText("Free");
        card.setRightButtonText("Pay-tv");
        card.setDismissible(false);

        card.setOnRightButtonPressedListener(new OnButtonPressListener() {
            @Override
            public void onButtonPressedListener(View view, Card card) {
                Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show();

                if (!first) emptyList(proposal_material_list_view,card,cardLastMovie,firstExec);
                movieList = new ArrayList<Movie>();
                first=false;
                lastChoosen="sky";
                new JsonRequest().execute(skyurl);
            }
        });

        card.setOnLeftButtonPressedListener(new OnButtonPressListener() {
            @Override
            public void onButtonPressedListener(View view, Card card) {
                Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show();

                if (!first) emptyList(proposal_material_list_view,card,cardLastMovie,firstExec);
                movieList = new ArrayList<Movie>();
                first=false;
                lastChoosen="free";
                new JsonRequest().execute(freeurl);
            }
        });

        if (firstExec) proposal_material_list_view.add(cardLastMovie);
        proposal_material_list_view.add(card);


        if (!first && movieList.isEmpty()){
            //se avevo una lista prima, devo rimetterla
            Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show();
            if (lastChoosen.equals("free")) new JsonRequest().execute(freeurl);
            if (lastChoosen.equals("sky")) new JsonRequest().execute(skyurl);
        }
        if (!first && !movieList.isEmpty()){
            drawResult();
        }


        return view;
    }
    private void emptyList(MaterialListView list, Card starting1, Card starting2, boolean b){
        //se non abbiamo ancora risposto al "ti è piaciuto il film di ieri", non dobbiamo togliere la card
        list.removeAllViewsInLayout();
        list.getAdapter().clear();
        if (b) {
            list.add(starting2);
            list.add(starting1);
        }
        else list.add(starting1);

    }

    private void drawResult() {
        //System.out.println("Size is: "+ movieList.size());
        for (int i = 0; i < movieList.size(); i++) {
            ProposalCard currentcard = new ProposalCard(context);
            Movie currentmovie = movieList.get(i);

            final String title = currentmovie.getTitle();
            
            if (!currentmovie.getOriginalTitle().equals("null"))
                currentcard.setDescription("Titolo originale: " + currentmovie.getOriginalTitle() + "\n" + currentmovie.getTime() + "\n" + currentmovie.getChannel());
            else
                currentcard.setDescription(currentmovie.getTime() + "\n" + currentmovie.getChannel());
            
            currentcard.setMovieInfoText(String.format(getResources().getString(R.string.movie_info_text), currentmovie.getChannel(), currentmovie.getTime()));
            
            currentcard.setTitle(title);
            currentcard.setDrawable(R.drawable.ic_launcher);
            currentcard.setFullWidthDivider(true);
            currentcard.setDividerVisible(true);
            currentcard.setLeftButtonText(getResources().getString(R.string.detail_text));
            currentcard.setRightButtonText(getResources().getString(R.string.watch_text));
            currentcard.setDismissible(false);
            currentcard.setOnRightButtonPressedListener(new OnButtonPressListener() {
                @Override
                public void onButtonPressedListener(View view, Card card) {
                    Toast.makeText(context, "You have pressed " + title, Toast.LENGTH_SHORT).show();
                    //TODO: Scelto film da mostrare
                }
            });

            currentcard.setOnLeftButtonPressedListener(new OnButtonPressListener() {
                @Override
                public void onButtonPressedListener(View view, Card card) {
                    //Toast.makeText(context, "You have pressed the left button", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(context,DetailsActivity.class);
                    i.putExtra("film",title);
                    startActivity(i);
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

            JsonObjectRequest movieReq = new JsonObjectRequest(Request.Method.GET, params[0], null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, response.toString());


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
                                //TODO:mettere drawresult nel onpostexecute
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
                                Toast.makeText(context,"Impossibile caricare la lista al momento, riprova tra poco",Toast.LENGTH_LONG).show();

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

    @Override
    public void onSaveInstanceState(Bundle toSave) {
        super.onSaveInstanceState(toSave);

    }

    class ProposalTask extends AsyncTask<String,Void,Void>{

        @Override
        protected Void doInBackground(String... params) {

            /**RISPOSTA JSON
             * {
             "code": 0,
             "data": {
             "proposal": [
             {
             "channel": "Rai Movie",
             "id_IMDB": "tt0240890",
             "original_title": "Serendipity",
             "poster": "http://ia.media-imdb.com/images/M/MV5BMTkzMjEzOTQ3Nl5BMl5BanBnXkFtZTYwMjI1NzU5._V1_SY317_CR4,0,214,317_AL_.jpg",
             "simple_plot": "A couple reunite years after the night they first met, fell in love, and separated, convinced that one day they'd end up together.",
             "time": "21:15"
             },
             {
             "channel": "Iris",
             "id_IMDB": "tt0146309",
             "original_title": "Thirteen Days",
             "poster": "http://ia.media-imdb.com/images/M/MV5BMTkwMTkxNTYyM15BMl5BanBnXkFtZTYwOTc5NTk2._V1_SY317_CR1,0,214,317_AL_.jpg",
             "simple_plot": "A dramatization of President Kennedy's administration's struggle to contain the Cuban Missile Crisis in October of 1962.",
             "time": "21:00"
             },
             {
             "channel": "Rai Movie",
             "id_IMDB": "tt0240890",
             "original_title": "Serendipity",
             "poster": "http://ia.media-imdb.com/images/M/MV5BMTkzMjEzOTQ3Nl5BMl5BanBnXkFtZTYwMjI1NzU5._V1_SY317_CR4,0,214,317_AL_.jpg",
             "simple_plot": "A couple reunite years after the night they first met, fell in love, and separated, convinced that one day they'd end up together.",
             "time": "21:15"
             }
             ],
             "user_id": "gc240790@gmail.com"
             }
             }
             */
            return null;
        }
    }
}