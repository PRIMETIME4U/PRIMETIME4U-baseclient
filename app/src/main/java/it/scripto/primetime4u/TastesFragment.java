package it.scripto.primetime4u;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dexafree.materialList.cards.model.Card;
import com.dexafree.materialList.controller.OnButtonPressListener;
import com.dexafree.materialList.view.MaterialListView;
import com.github.mrengineer13.snackbar.SnackBar;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

import it.scripto.primetime4u.cards.TasteCard;
import it.scripto.primetime4u.cards.WelcomeCard;
import it.scripto.primetime4u.model.Artist;
import it.scripto.primetime4u.model.Movie;
import it.scripto.primetime4u.model.ServerResponse;
import it.scripto.primetime4u.utils.MaterialListAdapter;
import it.scripto.primetime4u.utils.RefreshFragment;
import it.scripto.primetime4u.utils.Utils;

public class TastesFragment extends RefreshFragment {

    private static final String TASTES_TUTORIAL = "TASTES_TUTORIAL";

    private List<Movie> tastesListMovie = new ArrayList<>();
    private List<Artist> tastesListArtist = new ArrayList<>();
    private ArrayList<TasteCard> cardList = new ArrayList<>();
    private String account;
    private MaterialListAdapter materialListViewAdapter;

    private onTasteChangeListener onTasteChangeListener;
    private ProgressBar progressBar;
    private MenuItem searchItem;
    
    private String IMDB_SEARCH_LINK = "http://www.imdb.com/xml/find?json=1&nr=1&q=";

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment TastesFragment.
     */
    public static TastesFragment newInstance() {
        return new TastesFragment();
    }

    public TastesFragment() {
        // Required empty public constructor
    }

    @Override
    protected String getTagLog() {
        return "TastesFragment";
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_tastes;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        setHasOptionsMenu(true);

        // Setting up material list
        MaterialListView tastesMaterialListView = (MaterialListView) view.findViewById(R.id.tastes_material_list_view);

        // Get progress bar
        progressBar = (ProgressBar) view.findViewById(R.id.taste_progress_bar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor(getString(R.color.accent)), PorterDuff.Mode.SRC_IN);

        // Get user_id
        MainActivity base = (MainActivity) this.getActivity();
        account = base.getAccount();

        // Get preferences
        final SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);

        // Create and set adapter
        materialListViewAdapter = new MaterialListAdapter(getActivity());
        tastesMaterialListView.setAdapter(materialListViewAdapter);

        // Get data
        refresh();
        
        // TODO: doesn't work...
        //tastesMaterialListView.setEmptyView(view.findViewById(R.id.no_taste_text_view));

        // Tutorial card if is the first time
        if (!preferences.contains(TASTES_TUTORIAL)) {
            final WelcomeCard tutorialCard = new WelcomeCard(context);
            tutorialCard.setTitle(getResources().getString(R.string.welcome_tastes_tutorial));
            tutorialCard.setDescription(getResources().getString(R.string.tastes_tutorial));

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
                    editor.putBoolean(TASTES_TUTORIAL, true);
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

    @Override
    public void refresh() {
        // Clear adapter
        materialListViewAdapter.clear();
        materialListViewAdapter.notifyDataSetChanged();

        // Generate URL
        String url = Utils.SERVER_API + "tastes/" + account + "/all";

        // Get tastes
        get(url);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_taste, menu);
        searchItem = menu.findItem(R.id.menu_search);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
                searchView.setQueryHint("Movie/artist, es: Matrix, Di Caprio");

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        if (s == null || s.isEmpty() || s.length() == 0) {
                            Toast.makeText(getActivity(), "Non hai cercato nulla", Toast.LENGTH_LONG).show();
                        }
                        String rebuilt = s.replace(" ", "+"); //sostituisco spazi con +
                        String url = IMDB_SEARCH_LINK + rebuilt;
                        
                        getIMDb(url);

                        searchView.clearFocus();
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        return false;
                    }
                });
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getIMDb(String url) {
        progressBar.setVisibility(View.VISIBLE);
        Ion.with(context)
                .load(url)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null){
                            Toast.makeText(context,"Errore di rete",Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (result.has("title_popular")){
                            //film
                            Toast.makeText(getActivity(), "Il film verrà aggiunto alla tua lista gusti, attendi...",Toast.LENGTH_LONG).show();
                            JsonArray popArray= result.getAsJsonArray("title_popular");
                            JsonObject movie = popArray.get(0).getAsJsonObject();
                            String id = movie.get("id").getAsString();
                            String url = Utils.SERVER_API + "tastes/" + account + "/movie";

                            addTaste(url, id);
                        }
                        else if (result.has("name_popular")){
                            //artista
                            Toast.makeText(getActivity(), "L'artista verrà aggiunto alla tua lista gusti, attendi...",Toast.LENGTH_LONG).show();
                            JsonArray popArray= result.getAsJsonArray("name_popular");
                            JsonObject artist = popArray.get(0).getAsJsonObject();
                            String id = artist.get("id").getAsString();
                            String url = Utils.SERVER_API + "tastes/" + account + "/artist";

                            addTaste(url, id);
                        }
                        else if (result.has("name_exact")){
                            Toast.makeText(getActivity(), "L'artista verrà aggiunto alla tua lista gusti, attendi...",Toast.LENGTH_LONG).show();
                            JsonArray popArray= result.getAsJsonArray("name_exact");
                            JsonObject artist = popArray.get(0).getAsJsonObject();
                            String id = artist.get("id").getAsString();
                            String url = Utils.SERVER_API + "tastes/" + account + "/artist";

                            addTaste(url, id);
                        }

                        else {
                            Toast.makeText(getActivity(),"Provare con una ricerca più specifica",Toast.LENGTH_LONG).show();
                        }
                        
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
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
                .progressBar(progressBar)
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
     *
     */
    private void parseResponse(ServerResponse.TasteResponse response) {
        // Parse movies list
        for (Movie movie : response.data.tastes.movies) {
            tastesListMovie.add(movie);
        }
        // Parse artists list
        for (Artist artist : response.data.tastes.artists){
            tastesListArtist.add(artist);
        }
        
        fillCardList();
    }
    
    /**
     *
     */
    private void fillCardList() {
        // Create movies cards
        for (final Movie taste : tastesListMovie) {
            final TasteCard movieCard = new TasteCard(context);
            
            movieCard.setTitle(taste.getOriginalTitle());
            movieCard.setTaste(true);
            movieCard.setDismissible(false);
            movieCard.setType(TasteCard.MOVIE_TYPE);
            movieCard.setPoster(taste.getPoster());
            movieCard.setOnTasteButtonPressedListener(new OnButtonPressListener() {
                @Override
                public void onButtonPressedListener(View view, Card card) {
                    if (!movieCard.getTaste()) {
                        String url = Utils.SERVER_API + "tastes/" + account + "/movie/" + taste.getIdIMDB();
                        deleteTaste(url);

                        materialListViewAdapter.remove(movieCard);
                    }
                }
            });

            cardList.add(movieCard);
        }
        // Create artists cards
        for (final Artist taste : tastesListArtist) {
            final TasteCard artistCard = new TasteCard(context);
            
            artistCard.setTitle(taste.getName());
            artistCard.setTaste(true);
            artistCard.setDismissible(false);
            artistCard.setType(TasteCard.ARTIST_TYPE);
            artistCard.setPoster(taste.getPhoto());
            artistCard.setOnTasteButtonPressedListener(new OnButtonPressListener() {
                @Override
                public void onButtonPressedListener(View view, Card card) {
                    if (!artistCard.getTaste()) {
                        String url = Utils.SERVER_API + "tastes/" + account + "/artist/" + taste.getIdIMDB();
                        deleteTaste(url);
                        
                        materialListViewAdapter.remove(artistCard);
                    }
                }
            });

            cardList.add(artistCard);
        }
        
/*
        TasteCard genreCard = new TasteCard(context);
        genreCard.setDescription("Action");
        genreCard.setTaste(true);
        genreCard.setDismissible(false);
        genreCard.setType(TasteCard.GENRE_TYPE);
        cardList.add(genreCard);
*/

        materialListViewAdapter.addAll(cardList);
    }

    /**
     *
     */
    private void get(String url) {
        // Set progressbar
        progressBar.setVisibility(View.VISIBLE);
        // Do connection
        Ion.with(context)
                .load(url)
                .as(new TypeToken<ServerResponse.TasteResponse>() {
                })
                .setCallback(new FutureCallback<ServerResponse.TasteResponse>() {
                    @Override
                    public void onCompleted(Exception e, ServerResponse.TasteResponse result) {
                        if (e != null) {
                            Toast.makeText(context, "Errore di rete", Toast.LENGTH_LONG).show();
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
        tastesListArtist.clear();
        tastesListMovie.clear();
        cardList.clear();
    }

    /**
     *
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

    private void clearAdapter() {
        materialListViewAdapter.clear();
        materialListViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle toSave) {
        super.onSaveInstanceState(toSave);
        // TODO: save tastesListMovie in order to reuse after
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
