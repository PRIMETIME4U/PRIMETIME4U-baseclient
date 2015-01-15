package it.scripto.primetime4u;

import android.content.Intent;
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



public class ProposalFragment extends BaseFragment {

    private MaterialListView proposal_material_list_view;

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

        MainActivity base = (MainActivity) this.getActivity();
        String account = base.getAccount();

        //welcome card scorso film, compare solo alla prima esecuzione
        final WelcomeCard welcomeCard = new WelcomeCard(context);
        welcomeCard.setFullWidthDivider(true);
        welcomeCard.setDividerVisible(true);
        //togliete il " + account" dal titolo, l'ho messo per provare le shared preferences
        welcomeCard.setTitle(getResources().getString(R.string.welcome_text) + account);
        welcomeCard.setDescription(String.format(getResources().getString(R.string.feedback_text), "The Blues Brothers"));
        welcomeCard.setLeftButtonText(getString(R.string.no_text));
        welcomeCard.setRightButtonText(getString(R.string.yes_text));
        welcomeCard.setDismissible(false);
        welcomeCard.setOnLeftButtonPressedListener(new OnButtonPressListener() {
            @Override
            public void onButtonPressedListener(View view, Card card) {
                Toast.makeText(context, "You pressed No", Toast.LENGTH_SHORT).show();
                //non è piaciuto il film scorso
                welcomeCard.setDismissible(true);
                welcomeCard.dismiss();
            }
        });
        welcomeCard.setOnRightButtonPressedListener(new OnButtonPressListener() {
            @Override
            public void onButtonPressedListener(View view, Card card) {
                Toast.makeText(context, "You pressed Yes", Toast.LENGTH_SHORT).show();
                //è piaciuto il film scorso
                welcomeCard.setDismissible(true);
                welcomeCard.dismiss();
            }
        });
        
        proposal_material_list_view.add(welcomeCard);

        String url = Utils.SERVER_API + "proposal/" + account;
        
        JsonObjectRequest proposalRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    
                    List<Movie> proposalList = new ArrayList<>();
                    
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());

                        try {
                            JSONObject data = response.getJSONObject("data");
                            JSONArray proposals = data.getJSONArray("proposal");

                            for (int i = 0; i < proposals.length(); i++) {
                                JSONObject proposalJSON = proposals.getJSONObject(i);

                                Movie proposal = new Movie();
                                proposal.setOriginalTitle(proposalJSON.getString("original_title"));
                                proposal.setChannel(proposalJSON.getString(("channel")));
                                proposal.setTime(proposalJSON.getString("time"));
                                proposal.setIdIMDB(proposalJSON.getString("id_IMDB"));
                                proposal.setPoster(proposalJSON.getString("poster"));
                                proposal.setSimplePlot(proposalJSON.getString("simple_plot"));

                                proposalList.add(proposal);
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, e.toString());
                            Toast.makeText(context, "Impossibile caricare la lista al momento, riprova tra poco", Toast.LENGTH_LONG).show();
                        }
                        
                        drawResult(proposalList);
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

    private void drawResult(List<Movie> proposalList) {
        for (int i = 0; i < proposalList.size(); i++) {
            ProposalCard card = new ProposalCard(context);
            Movie proposal = proposalList.get(i);

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
                    Toast.makeText(context, "You have pressed " + originalTitle, Toast.LENGTH_SHORT).show();
                    //TODO: Scelto film da mostrare
                }
            });

            card.setOnLeftButtonPressedListener(new OnButtonPressListener() {
                @Override
                public void onButtonPressedListener(View view, Card card) {
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra("ORIGINAL_TITLE", originalTitle);
                    intent.putExtra("ID_IMDB",idIMDB);
                    startActivity(intent);
                }
            });
            
            proposal_material_list_view.add(card);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle toSave) {
        super.onSaveInstanceState(toSave);

    }
}