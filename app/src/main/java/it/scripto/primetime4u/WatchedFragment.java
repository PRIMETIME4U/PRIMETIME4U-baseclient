package it.scripto.primetime4u;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import it.scripto.primetime4u.cards.MaterialWatchedCardListAdapter;
import it.scripto.primetime4u.cards.WatchedCard;
import it.scripto.primetime4u.model.Movie;
import it.scripto.primetime4u.model.ServerResponse;
import it.scripto.primetime4u.model.Watched;
import it.scripto.primetime4u.utils.RefreshFragment;
import it.scripto.primetime4u.utils.Utils;

public class WatchedFragment extends RefreshFragment {

    private List<Movie> watchedList = new ArrayList<>();
    private List<String> dateList = new ArrayList<>();
    private List<Integer> tasteList = new ArrayList<>();
    private ArrayList<WatchedCard> cardList = new ArrayList<>();
    private String account;
    private MaterialWatchedCardListAdapter materialListViewAdapter;

    private onTasteChangeListener onTasteChangeListener;
    private ProgressBar progressBar;

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
        MaterialListView watchedMaterialListView = (MaterialListView) view.findViewById(R.id.watched_material_list_view);

        // Get progress bar
        progressBar = (ProgressBar) view.findViewById(R.id.watched_progress_bar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor(getString(R.color.accent)), PorterDuff.Mode.SRC_IN);

        // Get user_id
        MainActivity base = (MainActivity) this.getActivity();
        account = base.getAccount();

        // Create and set adapter
        materialListViewAdapter = new MaterialWatchedCardListAdapter(getActivity());
        watchedMaterialListView.setAdapter(materialListViewAdapter);

        // Get data
        refresh();

        return view;
    }

    /**
     *  
     */
    @Override
    public void refresh() {
        // Clear adapter
        clearAdapter();
        // Generate URL
        String url = Utils.SERVER_API + "watched/" + account;
        // Get watched
        get(url);
    }

    /**
     * * 
     * @param response
     */
    private void parseResponse(ServerResponse.WatchedResponse response) {
        for (Watched watched : response.data.watched) {
            Movie movie = new Movie();
            movie.setOriginalTitle(watched.getOriginalTitle());
            movie.setIdIMDB(watched.getIdIMDB());
            movie.setPoster(watched.getPoster());

            watchedList.add(movie);
            dateList.add(watched.getDate());
            tasteList.add(watched.getTasted());
        }
        fillCardList();
    }

    /**
     * draws cards of watched list
     */
    private void fillCardList() {

        for (int i = 0; i < watchedList.size(); i++) {
            final Movie watched = watchedList.get(i);

            final WatchedCard watchedCard = new WatchedCard(context);
            final String id = watched.getIdIMDB();
            
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
                            Toast.makeText(context,"Errore di rete",Toast.LENGTH_LONG).show();
                            return;
                        }
                        // Clear all data list
                        clearData();
                        // Parse response
                        parseResponse(result);
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
        dateList.clear();
        tasteList.clear();
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
        json.addProperty("idIMDB", id);
        // Do connection
        Ion.with(getActivity())
                .load("POST", url)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null){
                            Toast.makeText(context,"Errore di rete",Toast.LENGTH_LONG).show();
                            return;
                        }
                        // Refresh tastes
                        onTasteChangeListener.onTasteChanged();
                        // Unset progressbar
                        progressBar.setVisibility(View.INVISIBLE);
                        // Create snackbar
                        new SnackBar.Builder(getActivity().getApplicationContext(), view)
                                .withOnClickListener(new SnackBar.OnMessageClickListener() {
                                    @Override
                                    public void onMessageClick(Parcelable parcelable) {
                                        //TODO: create UNDO
                                    }
                                })
                                .withActionMessageId(R.string.undo)
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
                        if (e != null){
                            Toast.makeText(context,"Errore di rete",Toast.LENGTH_LONG).show();
                            return;
                        }
                        // Refresh tastes
                        onTasteChangeListener.onTasteChanged();
                        // Unset progressbar
                        progressBar.setVisibility(View.INVISIBLE);
                        // Create snackbar
                        new SnackBar.Builder(getActivity().getApplicationContext(), view)
                                .withOnClickListener(new SnackBar.OnMessageClickListener() {
                                    @Override
                                    public void onMessageClick(Parcelable parcelable) {
                                        //TODO: create UNDO
                                    }
                                })
                                .withActionMessageId(R.string.undo)
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
        super.onSaveInstanceState(toSave);
        // TODO: save watchedsList in order to reuse after
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
        public void onTasteChanged();
    }
}
