package it.scripto.primetime4u;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

import it.scripto.primetime4u.cards.ProposalCard;
import it.scripto.primetime4u.cards.WelcomeCard;
import it.scripto.primetime4u.model.Movie;
import it.scripto.primetime4u.model.ServerResponse;
import it.scripto.primetime4u.utils.BaseFragment;
import it.scripto.primetime4u.utils.ProposalListAdapter;
import it.scripto.primetime4u.utils.Utils;


public class ProposalFragment extends BaseFragment {

    private static final String PROPOSAL_TUTORIAL = "PROPOSAL_TUTORIAL";
    private static final String PENDING_MOVIE = "PENDING_MOVIE";
    private static final String PENDING_TITLE = "PENDING_TITLE";
    private static final String FINISH_TIME = "FINISH_TIME";
    private static final String PENDING_DATE = "PENDING_DATE";
    private static final String STATE_PROPOSAL_LIST = "PROPOSAL_LIST";
    private static final String STATE_COUNT = "COUNT";
    private static final String STATE_ALREADY_WATCHED_TITLES = "WATCHED_LIST";
    public static final String EXTRA_MOVIE = "MOVIE";

    private List<Movie> proposalList = new ArrayList<>();
    private ArrayList<Card> cardList = new ArrayList<>();
    private ArrayList<Card> alreadyWatchedList = new ArrayList<>();
    private ArrayList<String> alreadyWatchedTitles = new ArrayList<>();

    private ProposalListAdapter materialListViewAdapter;

    private ProgressBar progressBar;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private View fragmentView;
    private String account;
    private boolean italian;
    private MaterialListView proposalMaterialListView;

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

        fragmentView = view;

        // Setting up MaterialListView
        proposalMaterialListView = (MaterialListView) view.findViewById(R.id.proposal_material_list_view);
        // TODO add animation: proposal_material_list_view.setCardAnimation(IMaterialView.CardAnimation.SWING_BOTTOM_IN);

        // Get ProgressBar
        progressBar = (ProgressBar) view.findViewById(R.id.proposal_progress_bar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor(getString(R.color.accent)), PorterDuff.Mode.SRC_IN);

        // Get user_id
        MainActivity base = (MainActivity) this.getActivity();
        account = base.getAccount();

        // Get if is italian or not
        italian = base.isItalian();

        // Get preferences
        preferences = getActivity().getPreferences(Context.MODE_PRIVATE);

        // Create and set adapter
        materialListViewAdapter = new ProposalListAdapter(getActivity());
        proposalMaterialListView.setAdapter(materialListViewAdapter);
        //proposalMaterialListView.setEmptyView(view.findViewById(R.id.no_proposal_text_view));

        // Tutorial card if is the first time
        if (!preferences.contains(PROPOSAL_TUTORIAL)) {
            final WelcomeCard tutorialCard = new WelcomeCard(context);

            // Set card property
            tutorialCard.setFullWidthDivider(true);
            tutorialCard.setDividerVisible(true);
            tutorialCard.setDismissible(false);

            // Set card info
            tutorialCard.setTitle(getResources().getString(R.string.welcome_proposal_tutorial));
            tutorialCard.setDescription(getResources().getString(R.string.proposal_tutorial));
            tutorialCard.setLeftButtonText(getString(R.string.no_more_tutorial));
            tutorialCard.setRightButtonText(getString(R.string.got_it));

            // Set ButtonPressedListener
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
            String s = preferences.getString("ALREADY_WATCHED_LIST", "");
            StringTokenizer st = new StringTokenizer(s,"|");
            while(st.hasMoreTokens()){
                String s2 = st.nextToken();
                alreadyWatchedTitles.add(s2);
                //System.out.println(s2);

            }
        }

        if (aDayIsPassed()){
            if (preferences.contains("ALREADY_WATCHED_LIST")) {
                alreadyWatchedTitles.clear();
                preferences.edit().remove("ALREADY_WATCHED_LIST");
                preferences.edit().putString("ALREADY_WATCHED_LIST", "");
                preferences.edit().apply();
            }
        }

        if (savedInstanceState != null) {
            Log.i(TAG, "Restore proposalList and already watched list");
            proposalList = savedInstanceState.getParcelableArrayList(STATE_PROPOSAL_LIST);
            materialListViewAdapter.setCount(savedInstanceState.getInt(STATE_COUNT));
            alreadyWatchedTitles = savedInstanceState.getStringArrayList(STATE_ALREADY_WATCHED_TITLES);
            for (int i=0;i<alreadyWatchedTitles.size();i++){
                Log.i(TAG,"Back from saved instance: " + alreadyWatchedTitles.get(i));
            }
            fillCardList();
            progressBar.setVisibility(View.INVISIBLE);
        } else {
            Log.i(TAG, "Get proposalList");
            // Generate URL
            String url = Utils.SERVER_API + "proposal/" + account;
            // Get proposals
            get(url);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Welcome Card for feedback
        if (preferences.contains(PENDING_MOVIE) && movieIsFinished()) {
            final WelcomeCard welcomeCard = new WelcomeCard(context);

            // Set card property
            welcomeCard.setFullWidthDivider(true);
            welcomeCard.setDividerVisible(true);
            welcomeCard.setDismissible(false);

            // Set card info
            welcomeCard.setTitle(getResources().getString(R.string.welcome_text));
            welcomeCard.setDescription(String.format(getResources().getString(R.string.feedback_text), preferences.getString(PENDING_TITLE, "")));
            welcomeCard.setLeftButtonText(getString(R.string.no_text));
            welcomeCard.setRightButtonText(getString(R.string.yes_text));

            editor = preferences.edit();

            // Set ButtonPressedListener
            welcomeCard.setOnLeftButtonPressedListener(new OnButtonPressListener() {
                @Override
                public void onButtonPressedListener(View view, Card card) {
                    // Not watched
                    materialListViewAdapter.remove(welcomeCard);
                    editor.remove(PENDING_MOVIE);
                    editor.remove(PENDING_TITLE);
                    editor.remove(FINISH_TIME);
                    editor.apply();
                }
            });

            welcomeCard.setOnRightButtonPressedListener(new OnButtonPressListener() {
                @Override
                public void onButtonPressedListener(View view, Card card) {
                    // Watched
                    // Get movie ID
                    String movieId = preferences.getString(PENDING_MOVIE, "");
                    String date = preferences.getString(PENDING_DATE, "");

                    materialListViewAdapter.remove(welcomeCard);
                    editor.remove(PENDING_MOVIE);
                    editor.remove(PENDING_TITLE);
                    editor.remove(FINISH_TIME);
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
    private void fillCardList() {
        for (final Movie proposal: proposalList) {
            final ProposalCard card = new ProposalCard(context);

            // Get hours and minutes from movie's time
            String time = proposal.getTime();
            final int hourInt = Integer.parseInt(time.substring(0, 2));
            final int minInt = Integer.parseInt(time.substring(3, 5));

            // Get movie's run times
            String runTimes = proposal.getRunTimes();
            final int runTimesInt;
            if (runTimes != null) {
                runTimesInt = Integer.parseInt(runTimes.substring(0, runTimes.length() - 4));
            } else {
                runTimesInt = 90;
            }

            // Set card property
            card.setFullWidthDivider(true);
            card.setDividerVisible(true);
            card.setDismissible(false);

            // Set card info
            card.setTitle(italian ? proposal.getTitle() : proposal.getOriginalTitle());
            if (alreadyWatchedTitles.contains(card.getTitle())){
                continue;
            }
            card.setDescription(italian ? proposal.getItalianPlot() : proposal.getSimplePlot());
            card.setMovieInfoText(String.format(getResources().getString(R.string.movie_info_text), proposal.getChannel(), proposal.getTime()));
            card.setPoster(proposal.getPoster());
            card.setLeftButtonText(getResources().getString(R.string.already_watched_text));
            card.setRightButtonText(getResources().getString(R.string.watch_text));

            // Set ButtonPressedListener
            final String message = String.format(getResources().getString(R.string.will_watch), card.getTitle());
            card.setOnRightButtonPressedListener(new OnButtonPressListener() {
                @Override
                public void onButtonPressedListener(View view, Card card) {
                    // Will watch
                    // Calculate finish time
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.HOUR_OF_DAY, hourInt);
                    cal.set(Calendar.MINUTE, minInt);
                    long finishTime = cal.getTimeInMillis() + (runTimesInt * 60 * 1000);

                    // Add to preference
                    editor = preferences.edit();
                    editor.putString(PENDING_MOVIE, proposal.getIdIMDB());
                    editor.putString(PENDING_TITLE, italian ? proposal.getTitle() : proposal.getOriginalTitle());
                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat simpleDateFormat= new SimpleDateFormat("dd-MM-yyyy");
                    editor.putString(PENDING_DATE, simpleDateFormat.format(cal.getTime()));
                    editor.putLong(FINISH_TIME, finishTime);
                    editor.apply();

                    // Create SnackBar
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
                    // Already watched

                    alreadyWatchedTitles.add(proposal.getTitle());
                    editor = preferences.edit();
                    if (!preferences.contains("ALREADY_WATCHED_LIST")){
                        editor.putString("ALREADY_WATCHED_LIST", proposal.getTitle());
                    } else {
                        String s = preferences.getString("ALREADY_WATCHED_LIST","");
                        s = s + "|" + proposal.getTitle();
                        editor.putString("ALREADY_WATCHED_LIST",s);
                    }
                    editor.apply();

                    materialListViewAdapter.remove(card);

                    // Generate URL
                    String url = Utils.SERVER_API + "watched/" + account;
                    // Add watched movie, with a special date 01-01-1900 to recognize these movies
                    addWatched(url, proposal.getIdIMDB(), "01-01-1900");

                }
            });

            card.setOnImagePressListener(new OnButtonPressListener() {
                @Override
                public void onButtonPressedListener(View view, Card card) {
                    // Detail
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra(EXTRA_MOVIE, proposal);
                    startActivity(intent);
                }
            });

            card.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    // Dislike it
                    String url = Utils.SERVER_API+"untaste/"+account;
                    // Prevent to reshow the movie card for today
                    alreadyWatchedTitles.add(proposal.getTitle());
                    materialListViewAdapter.remove(card);
                    editor = preferences.edit();
                    if (!preferences.contains("ALREADY_WATCHED_LIST")){
                        editor.putString("ALREADY_WATCHED_LIST", proposal.getTitle());
                    } else {
                        String s = preferences.getString("ALREADY_WATCHED_LIST","");
                        s = s + "|" + proposal.getTitle();
                        editor.putString("ALREADY_WATCHED_LIST",s);
                    }
                    editor.apply();
                    JsonObject json = new JsonObject();
                    json.addProperty("data",proposal.getIdIMDB());
                    Ion.with(getActivity())
                            .load("POST", url)
                            .setJsonObjectBody(json)
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) {
                                    if (e != null) {
                                        Log.e(TAG, e.toString());
                                        Toast.makeText(context,getString(R.string.generic_error) ,Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                    // Create SnackBar
                    new SnackBar.Builder(getActivity().getApplicationContext(), fragmentView)
//                            .withOnClickListener(new SnackBar.OnMessageClickListener() {
//                                @Override
//                                public void onMessageClick(Parcelable parcelable) {
//
//                                }
//                            })
//                            .withActionMessageId(R.string.undo)
                            .withMessage(String.format(getResources().getString(R.string.dislike), italian ? proposal.getTitle() : proposal.getOriginalTitle()))
                            .show();
                    return true;
                }
            });
            cardList.add(card);
        }

        //mostrare solamente le card che non sono state già inserite nella already watched list

        for (int j=0; j < cardList.size(); j++){
            ProposalCard current = (ProposalCard) cardList.get(j);
            String currentTitle = current.getTitle();
            if (alreadyWatchedTitles.contains(currentTitle)){
                cardList.remove(j);
            }
        }

        materialListViewAdapter.addAll(cardList);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final View footerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.show_more, null, false);
            Button footerButton = (Button) footerView.findViewById(R.id.button);

            if (materialListViewAdapter.getCount() != materialListViewAdapter.getSize()) {
                proposalMaterialListView.addFooterView(footerView);
                Log.i(TAG, "Footer button has been added");
                footerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        materialListViewAdapter.increaseCount();
                        if (materialListViewAdapter.getCount() == materialListViewAdapter.getSize()) {
                            proposalMaterialListView.removeFooterView(footerView);
                        }
                    }
                });
            }
        }
        else{
            materialListViewAdapter.increaseCount();
        }
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
                        if (e != null) {
                            Log.e(TAG, e.toString());
                            Toast.makeText(context, getString(R.string.generic_error), Toast.LENGTH_LONG).show();
                            return;
                        }
                        // Clear lists
                        proposalList.clear();
                        cardList.clear();

                        // Fill card list
                        proposalList = result.data.proposal;
                        fillCardList();

                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }

    /**
     *
     */
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
                        if (e != null) {
                            Log.e(TAG, e.toString());
                            Toast.makeText(context,getString(R.string.generic_error) ,Toast.LENGTH_LONG).show();
                        }
                    }
                });

        // Create SnackBar
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

        // Refresh data
        MainActivity base = (MainActivity) this.getActivity();
        base.onTasteChanged();
    }

    /**
     *
     */
    public boolean aDayIsPassed(){
        Calendar c = Calendar.getInstance();
        int yy = c.get(Calendar.YEAR);
        int mm = c.get(Calendar.MONTH);
        int dd = c.get(Calendar.DAY_OF_MONTH);
        String s = String.valueOf(yy+mm+dd);
        if (preferences.contains("TODAY")){
            String oldday = preferences.getString("TODAY","");
            if (oldday.equals(s)) {
                return false;
            } else {
                preferences.edit().putString("TODAY",s).apply();
                return true;
            }
        } else {
            //first call, obviously is not passed a day
            preferences.edit().putString("TODAY",s).apply();
            return false;
        }

    }

    @Override
    public void onSaveInstanceState(Bundle toSave) {
        if (proposalList != null) {
            // Save proposal list
            toSave.putParcelableArrayList(STATE_PROPOSAL_LIST, (ArrayList<? extends Parcelable>) proposalList);
            // Save already watched list
            toSave.putStringArrayList(STATE_ALREADY_WATCHED_TITLES, alreadyWatchedTitles);
            toSave.putInt(STATE_COUNT, materialListViewAdapter.getCount());
            Log.i(TAG, "Save proposalList and already watched");
        }
        super.onSaveInstanceState(toSave);
    }
}
