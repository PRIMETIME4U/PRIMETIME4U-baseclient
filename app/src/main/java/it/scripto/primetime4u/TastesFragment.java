package it.scripto.primetime4u;

import android.os.Bundle;
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
                        // do stuff with the result or error
                    }
                });
    }
    
    public void refresh() {
        // Generate URL
        String url = Utils.SERVER_API + "tastes/" + account + "/all";
        
        get(url);
    }

    @Override
    public void onSaveInstanceState(Bundle toSave) {
        super.onSaveInstanceState(toSave);
        // TODO: save tastesListMovie in order to reuse after
    }
}
