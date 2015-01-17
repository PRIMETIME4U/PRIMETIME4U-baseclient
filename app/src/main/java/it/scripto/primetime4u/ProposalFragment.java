package it.scripto.primetime4u;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.Calendar;
import java.util.List;

import primetime4u.app.AppController;
import primetime4u.model.Movie;
import primetime4u.util.Utils;


public class ProposalFragment extends BaseFragment {

    private MaterialListView proposal_material_list_view;
    private List<Movie> proposalList = new ArrayList<>();
    private String account;


    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

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

        // Setting up material list
        proposal_material_list_view = (MaterialListView) view.findViewById(R.id.proposal_material_list_view);
        // TODO add animation: proposal_material_list_view.setCardAnimation(IMaterialView.CardAnimation.SWING_BOTTOM_IN);

        // Get user_id
        MainActivity base = (MainActivity) this.getActivity();
        account = base.getAccount();
        final String account = base.getAccount();
        preferences = getActivity().getPreferences(Context.MODE_PRIVATE);

        // welcome card scorso film, compare solo alla prima esecuzione, se e solo se ho un già un film da guardare
        if (preferences.contains("PENDING_MOVIE") && preferences.contains("PENDING_TITLE") && preferences.contains("TOBEANSWERED") && aDayIsPassed()) {
            final WelcomeCard welcomeCard = new WelcomeCard(context);
            editor = preferences.edit();
            welcomeCard.setFullWidthDivider(true);
            welcomeCard.setDividerVisible(true);
            welcomeCard.setTitle(getResources().getString(R.string.welcome_text));
            welcomeCard.setDescription(String.format(getResources().getString(R.string.feedback_text), preferences.getString("PENDING_TITLE", "")));
            welcomeCard.setLeftButtonText(getString(R.string.no_text));
            welcomeCard.setRightButtonText(getString(R.string.yes_text));
            welcomeCard.setDismissible(false);
            welcomeCard.setOnLeftButtonPressedListener(new OnButtonPressListener() {
                @Override
                public void onButtonPressedListener(View view, Card card) {

                    /**
                     * FILM NON GUARDATO
                     */
                    card.setDismissible(true);
                    card.dismiss();
                    editor.remove("TOBEANSWERED");
                    editor.commit();
                }
            });
            welcomeCard.setOnRightButtonPressedListener(new OnButtonPressListener() {
                @Override
                public void onButtonPressedListener(View view, Card card) {
                    /**
                     * FILM GUARDATO
                     */

                    String lastMovieId = preferences.getString("PENDING_MOVIE", "");
                    card.setDismissible(true);
                    card.dismiss();
                    editor.remove("TOBEANSWERED");
                    editor.commit();
                    String s = Utils.SERVER_API + "watched/" + account;
                    addWatched(s, lastMovieId);
                }
            });

            proposal_material_list_view.add(welcomeCard);
        }
        String url = Utils.SERVER_API + "proposal/" + account;
        
        // Get proposals
        get(url);
        
        return view;
    }

    private void parseResponse(JSONObject response) {
        try {
            JSONObject data = response.getJSONObject("data");
            JSONArray proposals = data.getJSONArray("proposal");

            for (int i = 0; i < proposals.length(); i++) {
                JSONObject proposalJSON = proposals.getJSONObject(i);

                Movie proposal = new Movie();
                proposal.setOriginalTitle(proposalJSON.getString("originalTitle"));
                proposal.setChannel(proposalJSON.getString(("channel")));
                proposal.setTime(proposalJSON.getString("time"));
                proposal.setIdIMDB(proposalJSON.getString("idIMDB"));
                proposal.setPoster(proposalJSON.getString("poster"));
                proposal.setSimplePlot(proposalJSON.getString("simplePlot"));

                proposalList.add(proposal);
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
        for (int i = 0; i < proposalList.size(); i++) {
            ProposalCard card = new ProposalCard(context);
            final Movie proposal = proposalList.get(i);

            final String originalTitle = proposal.getOriginalTitle();
            final String idIMDB = proposal.getIdIMDB();

            card.setTitle(originalTitle);
            card.setMovieInfoText(String.format(getResources().getString(R.string.movie_info_text), proposal.getChannel(), proposal.getTime()));
            card.setDescription(proposal.getSimplePlot());
            card.setPoster(proposal.getPoster());

            card.setFullWidthDivider(true);
            card.setDividerVisible(true);
            card.setDismissible(false);

            card.setLeftButtonText(getResources().getString(R.string.detail_text));
            card.setRightButtonText(getResources().getString(R.string.watch_text));

            card.setOnRightButtonPressedListener(new OnButtonPressListener() {
                @Override
                public void onButtonPressedListener(View view, Card card) {
                    Toast.makeText(context, "Hai scelto " + originalTitle + " , buona visione!", Toast.LENGTH_SHORT).show();


                    preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
                    editor = preferences.edit();

                    if (!preferences.contains("PENDING_MOVIE")){
                        editor.putString("PENDING_MOVIE", idIMDB);
                        editor.putString("PENDING_TITLE", originalTitle);
                        editor.putString("TOBEANSWERED","true");
                        //inizializzo il timer
                        Calendar c = Calendar.getInstance();
                        long day = c.getTimeInMillis();
                        editor.putLong("PENDING_TIME",day);

                        editor.commit();
                    }
                    else{
                        editor.remove("PENDING_MOVIE");
                        editor.remove("PENDING_TITLE");
                        editor.remove("PENDING_TIME");

                        editor.putString("PENDING_MOVIE", idIMDB);
                        editor.putString("PENDING_TITLE",originalTitle);
                        editor.putString("TOBEANSWERED","true");
                        //inizializzo il timer
                        Calendar c = Calendar.getInstance();
                        long day = c.getTimeInMillis();
                        editor.putLong("PENDING_TIME",day);

                        editor.commit();
                    }
                    /**
                     * TODO: some code here to change the card status into a "selected" movie
                     */
                }
            });

            card.setOnLeftButtonPressedListener(new OnButtonPressListener() {
                @Override
                public void onButtonPressedListener(View view, Card card) {
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra(DetailActivity.EXTRA_ID_IMDB, proposal.getIdIMDB());
                    intent.putExtra(DetailActivity.EXTRA_ORIGINAL_TITLE, proposal.getOriginalTitle());
                    intent.putExtra(DetailActivity.EXTRA_CHANNEL, proposal.getChannel());
                    intent.putExtra(DetailActivity.EXTRA_TIME, proposal.getTime());
                    startActivity(intent);
                }
            });
            
            proposal_material_list_view.add(card);
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

                        proposalList.clear();

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

    private void addWatched(String url,String id){
        /**
         * Creates a new request.
         * @param method the HTTP method to use
         * @param url URL to fetch the JSON from
         * @param jsonRequest A {@link org.json.JSONObject} to post with the request. Null is allowed and
         *   indicates no parameters will be posted along with request.
         * @param listener Listener to receive the JSON response
         * @param errorListener Error listener, or null to ignore errors.
         */
        JSONObject toBePosted = new JSONObject();
        try{
            toBePosted.put("idIMDB",id);
        }
        catch (JSONException e){
            Log.e(TAG, e.toString());
            Toast.makeText(context, "JSON Exception in post request", Toast.LENGTH_LONG).show();
        }

        JsonObjectRequest postRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                toBePosted,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Toast.makeText(context,"Film aggiunto nella lista dei guardati",Toast.LENGTH_LONG).show();

                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(context,"Errore nell'aggiunta del film",Toast.LENGTH_LONG).show();
                    }
                }
        );

        AppController.getInstance().addToRequestQueue(postRequest);
    }

    private boolean aDayIsPassed(){
        preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        //inizializzo il timer
        Calendar c = Calendar.getInstance();
        long now = c.getTimeInMillis();
        long yday = preferences.getLong("PENDING_TIME",0);
        long diff = now - yday;
        //possiamo far comparire la card dopo la giornata: 86 400 000
        //TEST: uso 10 minuti, 600 000, test OK
        if (diff > 86400000) return true;
        else return false;

    }
    @Override
    public void onSaveInstanceState(Bundle toSave) {
        super.onSaveInstanceState(toSave);
        // TODO: save proposalList in order to reuse after
    }
}