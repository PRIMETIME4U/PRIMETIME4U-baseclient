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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import primetime4u.app.AppController;
import primetime4u.model.Movie;
import primetime4u.util.Utils;

public class TastesFragment extends BaseFragment {

    private MaterialListView tastes_material_list_view;
    private List<Movie> tastesList = new ArrayList<>();
    private String account;

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

        // Setting up material list
        tastes_material_list_view = (MaterialListView) view.findViewById(R.id.tastes_material_list_view);
        
        // Get user_id
        MainActivity base = (MainActivity) this.getActivity();
        account = base.getAccount();
        
        // Generate URL
        String url = Utils.SERVER_API + "tastes/" + account + "/movie";
        
        // Get tastes
        get(url);
        
        return view;
    }
    
    /**
     *
     */
    private void parseResponse(JSONObject response) {
        try {
            JSONObject data = response.getJSONObject("data");
            JSONArray tastes = data.getJSONArray("tastes");

            for (int i = 0; i < tastes.length(); i++) {
                JSONObject tasteJSON = tastes.getJSONObject(i);

                Movie proposal = new Movie();
                proposal.setOriginalTitle(tasteJSON.getString("original_title"));
                proposal.setIdIMDB(tasteJSON.getString("id_IMDB"));
                proposal.setPoster(tasteJSON.getString("poster"));

                tastesList.add(proposal);
            }
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }
    }
    
    /**
     *
     */
    private void drawResult() {
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
                        String url = Utils.SERVER_API + "tastes/" + account + "/movie/" + taste.getIdIMDB();
                        deleteTaste(url);
                    }
                }
            });
            
            tastes_material_list_view.add(movieCard);
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

                        parseResponse(response);

                        drawResult();
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
     *
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

                        tastesList.clear();

                        parseResponse(response);

                        drawResult();
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
