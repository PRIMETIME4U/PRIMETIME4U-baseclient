package it.scripto.primetime4u;

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
import com.dexafree.materialList.view.MaterialStaggeredGridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import primetime4u.app.AppController;
import primetime4u.model.Movie;
import primetime4u.util.Utils;
import primetime4u.model.Artist;

public class TastesFragment extends BaseFragment {

    private MaterialStaggeredGridView tastes_material_list_view;
    private List<Movie> tastesList = new ArrayList<>();
    private List<Artist> tastesListArtist = new ArrayList<>();
    private String account;

    private LayoutInflater inflater;
    private ViewGroup container;
    private Bundle savedInstanceState;

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment TastesFragment.
     */
    public static TastesFragment newInstance() {
        return new TastesFragment();
    }

    public TastesFragment() {
        // Required empty public constructor
    }

    @Override
    protected String getTagLog() {
        return "TastesFragment";
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_tastes;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        //i need to save these info
        inflater=inflater;
        container = container;
        savedInstanceState = savedInstanceState;
        // Setting up material list
        tastes_material_list_view = (MaterialStaggeredGridView) view.findViewById(R.id.tastes_material_grid_view);
        
        // Get user_id
        MainActivity base = (MainActivity) this.getActivity();
        account = base.getAccount();

        // Generate URL
        String url = Utils.SERVER_API + "tastes/" + account + "/all";

        // Get tastes
        get(url);
        
        return view;
    }
    
    /**
     *
     */
    private void parseResponse(JSONObject response) {
        try {
            //il get lo faccio sempre da ALL
            JSONObject data = response.getJSONObject("data");
            JSONObject tastes = data.getJSONObject("tastes");
            JSONArray movies = tastes.getJSONArray("movies");
            for (int i = 0; i < movies.length(); i++) {
                JSONObject tasteJSON = movies.getJSONObject(i);

                Movie proposal = new Movie();
                proposal.setOriginalTitle(tasteJSON.getString("originalTitle"));
                proposal.setIdIMDB(tasteJSON.getString("idIMDB"));
                proposal.setPoster(tasteJSON.getString("poster"));

                tastesList.add(proposal);
            }
            //fare il for per artists e creare una tasteslist per movies
            JSONArray artists = tastes.getJSONArray("artists");
            for (int i=0;i < artists.length();i++){
                JSONObject tasteJSON = artists.getJSONObject(i);

                Artist proposal = new Artist();
                proposal.setName(tasteJSON.getString("name"));
                proposal.setIdIMDB(tasteJSON.getString("idIMDB"));
                proposal.setPoster(tasteJSON.getString("photo"));

                tastesListArtist.add(proposal);
            }

        } catch (JSONException e) {
            Log.e(TAG, e.toString());
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }

        drawResult();
    }
    
    /**
     *
     */
    private void drawResult() {
        //draw movies
        for (int i = 0; i < tastesList.size(); i++) {
            final Movie taste = tastesList.get(i);
            final TasteCard movieCard = new TasteCard(context);
            movieCard.setTitle(taste.getOriginalTitle());
            movieCard.setTaste(true);
            movieCard.setDismissible(false);
            movieCard.setType(TasteCard.MOVIE_TYPE);
            movieCard.setPoster(taste.getPoster());
            movieCard.setOnTasteButtonPressedListener(new OnButtonPressListener() {
                @Override
                public void onButtonPressedListener(View view, Card card) {
                    if (movieCard.getTaste()) {



                    } else {

                        card.dismiss();
                        Toast.makeText(context,"Elemento rimosso dalla tua lista di gusti, attendi...",Toast.LENGTH_LONG).show();
                        String url = Utils.SERVER_API + "tastes/" + account + "/movie/" + taste.getIdIMDB();
                        deleteTaste(url);

                    }
                }
            });

            tastes_material_list_view.add(movieCard);
        }
        //draw artists
        for (int i = 0; i < tastesListArtist.size(); i++) {
            final Artist taste = tastesListArtist.get(i);
            final TasteCard artistCard = new TasteCard(context);
            artistCard.setTitle(taste.getName());
            artistCard.setTaste(true);
            artistCard.setDismissible(false);
            artistCard.setType(TasteCard.ARTIST_TYPE);
            artistCard.setPoster(taste.getPoster());
            artistCard.setOnTasteButtonPressedListener(new OnButtonPressListener() {
                @Override
                public void onButtonPressedListener(View view, Card card) {
                    if (artistCard.getTaste()) {



                    } else {

                        card.dismiss();
                        Toast.makeText(context,"Elemento rimosso dalla tua lista di gusti, attendi...",Toast.LENGTH_LONG).show();
                        String url = Utils.SERVER_API + "tastes/" + account + "/artist/" + taste.getIdIMDB();
                        deleteTaste(url);

                    }
                }
            });

            tastes_material_list_view.add(artistCard);
        }


    }

    /**
     *
     */
    private void get(String url) {


        JsonObjectRequest proposalRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        tastesList.clear();
                        tastesListArtist.clear();
                        parseResponse(response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                    }
                }
        );

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(proposalRequest);
    }

    /**
     *  cancella gusti
     */
    private void deleteTaste(String url) {
        JsonObjectRequest tasteDelete = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        tastesListArtist.clear();
                        tastesList.clear();
                        MainActivity base = (MainActivity) getActivity();
                        base.refreshTastes();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e(TAG, "Error: " + error.getMessage());
                    }
                }
        );

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(tasteDelete);

    }

    @Override
    public void onSaveInstanceState(Bundle toSave) {
        super.onSaveInstanceState(toSave);
        // TODO: save tastesList in order to reuse after
    }
}
