package it.scripto.primetime4u;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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
import java.util.Locale;

import it.scripto.primetime4u.cards.ProposalCard;
import it.scripto.primetime4u.cards.WelcomeCard;
import it.scripto.primetime4u.model.Movie;
import it.scripto.primetime4u.model.Proposal;
import it.scripto.primetime4u.model.ServerResponse;
import it.scripto.primetime4u.utils.BaseFragment;
import it.scripto.primetime4u.utils.MaterialListAdapter;
import it.scripto.primetime4u.utils.Utils;


public class ProposalFragment extends BaseFragment {

    private static final String PROPOSAL_TUTORIAL = "PROPOSAL_TUTORIAL";
    private static final String PENDING_MOVIE = "PENDING_MOVIE";
    private static final String PENDING_TITLE = "PENDING_TITLE";
    private static final String FINISIH_TIME = "FINISH_TIME";
    
    private List<Movie> proposalList = new ArrayList<>();
    private ArrayList<Card> cardList = new ArrayList<>();
    private MaterialListAdapter materialListViewAdapter;

    private ProgressBar progressBar;
    
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
        MaterialListView proposalMaterialListView = (MaterialListView) view.findViewById(R.id.proposal_material_list_view);
        // TODO add animation: proposal_material_list_view.setCardAnimation(IMaterialView.CardAnimation.SWING_BOTTOM_IN);

        // Get progress bar
        progressBar = (ProgressBar) view.findViewById(R.id.proposal_progress_bar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor(getString(R.color.accent)), PorterDuff.Mode.SRC_IN);
        
        // Get user_id
        MainActivity base = (MainActivity) this.getActivity();
        final String account = base.getAccount();
        
        // Get preferences
        preferences = getActivity().getPreferences(Context.MODE_PRIVATE);

        // Generate URL
        String url = Utils.SERVER_API + "proposal/" + account;
        
        // Get proposals
        get(url);

        // Create and set adapter
        materialListViewAdapter = new MaterialListAdapter(getActivity());
        proposalMaterialListView.setAdapter(materialListViewAdapter);
        //proposalMaterialListView.setEmptyView(view.findViewById(R.id.no_proposal_text_view));

        // Welcome Card for feedback
        if (preferences.contains(PENDING_MOVIE) && movieIsFinished()) {
            final WelcomeCard welcomeCard = new WelcomeCard(context);
            welcomeCard.setTitle(getResources().getString(R.string.welcome_text));
            welcomeCard.setDescription(String.format(getResources().getString(R.string.feedback_text), preferences.getString(PENDING_TITLE, "")));

            welcomeCard.setFullWidthDivider(true);
            welcomeCard.setDividerVisible(true);
            welcomeCard.setDismissible(false);

            welcomeCard.setLeftButtonText(getString(R.string.no_text));
            welcomeCard.setRightButtonText(getString(R.string.yes_text));

            editor = preferences.edit();

            welcomeCard.setOnLeftButtonPressedListener(new OnButtonPressListener() {
                @Override
                public void onButtonPressedListener(View view, Card card) {
                    materialListViewAdapter.remove(welcomeCard);
                    editor.remove(PENDING_MOVIE);
                    editor.remove(PENDING_TITLE);
                    editor.remove(FINISIH_TIME);
                    editor.apply();
                }
            });
            welcomeCard.setOnRightButtonPressedListener(new OnButtonPressListener() {
                @Override
                public void onButtonPressedListener(View view, Card card) {
                    // Get movie ID
                    String movieId = preferences.getString(PENDING_MOVIE,"");

                    materialListViewAdapter.remove(welcomeCard);
                    editor.remove(PENDING_MOVIE);
                    editor.remove(PENDING_TITLE);
                    editor.remove(FINISIH_TIME);
                    editor.apply();

                    // Generate URL
                    String url = Utils.SERVER_API + "watched/" + account;

                    // Add watched movie
                    addWatched(url, movieId);
                }
            });

            materialListViewAdapter.add(welcomeCard);
        }
        
        // Tutorial card if is the first time
        if (!preferences.contains(PROPOSAL_TUTORIAL)) {
            final WelcomeCard tutorialCard = new WelcomeCard(context);
            tutorialCard.setTitle(getResources().getString(R.string.welcome_proposal_tutorial));
            tutorialCard.setDescription(getResources().getString(R.string.proposal_tutorial));

            tutorialCard.setFullWidthDivider(true);
            tutorialCard.setDividerVisible(true);
            tutorialCard.setDismissible(false);

            tutorialCard.setLeftButtonText(getString(R.string.no_more_tutorial));
            tutorialCard.setRightButtonText(getString(R.string.got_it));

            tutorialCard.setOnLeftButtonPressedListener(new OnButtonPressListener() {
                @Override
                public void onButtonPressedListener(View view, Card card) {
                    SharedPreferences.Editor editor = preferences.edit();
                    materialListViewAdapter.remove(tutorialCard);
                    editor.putBoolean(PROPOSAL_TUTORIAL, true);
                    editor.apply();
                }
            });
            tutorialCard.setOnRightButtonPressedListener(new OnButtonPressListener() {
                @Override
                public void onButtonPressedListener(View view, Card card) {
                    materialListViewAdapter.remove(tutorialCard);
                }
            });

            materialListViewAdapter.add(tutorialCard);
        }

        return view;
    }
    
    /**
     *
     */
    private boolean movieIsFinished() {
        long finishTime = preferences.getLong("FINISH_TIME", 0);
        return Calendar.getInstance().getTimeInMillis() - finishTime >= 0;
    }

    /**
     *
     */
    private void parseResponse(ServerResponse.ProposalData response) {
        List<Proposal> proposals = response.proposal;
        
        Log.i(TAG, String.valueOf(proposals));

        for (Proposal proposal : proposals) {

            Movie movie = new Movie();
            movie.setOriginalTitle(proposal.getOriginalTitle());
            movie.setTitle(proposal.getTitle());
            movie.setRunTimes(proposal.getRunTimes());
            movie.setChannel(proposal.getChannel());
            movie.setTime(proposal.getTime());
            movie.setIdIMDB(proposal.getIdIMDB());
            movie.setPoster(proposal.getPoster());
            if (Locale.getDefault().getLanguage().equals("it")) {
                movie.setSimplePlot(proposal.getItalianPlot());
                movie.setTitle(proposal.getTitle());
            }
            else {
                movie.setSimplePlot(proposal.getSimplePlot());
                movie.setTitle(proposal.getOriginalTitle());
            }
            movie.setPlotIt(proposal.getItalianPlot());

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
            final String title = proposal.getTitle();
            final String idIMDB = proposal.getIdIMDB();
            // Get hours and minutes from movie's time
            String time = proposal.getTime();
            final int hourInt = Integer.parseInt(time.substring(0, 2));
            final int minInt = Integer.parseInt(time.substring(3, 5));
            // Get movie's run times
            String runTimes = proposal.getRunTimes();
            // TODO: manage runtimes null
            final int runTimesInt;
            if (runTimes != null) {
                runTimesInt = Integer.parseInt(runTimes.substring(0, runTimes.length() - 4));
            } else {
                runTimesInt = 90;
            }
            card.setTitle(originalTitle);
            card.setMovieInfoText(String.format(getResources().getString(R.string.movie_info_text), proposal.getChannel(), time));

            if (!Locale.getDefault().getLanguage().equals("it")) {
                card.setTitle(originalTitle);
            } else {
                card.setTitle(title);
            }
            
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
                    Log.i(TAG, "I'LL WATCH IT");
                    // Calculate finish time
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.HOUR_OF_DAY, hourInt);
                    cal.set(Calendar.MINUTE, minInt);
                    long finishTime = cal.getTimeInMillis() + (runTimesInt * 60 * 1000);
                    
                    editor = preferences.edit();

                    editor.putString(PENDING_MOVIE, idIMDB);
                    editor.putString(PENDING_TITLE, originalTitle);
                    editor.putLong(FINISIH_TIME, finishTime);

                    editor.apply();
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
                        if (e != null){
                            Toast.makeText(context,"Errore di rete",Toast.LENGTH_LONG).show();
                            return;
                        }
                        proposalList.clear();
                        cardList.clear();

                        parseResponse(result.data);
                        
                        progressBar.setVisibility(View.INVISIBLE);
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
                        if (e != null){
                            Toast.makeText(context,"Errore di rete",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onSaveInstanceState(Bundle toSave) {
        super.onSaveInstanceState(toSave);
        // TODO: save proposalList in order to reuse after
    }
}