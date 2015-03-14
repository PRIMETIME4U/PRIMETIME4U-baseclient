package it.scripto.primetime4u;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
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
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import it.scripto.primetime4u.cards.TasteCard;
import it.scripto.primetime4u.cards.WelcomeCard;
import it.scripto.primetime4u.model.Artist;
import it.scripto.primetime4u.model.Genre;
import it.scripto.primetime4u.model.Movie;
import it.scripto.primetime4u.model.ServerResponse;
import it.scripto.primetime4u.utils.MaterialListAdapter;
import it.scripto.primetime4u.utils.RefreshFragment;
import it.scripto.primetime4u.utils.Utils;

import static it.scripto.primetime4u.utils.Utils.sanitize;

public class TastesFragment extends RefreshFragment {

    private static final String TASTES_TUTORIAL = "TASTES_TUTORIAL";

    private List<Movie> tastesListMovie = new ArrayList<>();
    private List<Artist> tastesListArtist = new ArrayList<>();
    private List<Genre> tastesListGenre = new ArrayList<>();
    private ArrayList<TasteCard> cardList = new ArrayList<>();
    private HashMap<String,String> dictionary = new HashMap<>();
    private String account;
    private MaterialListAdapter materialListViewAdapter;

    private onTasteChangeListener onTasteChangeListener;
    private ProgressBar progressBar;
    private MenuItem searchItem;
    private MaterialListView tastesMaterialListView;

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

        //setting up dictionary for genres search
        setUpDictionary();

        // Setting up material list
        tastesMaterialListView = (MaterialListView) view.findViewById(R.id.tastes_material_list_view);

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
        
        //
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
        // Clear data
        clearData();
        // Clear adapter
        clearAdapter();
        // Generate URL
        String url = Utils.SERVER_API + "tastes/" + account + "/all";
        // Get tastes
        get(url);
    }

    private void setUpDictionary(){
        dictionary.put("azione","action");
        dictionary.put("amore","romance");
        dictionary.put("commedia","comedy");
        dictionary.put("romantico","romance");
        dictionary.put("romantici","romance");
        dictionary.put("fantascienza","sci-fi");
        dictionary.put("fantascenza","sci-fi"); //tengo conto anche degli utenti sgrammaticati :D
        dictionary.put("fantascientifico","sci-fi");
        dictionary.put("orrore","horror");
        dictionary.put("giallo","crime");
        dictionary.put("gialli","crime");
        dictionary.put("noir","crime");
        dictionary.put("noire","crime");
        dictionary.put("avventura","adventure");
        dictionary.put("guerra","war");
        dictionary.put("documentario","documentary");
        dictionary.put("documentari","documentary");
        dictionary.put("biografia","biography");
        dictionary.put("biografico","biography");
        dictionary.put("fantasia","fantasy");
        dictionary.put("fantastico","fantasy");
        dictionary.put("biografici","biography");
        dictionary.put("drammatico","drama");
        dictionary.put("drammatici","drama");
        dictionary.put("animazione","animation");
        dictionary.put("cartoni","animation");
        dictionary.put("animati","animation");
        dictionary.put("west","western");


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
                searchView.setQueryHint(getString(R.string.search_hint));

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    public boolean modified;

                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        if (s == null || s.isEmpty() || s.length() == 0) {
                            Toast.makeText(getActivity(), getString(R.string.query_error), Toast.LENGTH_LONG).show();
                        }

                        StringTokenizer st = new StringTokenizer(s);
                        if (st.countTokens()==1){
                            //una sola stringa cercata, potrebbe essere un genere, che va tradotto
                            String curr = st.nextToken();
                            curr = curr.toLowerCase();
                            if (dictionary.containsKey(curr)){
                                //se sii tratta di un genere del dizionario, traduciamo prima di fare la request
                                s = dictionary.get(curr);
                            }

                        }
                        
                        String url = Utils.SERVER_API + "search/" + account + "/" + sanitize(s, false);
                        
                        getSearch(url);

                        searchView.clearFocus();
                        modified = true;
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        /*if (s.length() > 0) {
                            modified = true;
                            String url = Utils.SERVER_API + "suggest/" + account + "/" + sanitize(s, true);

                            getSuggest(url);
                            return true;
                        } else if (s.length() == 0 && modified) {
                            clearData();
                            refresh();
                            return true;
                        }
                        */
                        if (s.length() == 0 && modified) {
                            clearData();
                            refresh();
                            modified = false;
                            return true;
                        }
                        return false;
                    }
                });
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getSearch(String url) {
        progressBar.setVisibility(View.VISIBLE);
        Ion.with(context)
                .load(url)
                .as(new TypeToken<ServerResponse.SuggestResponse>() {
                })
                .setCallback(new FutureCallback<ServerResponse.SuggestResponse>() {
                    @Override
                    public void onCompleted(Exception e, ServerResponse.SuggestResponse result) {
                        if (e != null) {
                            Log.e(TAG, e.toString());
                            Toast.makeText(context, getString(R.string.generic_error) , Toast.LENGTH_LONG).show();
                            return;
                        }
                        // Parse response
                        parseSuggestResponse(result);
                        // Unset progressbar
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });    
    }

    private void getSuggest(String url) {
        progressBar.setVisibility(View.VISIBLE);
        Ion.with(context)
                .load(url)
                .as(new TypeToken<ServerResponse.SuggestResponse>() {
                })
                .setCallback(new FutureCallback<ServerResponse.SuggestResponse>() {
                    @Override
                    public void onCompleted(Exception e, ServerResponse.SuggestResponse result) {
                        if (e != null) {
                            Log.e(TAG, e.toString());
                            Toast.makeText(context, getString(R.string.generic_error) , Toast.LENGTH_LONG).show();
                            return;
                        }
                        // Parse response
                        parseSuggestResponse(result);
                        // Unset progressbar
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
        json.addProperty("data", id);
        // Do connection
        Ion.with(getActivity())
                .load("POST", url)
                .progressBar(progressBar)
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
        // Parse genres list
        for (Genre genre : response.data.tastes.genres){
            tastesListGenre.add(genre);
        }
        
        fillCardList();
    }

    /**
     *
     */
    private void parseSuggestResponse(ServerResponse.SuggestResponse response) {

        if (!response.data.movies.isEmpty() || !response.data.artists.isEmpty() || !response.data.genres.isEmpty()) {
            // Clear all data list
            clearData();
            // Parse movies list
            for (Movie movie : response.data.movies) {
                tastesListMovie.add(movie);
            }
            // Parse artists list
            for (Artist artist : response.data.artists) {
                tastesListArtist.add(artist);
            }
            // Parse genres list
            for (Genre genre : response.data.genres) {
                tastesListGenre.add(genre);
            }

            clearAdapter();

            fillCardList();
        }
    }
    
    /**
     *
     */
    private void fillCardList() {
        // Create movies cards
        for (final Movie taste : tastesListMovie) {
            final TasteCard movieCard = new TasteCard(context);
            
            if (!Locale.getDefault().getLanguage().equals("it")) {
                movieCard.setTitle(taste.getOriginalTitle());
            } else {
                movieCard.setTitle(taste.getTitle());
            }

            movieCard.setTaste(taste.isTaste());
            movieCard.setDismissible(false);
            movieCard.setType(TasteCard.MOVIE_TYPE);
            movieCard.setPoster(taste.getPoster());
            movieCard.setOnTasteButtonPressedListener(new OnButtonPressListener() {
                @Override
                public void onButtonPressedListener(View view, Card card) {
                    if (movieCard.getTaste()) {
                        String url = Utils.SERVER_API + "tastes/" + account + "/movie";
                        addTaste(url, taste.getIdIMDB());
                    } else {
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
            artistCard.setTaste(taste.isTaste());
            artistCard.setDismissible(false);
            artistCard.setType(TasteCard.ARTIST_TYPE);
            artistCard.setPoster(taste.getPhoto());
            artistCard.setOnTasteButtonPressedListener(new OnButtonPressListener() {
                @Override
                public void onButtonPressedListener(View view, Card card) {
                    if (artistCard.getTaste()) {
                        String url = Utils.SERVER_API + "tastes/" + account + "/artist";
                        addTaste(url, taste.getIdIMDB());
                    } else {
                        String url = Utils.SERVER_API + "tastes/" + account + "/artist/" + taste.getIdIMDB();
                        deleteTaste(url);
                        materialListViewAdapter.remove(artistCard);
                    }
                }
            });

            cardList.add(artistCard);
        }

        // Create genres cards
        for (final Genre taste : tastesListGenre) {
            final TasteCard genreCard = new TasteCard(context);

            genreCard.setDescription(taste.getName());
            genreCard.setTaste(taste.isTaste());
            genreCard.setDismissible(false);
            genreCard.setType(TasteCard.GENRE_TYPE);
            genreCard.setOnTasteButtonPressedListener(new OnButtonPressListener() {
                @Override
                public void onButtonPressedListener(View view, Card card) {
                    if (genreCard.getTaste()) {
                        String url = Utils.SERVER_API + "tastes/" + account + "/genre";
                        addTaste(url, taste.getName());
                    } else {
                        String url = Utils.SERVER_API + "tastes/" + account + "/genre/" + taste.getName();
                        deleteTaste(url);
                        materialListViewAdapter.remove(genreCard);
                    }
                }
            });

            cardList.add(genreCard);
        }

        materialListViewAdapter.addAll(cardList);
        tastesMaterialListView.smoothScrollToPosition(0);
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
                            Log.e(TAG, e.toString());
                            Toast.makeText(context, getString(R.string.generic_error) , Toast.LENGTH_LONG).show();
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
        tastesListGenre.clear();
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
        void onTasteChanged();
    }    
}
