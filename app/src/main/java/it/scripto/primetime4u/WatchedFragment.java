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

public class WatchedFragment extends BaseFragment {

    private MaterialListView watched_material_list_view;

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

        watched_material_list_view = (MaterialListView) view.findViewById(R.id.watched_material_list_view);

        MainActivity base = (MainActivity) this.getActivity();
        String account = base.getAccount();

        String url = Utils.SERVER_API + "watched/" + account;

        JsonObjectRequest proposalRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    List<Movie> watchedList = new ArrayList<>();
                    List<String> dateList = new ArrayList<>();

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());

                        try {
                            JSONObject data = response.getJSONObject("data");
                            JSONArray watcheds = data.getJSONArray("watched");

                            for (int i = 0; i < watcheds.length(); i++) {
                                JSONObject watchedJSON = watcheds.getJSONObject(i);

                                Movie watched = new Movie();
                                watched.setOriginalTitle(watchedJSON.getString("original_title"));
                                watched.setIdIMDB(watchedJSON.getString("id_IMDB"));
                                watched.setPoster(watchedJSON.getString("poster"));
                                
                                watchedList.add(watched);
                                dateList.add(watchedJSON.getString("date"));
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, e.toString());
                            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                        }

                        drawResult(watchedList, dateList);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(proposalRequest);
        return view;
    }

    private void drawResult(List<Movie> watchedList, List<String> dateList) {
        for (int i = 0; i < watchedList.size(); i++) {
            final Movie watched = watchedList.get(i);

            final WatchedCard watchedCard = new WatchedCard(context);
            watchedCard.setTitle(watched.getOriginalTitle());
            watchedCard.setDate(dateList.get(i));
            watchedCard.setDismissible(false);
            watchedCard.setDrawable(R.drawable.ic_launcher);
            watchedCard.setOnTasteButtonPressedListener(new OnButtonPressListener() {
                @Override
                public void onButtonPressedListener(View view, Card card) {
                    String toastText = watchedCard.getTaste() ? "Me gusta" : "Me disgusta";
                    Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
                }
            });

            watched_material_list_view.add(watchedCard);
        }
    }

}
