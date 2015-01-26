package it.scripto.primetime4u;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dexafree.materialList.cards.model.Card;
import com.dexafree.materialList.controller.OnButtonPressListener;
import com.dexafree.materialList.view.MaterialListView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

import it.scripto.primetime4u.cards.MaterialTasteCardListAdapter;
import it.scripto.primetime4u.cards.TasteCard;
import it.scripto.primetime4u.model.Artist;
import it.scripto.primetime4u.model.Movie;
import it.scripto.primetime4u.model.ServerResponse;
import it.scripto.primetime4u.utils.BaseFragment;
import it.scripto.primetime4u.utils.Utils;

public class TastesFragment extends BaseFragment {

    private List<Movie> tastesListMovie = new ArrayList<>();
    private List<Artist> tastesListArtist = new ArrayList<>();
    private ArrayList<TasteCard> cardList = new ArrayList<>();
    private String account;
    private MaterialTasteCardListAdapter materialListViewAdapter;

    private onTasteChangeListener onTasteChangeListener;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
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
                        else if (result.has("name_approx")){
                            Toast.makeText(getActivity(), "L'artista verrà aggiunto alla tua lista gusti, attendi...",Toast.LENGTH_LONG).show();
                            JsonArray popArray= result.getAsJsonArray("name_approx");
                            JsonObject artist = popArray.get(0).getAsJsonObject();
                            String id = artist.get("id").getAsString();
                            String url = Utils.SERVER_API + "tastes/" + account + "/artist";

                            addTaste(url, id);
                        }

                        else {
                            Toast.makeText(getActivity(),"Provare con una ricerca più specifica",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /**
     * adds taste to user's taste list
     */
    private void addTaste(String url, final String id) {
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
                            return;
                        }
                        onTasteChangeListener.onTasteChanged();
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        
        setHasOptionsMenu(true);
        
        // Setting up material list
        MaterialListView tastes_material_list_view = (MaterialListView) view.findViewById(R.id.tastes_material_list_view);
        
        // Get user_id
        MainActivity base = (MainActivity) this.getActivity();
        account = base.getAccount();

        // Generate URL
        String url = Utils.SERVER_API + "tastes/" + account + "/all";
        
        // Get tastes
        get(url);

        // Create and set adapter
        materialListViewAdapter = new MaterialTasteCardListAdapter(getActivity());
        tastes_material_list_view.setAdapter(materialListViewAdapter);
        
        return view;
    }
    
    /**
     *
     */
    private void parseResponse(ServerResponse.TasteResponse response) {
        // Parse movies list
        for (Movie movie : response.data.tastes.movies) {
            tastesListMovie.add(movie);
        }
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
                        Toast.makeText(context,"L'elemento è stato rimosso dalla tua lista di gusti",Toast.LENGTH_LONG).show();

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
            artistCard.setPoster(taste.getPoster());
            artistCard.setOnTasteButtonPressedListener(new OnButtonPressListener() {
                @Override
                public void onButtonPressedListener(View view, Card card) {
                    if (!artistCard.getTaste()) {
                        Toast.makeText(context,"L'elemento è stato rimosso dalla tua lista di gusti",Toast.LENGTH_LONG).show();
                        
                        String url = Utils.SERVER_API + "tastes/" + account + "/artist/" + taste.getIdIMDB();
                        deleteTaste(url);
                        
                        materialListViewAdapter.remove(artistCard);
                    }
                }
            });

            cardList.add(artistCard);
        }

        materialListViewAdapter.addAll(cardList);
    }

    /**
     *
     */
    private void get(String url) {
        Ion.with(context)
                .load(url)
                .as(new TypeToken<ServerResponse.TasteResponse>() {
                })
                .setCallback(new FutureCallback<ServerResponse.TasteResponse>() {
                    @Override
                    public void onCompleted(Exception e, ServerResponse.TasteResponse result) {
                        if (e != null){
                            Toast.makeText(context,"Errore di rete",Toast.LENGTH_LONG).show();
                            return;
                        }
                        tastesListArtist.clear();
                        tastesListMovie.clear();
                        cardList.clear();

                        parseResponse(result);
                    }
                });
    }

    /**
     *
     */
    private void deleteTaste(String url) {
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
                        onTasteChangeListener.onTasteChanged();
                    }
                });
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
