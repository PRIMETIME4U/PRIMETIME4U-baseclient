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

import com.dexafree.materialList.cards.model.Card;
import com.dexafree.materialList.controller.OnButtonPressListener;
import com.dexafree.materialList.view.MaterialListView;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import it.scripto.primetime4u.cards.MaterialProposalCardListAdapter;
import it.scripto.primetime4u.cards.ProposalCard;
import it.scripto.primetime4u.cards.WelcomeCard;
import it.scripto.primetime4u.model.Movie;
import it.scripto.primetime4u.model.Proposal;
import it.scripto.primetime4u.model.ServerResponse;
import it.scripto.primetime4u.utils.BaseFragment;
import it.scripto.primetime4u.utils.Utils;


public class ProposalFragment extends BaseFragment {

    private List<Movie> proposalList = new ArrayList<>();
    private ArrayList<ProposalCard> cardList = new ArrayList<>();
    private MaterialProposalCardListAdapter materialListViewAdapter;

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
        MaterialListView proposal_material_list_view = (MaterialListView) view.findViewById(R.id.proposal_material_list_view);
        // TODO add animation: proposal_material_list_view.setCardAnimation(IMaterialView.CardAnimation.SWING_BOTTOM_IN);

        // Get user_id
        MainActivity base = (MainActivity) this.getActivity();
        final String account = base.getAccount();
        preferences = getActivity().getPreferences(Context.MODE_PRIVATE);



        // welcome card scorso film, compare solo alla prima esecuzione, se e solo se ho un già un film da guardare
        if (preferences.contains("PENDING_MOVIE") && preferences.contains("PENDING_TITLE") && preferences.contains("TOBEANSWERED") && aDayIsPassed()) {
            final WelcomeCard welcomeCard = new WelcomeCard(context);
            editor = preferences.edit();
            welcomeCard.setFullWidthDivider(true);
            welcomeCard.setDividerVisible(true);
            welcomeCard.setTitle(getResources().getString(R.string.welcome_text));
            welcomeCard.setDescription(String.format(getResources().getString(R.string.feedback_text), preferences.getString("PENDING_TITLE","")));
            welcomeCard.setLeftButtonText(getString(R.string.no_text));
            welcomeCard.setRightButtonText(getString(R.string.yes_text));
            welcomeCard.setDismissible(false);
            welcomeCard.setOnLeftButtonPressedListener(new OnButtonPressListener() {
                @Override
                public void onButtonPressedListener(View view, Card card) {
                    Toast.makeText(context, "Peccato", Toast.LENGTH_SHORT).show();
                    //TODO: non è piaciuto
                    card.setDismissible(true);
                    card.dismiss();
                    editor.remove("TOBEANSWERED");
                    editor.remove("PENDING_MOVIE");
                    editor.remove("PENDING_TITLE");
                    editor.remove("PENDING_TIME");
                    editor.remove("TIME_HOUR");
                    editor.commit();
                }
            });
            welcomeCard.setOnRightButtonPressedListener(new OnButtonPressListener() {
                @Override
                public void onButtonPressedListener(View view, Card card) {
                    Toast.makeText(context, "L'hai guardato", Toast.LENGTH_SHORT).show();

                    /**
                     * Cosa fare: ricordo l'idIMDB e il titolo
                     *
                     *  http://hale-kite-786.appspot.com/api/watched/<id>

                     mettendo il json
                     {

                     "idIMDB":"id"
                     }
                     */
                    String lastMovieId = preferences.getString("PENDING_MOVIE","");
                    /**
                     * faccio add di questo ID ai watched dell'utente con HTTP POST
                     */
                    card.setDismissible(true);
                    card.dismiss();
                    editor.remove("TOBEANSWERED");
                    editor.remove("PENDING_MOVIE");
                    editor.remove("PENDING_TITLE");
                    editor.remove("PENDING_TIME");
                    editor.remove("TIME_HOUR");
                    editor.commit();
                    
                    // Generate URL
                    String url = Utils.SERVER_API + "watched/" + account;
                    
                    // Add watched movie
                    addWatched(url, lastMovieId);
                }
            });

            proposal_material_list_view.add(welcomeCard);
        }
        
        // Generate URL
        String url = Utils.SERVER_API + "proposal/" + account;
        
        // Get proposals
        get(url);

        // Create and set adapter
        materialListViewAdapter = new MaterialProposalCardListAdapter(getActivity());
        proposal_material_list_view.setAdapter(materialListViewAdapter);
        
        return view;
    }
    
    //
    private void parseResponse(ServerResponse.ProposalData response) {
        List<Proposal> proposals = response.proposal;
        
        Log.i(TAG, String.valueOf(proposals));

        for (Proposal proposal : proposals) {

            Movie movie = new Movie();
            movie.setOriginalTitle(proposal.getOriginalTitle());
            movie.setChannel(proposal.getChannel());
            movie.setTime(proposal.getTime());
            movie.setIdIMDB(proposal.getIdIMDB());
            movie.setPoster(proposal.getPoster());
            movie.setSimplePlot(proposal.getSimplePlot());

            proposalList.add(movie);
        }
 
        fillCardList();
    }

    /**
     *
     */
    private void fillCardList() {
        for (int i = 0; i < proposalList.size(); i++) {
            ProposalCard card = new ProposalCard(context);
            final Movie proposal = proposalList.get(i);

            final String originalTitle = proposal.getOriginalTitle();
            final String idIMDB = proposal.getIdIMDB();


            card.setTitle(originalTitle);
            card.setMovieInfoText(String.format(getResources().getString(R.string.movie_info_text), proposal.getChannel(), proposal.getTime()));

            final String info = card.getMovieInfoText();
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
                    Toast.makeText(context, "Guarderai " + originalTitle + " , buona visione!", Toast.LENGTH_SHORT).show();


                    preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
                    editor = preferences.edit();

                    if (!preferences.contains("PENDING_MOVIE")){
                        editor.putString("PENDING_MOVIE", idIMDB);
                        editor.putString("PENDING_TITLE", originalTitle);
                        editor.putString("TIME_HOUR",info);
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
                        editor.remove("TIME_HOUR");

                        editor.putString("PENDING_MOVIE", idIMDB);
                        editor.putString("PENDING_TITLE",originalTitle);
                        editor.putString("TOBEANSWERED","true");
                        editor.putString("TIME_HOUR",info);
                        //inizializzo il timer
                        Calendar c = Calendar.getInstance();
                        long day = c.getTimeInMillis();
                        editor.putLong("PENDING_TIME",day);

                        editor.commit();
                    }

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
            
            cardList.add(card);
        }

        materialListViewAdapter.addAll(cardList);
    }

    /**
     *
     */
    private void get(String url) {
        Ion.with(context)
                .load(url)
                .as(new TypeToken<ServerResponse.ProposalResponse>() {
                })
                .setCallback(new FutureCallback<ServerResponse.ProposalResponse>() {
                    @Override
                    public void onCompleted(Exception e, ServerResponse.ProposalResponse result) {
                        proposalList.clear();
                        cardList.clear();

                        parseResponse(result.data);
                    }
                });
    }

    private void addWatched(String url, String id) {
        JsonObject json = new JsonObject();
        json.addProperty("idIMDB", id);

        Ion.with(getActivity())
                .load("POST", url)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                    }
                });
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
        if (diff > 72000000) return true;
        else return false;

    }
    @Override
    public void onSaveInstanceState(Bundle toSave) {
        super.onSaveInstanceState(toSave);
        // TODO: save proposalList in order to reuse after
    }
}