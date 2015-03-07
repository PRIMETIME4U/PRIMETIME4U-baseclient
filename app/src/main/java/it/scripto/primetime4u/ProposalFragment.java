package it.scripto.primetime4u;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dexafree.materialList.cards.model.Card;
import com.dexafree.materialList.controller.OnButtonPressListener;
import com.dexafree.materialList.view.MaterialListView;
import com.github.mrengineer13.snackbar.SnackBar;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;

import it.scripto.primetime4u.cards.ProposalCard;
import it.scripto.primetime4u.cards.WelcomeCard;
import it.scripto.primetime4u.model.Movie;
import it.scripto.primetime4u.model.Proposal;
import it.scripto.primetime4u.model.ServerResponse;
import it.scripto.primetime4u.utils.BaseFragment;
import it.scripto.primetime4u.utils.MaterialListAdapter;
import it.scripto.primetime4u.utils.Utils;

import static java.util.Calendar.DAY_OF_WEEK;


public class ProposalFragment extends BaseFragment {

    private static final String PROPOSAL_TUTORIAL = "PROPOSAL_TUTORIAL";
    private static final String PENDING_MOVIE = "PENDING_MOVIE";
    private static final String PENDING_TITLE = "PENDING_TITLE";
    private static final String FINISIH_TIME = "FINISH_TIME";
    private static final String PENDING_DATE = "PENDING_DATE";

    private List<Movie> proposalList = new ArrayList<>();
    private ArrayList<Card> cardList = new ArrayList<>();
    private ArrayList<Card> alreadyWatchedList = new ArrayList<>();
    private ArrayList<String> alreadyWatchedTitles = new ArrayList<>();
    private MaterialListAdapter materialListViewAdapter;

    private ProgressBar progressBar;
    
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private View fragmentView;
    private String account;

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

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int widthPixels = displayMetrics.widthPixels;
        float density = displayMetrics.density;
        int densityDpi = displayMetrics.densityDpi;
        int heightPixels= displayMetrics.heightPixels;
        float scaledDensity = displayMetrics.scaledDensity;
        float xdpi = displayMetrics.xdpi;
        float ydpi = displayMetrics.ydpi;
        Log.i(TAG, "\nWidth: " + String.valueOf(widthPixels) 
                + "\nHeight: " + String.valueOf(heightPixels)
                + "\ndensity: " + String.valueOf(density) 
                + "\ndensityDpi: " + String.valueOf(densityDpi)
                + "\nscaledDensity: " + String.valueOf(scaledDensity) 
                + "\nxdpi: " + String.valueOf(xdpi)
                + "\nydpi: " + String.valueOf(ydpi));
        
        
        fragmentView = view;
        
        // Setting up material list
        MaterialListView proposalMaterialListView = (MaterialListView) view.findViewById(R.id.proposal_material_list_view);
        // TODO add animation: proposal_material_list_view.setCardAnimation(IMaterialView.CardAnimation.SWING_BOTTOM_IN);

        // Get progress bar
        progressBar = (ProgressBar) view.findViewById(R.id.proposal_progress_bar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor(getString(R.color.accent)), PorterDuff.Mode.SRC_IN);
        
        // Get user_id
        MainActivity base = (MainActivity) this.getActivity();
        account = base.getAccount();
        
        // Get preferences
        preferences = getActivity().getPreferences(Context.MODE_PRIVATE);

        Calendar c = Calendar.getInstance();

        // Generate URL
        String url = Utils.SERVER_API + "proposal/" + account;


        // Create and set adapter
        materialListViewAdapter = new MaterialListAdapter(getActivity());
        proposalMaterialListView.setAdapter(materialListViewAdapter);
        //proposalMaterialListView.setEmptyView(view.findViewById(R.id.no_proposal_text_view));

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

        if (preferences.contains("ALREADY_WATCHED_LIST") && !aDayIsPassed()){
            String s = preferences.getString("ALREADY_WATCHED_LIST","");
            StringTokenizer st = new StringTokenizer(s,"|");
            while(st.hasMoreTokens()){
                alreadyWatchedTitles.add(st.nextToken());
            }
        }

        if (aDayIsPassed()){
            if (preferences.contains("ALREADY_WATCHED_LIST")){
                alreadyWatchedTitles.clear();
                preferences.edit().putString("ALREADY_WATCHED_LIST","");
            }
        }
        // Get proposals
        get(url);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
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
                    String date = preferences.getString(PENDING_DATE, "");

                    materialListViewAdapter.remove(welcomeCard);
                    editor.remove(PENDING_MOVIE);
                    editor.remove(PENDING_TITLE);
                    editor.remove(FINISIH_TIME);
                    editor.apply();

                    // Generate URL
                    String url = Utils.SERVER_API + "watched/" + account;

                    // Add watched movie
                    addWatched(url, movieId, date);
                }
            });

            materialListViewAdapter.add(welcomeCard);
        }
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

            if (alreadyWatchedTitles.contains(proposal.getTitle())||alreadyWatchedTitles.contains(proposal.getOriginalTitle())) continue;
            proposalList.add(movie);
        }
 
        fillCardList();
    }

    /**
     *
     */

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void fillCardList() {



        for (int i = 0; i < proposalList.size(); i++) {
            final ProposalCard card = new ProposalCard(context);
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
            
            final String message = String.format(getResources().getString(R.string.will_watch), card.getTitle());

            card.setMovieInfoText(String.format(getResources().getString(R.string.movie_info_text), proposal.getChannel(), proposal.getTime()));

            card.setDescription(proposal.getSimplePlot());
            card.setPoster(proposal.getPoster());

            card.setFullWidthDivider(true);
            card.setDividerVisible(true);
            card.setDismissible(false);

            card.setLeftButtonText(getResources().getString(R.string.already_watched_text));
            card.setRightButtonText(getResources().getString(R.string.watch_text));

            card.setOnRightButtonPressedListener(new OnButtonPressListener() {
                @Override
                public void onButtonPressedListener(View view, Card card) {
                    // Calculate finish time
                    // TODO: manage in better way date
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.HOUR_OF_DAY, hourInt);
                    cal.set(Calendar.MINUTE, minInt);
                    long finishTime = cal.getTimeInMillis() + (runTimesInt * 60 * 1000);

                    editor = preferences.edit();

                    editor.putString(PENDING_MOVIE, idIMDB);

                    if (!Locale.getDefault().getLanguage().equals("it")) {
                        editor.putString(PENDING_TITLE, originalTitle);
                    } else {
                        editor.putString(PENDING_TITLE, title);
                    }

                    SimpleDateFormat simpleDateFormat= new SimpleDateFormat("dd-MM-yyyy");
                    Log.i(TAG, simpleDateFormat.format(cal.getTime()));
                    
                    editor.putString(PENDING_DATE, simpleDateFormat.format(cal.getTime()));
                            
                    editor.putLong(FINISIH_TIME, finishTime);

                    editor.apply();

                    // Create snackbar
                    new SnackBar.Builder(getActivity().getApplicationContext(), fragmentView)
//                            .withOnClickListener(new SnackBar.OnMessageClickListener() {
//                                @Override
//                                public void onMessageClick(Parcelable parcelable) {
//                                    //TODO: create UNDO
//                                }
//                            })
//                            .withActionMessageId(R.string.undo)
                            .withMessage(message)
                            .show();
                }
            });

            card.setOnLeftButtonPressedListener(new OnButtonPressListener() {
                @Override
                public void onButtonPressedListener(View view, Card card) {

                    //FILM GIA' VISTO, RIMUOVERE DA LISTA E AGGIORNARE SERVER

                    alreadyWatchedList.add(card);

                    editor = preferences.edit();

                    if (!preferences.contains("ALREADY_WATCHED_LIST")){
                        editor.putString("ALREADY_WATCHED_LIST",title);
                    }
                    else {
                        String s = preferences.getString("ALREADY_WATCHED_LIST","");
                        s = s + "|" + title;
                        editor.putString("ALREADY_WATCHED_LIST",s);
                    }
                    editor.apply();

                    materialListViewAdapter.remove(card);

                    // Generate URL
                    String url = Utils.SERVER_API + "watched/" + account;



                    // Add watched movie, with a special date 00-00-0000 to recognize these movies

                    addWatched(url, idIMDB, "00-00-0000");
                }
            });

            card.setOnImagePressListener(new OnButtonPressListener() {
                @Override
                public void onButtonPressedListener(View view, Card card) {

                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra(DetailActivity.EXTRA_ID_IMDB, proposal.getIdIMDB());

                    if (!Locale.getDefault().getLanguage().equals("it")) {
                        intent.putExtra(DetailActivity.EXTRA_TITLE, originalTitle);
                    } else {
                        intent.putExtra(DetailActivity.EXTRA_TITLE, title);
                    }

                    intent.putExtra(DetailActivity.EXTRA_CHANNEL, proposal.getChannel());
                    intent.putExtra(DetailActivity.EXTRA_TIME, proposal.getTime());
                    startActivity(intent);
                }
            });

            card.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    //TODO: COMUNICARE AL SERVER CHE QUESTO FILM NON MI PIACE
                    // Create snackbar
                    new SnackBar.Builder(getActivity().getApplicationContext(), fragmentView)
//                            .withOnClickListener(new SnackBar.OnMessageClickListener() {
//                                @Override
//                                public void onMessageClick(Parcelable parcelable) {
//                                    //
//                                }
//                            })
//                            .withActionMessageId(R.string.undo)
                            .withMessage("Non ti piace: "+card.getTitle())
                            .show();
                    return true;
                }
            });


            cardList.add(card);
        }

        //mostrare solamente le card che non sono state già inserite nella already watched list

        for (int j=0;j<cardList.size();j++){
            ProposalCard current = (ProposalCard) cardList.get(j);
            for (int i=0;i<alreadyWatchedList.size();i++){
                ProposalCard curr2 = (ProposalCard) alreadyWatchedList.get(i);
                if (curr2.getTitle().equals(current.getTitle())){
                    cardList.remove(j);
                }
            }
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
                            Toast.makeText(context,getString(R.string.generic_error) ,Toast.LENGTH_LONG).show();
                            return;
                        }
                        proposalList.clear();
                        cardList.clear();

                        parseResponse(result.data);
                        
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }

    private void addWatched(String url, String id, String date) {
        JsonObject json = new JsonObject();
        json.addProperty("idIMDB", id);
        json.addProperty("date", date);

        Ion.with(getActivity())
                .load("POST", url)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null){
                            Toast.makeText(context,getString(R.string.generic_error) ,Toast.LENGTH_LONG).show();
                        }
                    }
                });

        // Create snackbar
        new SnackBar.Builder(getActivity().getApplicationContext(), fragmentView)
//                            .withOnClickListener(new SnackBar.OnMessageClickListener() {
//                                @Override
//                                public void onMessageClick(Parcelable parcelable) {
//                                    //TODO: create UNDO
//                                }
//                            })
//                            .withActionMessageId(R.string.undo)
                .withMessageId(R.string.watched_added)
                .show();
        //refresh activities
        MainActivity base = (MainActivity) this.getActivity();
        base.onTasteChanged();
    }

    public boolean aDayIsPassed(){
        Calendar c = Calendar.getInstance();
        int yy = c.get(Calendar.YEAR);
        int mm = c.get(Calendar.MONTH);
        int dd = c.get(Calendar.DAY_OF_MONTH);
        String s = String.valueOf(yy+mm+dd);
        if (preferences.contains("TODAY")){
            String oldday = preferences.getString("TODAY","");
            if (oldday.equals(s)) return false;
            else{
                preferences.edit().putString("TODAY",s).apply();
                return true;
            }
        }
        else{
            //first call, obviously is not passed a day
            preferences.edit().putString("TODAY",s).apply();
            return false;
        }

    }

    @Override
    public void onSaveInstanceState(Bundle toSave) {
        super.onSaveInstanceState(toSave);
        // TODO: save proposalList in order to reuse after

    }
}
