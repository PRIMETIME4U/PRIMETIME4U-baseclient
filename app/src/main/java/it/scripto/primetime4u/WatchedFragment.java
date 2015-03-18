package it.scripto.primetime4u;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.scripto.primetime4u.cards.WatchedCard;
import it.scripto.primetime4u.cards.WelcomeCard;
import it.scripto.primetime4u.model.Movie;
import it.scripto.primetime4u.model.ServerResponse;
import it.scripto.primetime4u.utils.MaterialListAdapter;
import it.scripto.primetime4u.utils.RefreshFragment;
import it.scripto.primetime4u.utils.Utils;

public class WatchedFragment extends RefreshFragment {

    private static final String WATCHED_TUTORIAL = "WATCHED_TUTORIAL";
    private static final String STATE_WATCHED_LIST = "WATCHED_LIST";
    private static final String STATE_NEXT_PAGE = "NEXT_PAGE";

    private List<Movie> watchedList = new ArrayList<>();
    private ArrayList<WatchedCard> cardList = new ArrayList<>();
    private String account;
    private MaterialListAdapter materialListViewAdapter;

    private onTasteChangeListener onTasteChangeListener;
    private ProgressBar progressBar;
    private MaterialListView watchedMaterialListView;
    private String nextPage;

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
        watchedMaterialListView = (MaterialListView) view.findViewById(R.id.watched_material_list_view);

        // Get progress bar
        progressBar = (ProgressBar) view.findViewById(R.id.watched_progress_bar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor(getString(R.color.accent)), PorterDuff.Mode.SRC_IN);

        // Get user_id
        MainActivity base = (MainActivity) this.getActivity();
        account = base.getAccount();

        // Get preferences
        final SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);

        // Create and set adapter
        materialListViewAdapter = new MaterialListAdapter(getActivity());
        watchedMaterialListView.setAdapter(materialListViewAdapter);


        // Tutorial card if is the first time
        if (!preferences.contains(WATCHED_TUTORIAL)) {
            final WelcomeCard tutorialCard = new WelcomeCard(context);
            tutorialCard.setTitle(getResources().getString(R.string.welcome_watched_tutorial));
            tutorialCard.setDescription(getResources().getString(R.string.watched_tutorial));

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
                    editor.putBoolean(WATCHED_TUTORIAL, true);
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
        if (savedInstanceState != null) {
            Log.i(TAG, "Restore watchedList");
            watchedList = savedInstanceState.getParcelableArrayList(STATE_WATCHED_LIST);
            Log.i(TAG, "Restore nextPage");
            nextPage = savedInstanceState.getString(STATE_NEXT_PAGE);
            Log.i(TAG, String.format("Size saved: %d", watchedList.size()));
            fillCardList();
            progressBar.setVisibility(View.INVISIBLE);
        } else {
            // Get watched
            Log.i(TAG, "Get watchedList");
            // Get data
            clearData();
            // Generate URL
            String url = Utils.SERVER_API + "watched/" + account;
            // Get watched
            get(url);
        }

        return view;
    }

    /**
     *  
     */
    @Override
    public void refresh() {
        // Clear data
        clearData();
        // Clear adapter
        clearAdapter();
        // Generate URL
        String url = Utils.SERVER_API + "watched/" + account;
        // Get watched
        get(url);
    }

    /**
     * draws cards of watched list
     */
    private void fillCardList() {

        for (final Movie watched : watchedList) {

            final WatchedCard watchedCard = new WatchedCard(context);
            final String id = watched.getIdIMDB();

            if (!Locale.getDefault().getLanguage().equals("it")) {
                watchedCard.setTitle(watched.getOriginalTitle());
            } else {
                watchedCard.setTitle(watched.getTitle());
            }
            
            watchedCard.setDate(watched.getDate());
            watchedCard.setTaste(watched.isTaste());
            watchedCard.setDismissible(false);
            watchedCard.setPoster(watched.getPoster());
            watchedCard.setOnTasteButtonPressedListener(new OnButtonPressListener() {
                @Override
                public void onButtonPressedListener(View view, Card card) {
                    if (watchedCard.getTaste()) {
                        String url = Utils.SERVER_API + "tastes/" + account + "/movie";
                        addTaste(url, id);
                    } else {
                        String url = Utils.SERVER_API + "tastes/" + account + "/movie/" + id;
                        deleteTaste(url);
                    }
                }
            });

            cardList.add(watchedCard);
        }

        materialListViewAdapter.addAll(cardList);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final View footerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.show_more, null, false);
            Button footerButton = (Button) footerView.findViewById(R.id.button);

            if (nextPage != null) {
                Log.i(TAG, String.format("Size: %d", watchedList.size()));
                watchedMaterialListView.addFooterView(footerView);
                footerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        get(Utils.SERVER_URL + nextPage);
                        watchedMaterialListView.removeFooterView(footerView);
                    }
                });
            }
        }
        else{
            if (nextPage!=null)
                get(Utils.SERVER_URL + nextPage);
        }
    }
    
    /**
     * gets "watched" list and parses the response
     */
    private void get(String url) {
        // Set progressbar
        progressBar.setVisibility(View.VISIBLE);
        // Do connection
        Ion.with(context)
                .load(url)
                .as(new TypeToken<ServerResponse.WatchedResponse>() {
                })
                .setCallback(new FutureCallback<ServerResponse.WatchedResponse>() {
                    @Override
                    public void onCompleted(Exception e, ServerResponse.WatchedResponse result) {
                        if (e != null) {
                            Log.e(TAG, e.toString());
                            Toast.makeText(context,getString(R.string.generic_error) ,Toast.LENGTH_LONG).show();
                            return;
                        }
                        cardList.clear();
                        clearAdapter();
                        nextPage = result.data.nextPage;
                        watchedList.addAll(result.data.watched);
                        fillCardList();

                        // Unset progressbar
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }
    
    /**
     *
     */
    private void clearData() {
        watchedList.clear();
        cardList.clear();
    }

    /**
     * adds taste to user's taste list
     */
    private void addTaste(String url, final String id) {
        // Clear adapter
        clearAdapter();
        // Set progressbar
        progressBar.setVisibility(View.VISIBLE);
       
        // Create JSON object
        JsonObject json = new JsonObject();
        json.addProperty("data", id);
        // Do connection
        Ion.with(getActivity())
                .load("POST", url)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {
                            Log.e(TAG, e.toString());
                            Toast.makeText(context, getString(R.string.generic_error) , Toast.LENGTH_LONG).show();
                            return;
                        }
                        // Refresh tastes
                        onTasteChangeListener.onTasteChanged();
                        // Unset progressbar
                        progressBar.setVisibility(View.INVISIBLE);
                        // Create snackbar
                        new SnackBar.Builder(getActivity().getApplicationContext(), view)
//                                .withOnClickListener(new SnackBar.OnMessageClickListener() {
//                                    @Override
//                                    public void onMessageClick(Parcelable parcelable) {
//                                        //TODO: create UNDO
//                                    }
//                                })
//                                .withActionMessageId(R.string.undo)
                                .withMessageId(R.string.taste_added)
                                .show();
                    }
                });
    }

    /**
     * deletes from user's taste list
     */
    private void deleteTaste(String url) {
        // Clear adapter
        clearAdapter();
        // Set progressbar
        progressBar.setVisibility(View.VISIBLE);
        
        // Do connection
        Ion.with(context)
                .load("DELETE", url)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {
                            Log.e(TAG, e.toString());
                            Toast.makeText(context, getString(R.string.generic_error) , Toast.LENGTH_LONG).show();
                            return;
                        }
                        // Refresh tastes
                        onTasteChangeListener.onTasteChanged();
                        // Unset progressbar
                        progressBar.setVisibility(View.INVISIBLE);
                        // Create snackbar
                        new SnackBar.Builder(getActivity().getApplicationContext(), view)
//                                .withOnClickListener(new SnackBar.OnMessageClickListener() {
//                                    @Override
//                                    public void onMessageClick(Parcelable parcelable) {
//                                        //TODO: create UNDO
//                                    }
//                                })
//                                .withActionMessageId(R.string.undo)
                                .withMessageId(R.string.taste_deleted)
                                .show();
                    }
                });
    }

    /**
     * 
     */
    private void clearAdapter() {
        materialListViewAdapter.clear();
        materialListViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle toSave) {
        // Save proposal, date and taste list
        Log.i(TAG, "Save watchedList");
        toSave.putParcelableArrayList(STATE_WATCHED_LIST, (ArrayList<? extends Parcelable>) watchedList);
        Log.i(TAG, "Save nextPage");
        toSave.putString(STATE_NEXT_PAGE, nextPage);
        super.onSaveInstanceState(toSave);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onTasteChangeListener = (onTasteChangeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onTasteChangeListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onTasteChangeListener = null;
    }

    public interface onTasteChangeListener {
        void onTasteChanged();
    }
}
