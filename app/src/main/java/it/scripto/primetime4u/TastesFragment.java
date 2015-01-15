package it.scripto.primetime4u;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dexafree.materialList.cards.model.Card;
import com.dexafree.materialList.controller.OnButtonPressListener;
import com.dexafree.materialList.view.MaterialListView;

public class TastesFragment extends BaseFragment {

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


        /** in order to add tastes (movies or actors):
         *
         * route('/api/tastes/<user_id>/<type>', methods=['GET', 'POST'])

        Endpoint that allow to list all tastes by type or add new one.
        :param user_id: email of the user
        :type user_id: string, "movie" or "artist"

        :return: list of tastes
        {"code": 0, "data": {"tastes": [{"id_IMDB": id,"original_title": original_title, "poster": poster_url}],
        "type": type, "user_id": user_id}
        :rtype: JSON
        :raise MethodNotAllowed: if method is neither POST neither GET
        :raise InternalServerError: if user is not subscribed
        :raise BadRequest: if type is neither artist neither movie
        :raise InternalServerError: if there is an error from MYAPIFILMS
        """
         *
         */


        MaterialListView tastes_material_list_view = (MaterialListView) view.findViewById(R.id.tastes_material_list_view);

        final TasteCard movieCard = new TasteCard(context);
        movieCard.setTitle("Dead Poets Society");
        movieCard.setDismissible(false);
        movieCard.setType(TasteCard.MOVIE_TYPE);
        movieCard.setDrawable(R.drawable.ic_launcher);
        movieCard.setOnTasteButtonPressedListener(new OnButtonPressListener() {
            @Override
            public void onButtonPressedListener(View view, Card card) {
                String toastText = movieCard.getTaste() ? "Me gusta" : "Me disgusta";
                Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
            }
        });

        final TasteCard artistCard = new TasteCard(context);
        artistCard.setTitle("Robin Williams");
        artistCard.setDismissible(false);
        artistCard.setType(TasteCard.ARTIST_TYPE);
        artistCard.setDrawable(R.drawable.ic_launcher);
        artistCard.setOnTasteButtonPressedListener(new OnButtonPressListener() {
            @Override
            public void onButtonPressedListener(View view, Card card) {
                String toastText = artistCard.getTaste() ? "Me gusta" : "Me disgusta";
                Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
            }
        });

        tastes_material_list_view.add(movieCard);
        tastes_material_list_view.add(artistCard);

        return view;
    }

}
