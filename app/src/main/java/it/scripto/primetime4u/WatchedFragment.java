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
import java.util.HashMap;
import java.util.List;

import primetime4u.app.AppController;
import primetime4u.model.Movie;
import primetime4u.util.Utils;

public class WatchedFragment extends BaseFragment {

    private MaterialListView watched_material_list_view;
    private List<Movie> watchedList = new ArrayList<>();
    private List<String> dateList = new ArrayList<>();
    private List<Integer> tasteList = new ArrayList<>();
    private String account;

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment WatchedFragment.
     */
    public static WatchedFragment newInstance() {
        return new WatchedFragment();
    }

    public WatchedFragment() {
        // Required empty public constructor
    }

    @Override
    protected String getTagLog() {
        return "WatchedFragment";
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_watched;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Setting up material list
        watched_material_list_view = (MaterialListView) view.findViewById(R.id.watched_material_list_view);

        // Get user_id
        MainActivity base = (MainActivity) this.getActivity();
        account = base.getAccount();

        // Generate URL
        String url = Utils.SERVER_API + "watched/" + account;

        // Get watcheds
        get(url);
        
        return view;
    }

    /**
     *
     */
    private void parseResponse(JSONObject response) {
        try {
            JSONObject data = response.getJSONObject("data");
            JSONArray watcheds = data.getJSONArray("watched");

            for (int i = 0; i < watcheds.length(); i++) {
                JSONObject watchedJSON = watcheds.getJSONObject(i);

                Movie watched = new Movie();
                watched.setOriginalTitle(watchedJSON.getString("originalTitle"));
                watched.setIdIMDB(watchedJSON.getString("idIMDB"));
                watched.setPoster(watchedJSON.getString("poster"));

                watchedList.add(watched);
                dateList.add(watchedJSON.getString("date"));
                tasteList.add(watchedJSON.getInt("tasted"));
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
        for (int i = 0; i < watchedList.size(); i++) {
            final Movie watched = watchedList.get(i);

            final WatchedCard watchedCard = new WatchedCard(context);
            watchedCard.setTitle(watched.getOriginalTitle());
            watchedCard.setDate(dateList.get(i));
            watchedCard.setTaste(tasteList.get(i) == 1);
            watchedCard.setDismissible(false);
            watchedCard.setPoster(watched.getPoster());
            watchedCard.setOnTasteButtonPressedListener(new OnButtonPressListener() {
                @Override
                public void onButtonPressedListener(View view, Card card) {
                    if (watchedCard.getTaste()) {
                        String url = Utils.SERVER_API + "tastes/" + account + "/movie";
                        Log.i(TAG, url);
                        addTaste(url, watched.getIdIMDB());
                    } else {
                        String url = Utils.SERVER_API + "tastes/" + account + "/movie/" + watched.getIdIMDB();
                        deleteTaste(url);
                    }
                }
            });

            watched_material_list_view.add(watchedCard);
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

                        watchedList.clear();
                        dateList.clear();
                        tasteList.clear();

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
        AppController.getInstance().addToRequestQueue(proposalRequest);
    }
    /**
     *
     */
    private void addTaste(String url, final String id) {
        
        HashMap<String, String> params = new HashMap<>();
        params.put("idIMDB", id);
        
        JsonObjectRequest tasteAdd = new JsonObjectRequest(
                Request.Method.POST,
                url,
                new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        // TODO: manage response (update tastesFragment)
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
        AppController.getInstance().addToRequestQueue(tasteAdd);
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
                        // TODO: manage response (update tastesFragment)
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
        // TODO: save watchedsList in order to reuse after
    }
}
