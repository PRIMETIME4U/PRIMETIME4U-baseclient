package it.scripto.primetime4u;

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
    //list of movies
    private List<Movie> movieList;
    private MaterialListView proposal_material_list_view;
    boolean first=true;

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
        movieList = new ArrayList<Movie>();

        final ProposalCard card = new ProposalCard(context);
        card.setTitle("PRIMETIME4U");
        card.setDescription("Scegli tra free tv e pay-tv per vedere i suggerimenti di oggi");
        card.setLeftButtonText("Free");
        card.setRightButtonText("Pay-tv");
        card.setDismissible(false);

        card.setOnRightButtonPressedListener(new OnButtonPressListener() {
            @Override
            public void onButtonPressedListener(View view, Card card) {
                Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show();

                if (!first) emptyList(proposal_material_list_view,card);
                movieList = new ArrayList<Movie>();
                first=false;
                new JsonRequest().execute(skyurl);
            }
        });

        card.setOnLeftButtonPressedListener(new OnButtonPressListener() {
            @Override
            public void onButtonPressedListener(View view, Card card) {
                Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show();

                if (!first) emptyList(proposal_material_list_view,card);
                movieList = new ArrayList<Movie>();
                first=false;
                new JsonRequest().execute(freeurl);
            }
        });

        proposal_material_list_view.add(card);

        return view;
    }
    private void emptyList(MaterialListView list, Card starting){

        list.removeAllViewsInLayout();
        list.getAdapter().clear();
        list.add(starting);
    }
    private void drawResult() {
        //System.out.println("Size is: "+ movieList.size());
        for (int i = 0; i < movieList.size(); i++) {
            final ProposalCard currentcard = new ProposalCard(context);
            Movie currentmovie = movieList.get(i);

            String title = currentmovie.getTitle();
            
            if (!currentmovie.getOriginalTitle().equals("null"))
                currentcard.setDescription("Titolo originale: " + currentmovie.getOriginalTitle() + "\n" + currentmovie.getTime() + "\n" + currentmovie.getChannel());
            else
                currentcard.setDescription(currentmovie.getTime() + "\n" + currentmovie.getChannel());
            
            currentcard.setMovieInfoText("Channel: " + currentmovie.getChannel() + " Time: " + currentmovie.getTime());
            
            currentcard.setTitle(title);
            currentcard.setDrawable(R.drawable.ic_launcher);
            currentcard.setFullWidthDivider(true);
            currentcard.setDividerVisible(true);
            currentcard.setLeftButtonText("Detail");
            currentcard.setRightButtonText("I'll watch it");
            currentcard.setDismissible(false);
            currentcard.setOnRightButtonPressedListener(new OnButtonPressListener() {
                @Override
                public void onButtonPressedListener(View view, Card card) {
                    Toast.makeText(context, "You have pressed " + currentcard.getTitle(), Toast.LENGTH_SHORT).show();

                }
            });

            currentcard.setOnLeftButtonPressedListener(new OnButtonPressListener() {
                @Override
                public void onButtonPressedListener(View view, Card card) {
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