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

        MaterialListView tastes_material_list_view = (MaterialListView) view.findViewById(R.id.tastes_material_list_view);
        
        final TasteCard movieCard = new TasteCard(context);
        movieCard.setTitle("Dead Poets Society");
        movieCard.setDescription("Film");
        movieCard.setDrawable(R.drawable.ic_launcher);
        movieCard.setOnTasteButtonPressedListener(new OnButtonPressListener() {
            @Override
            public void onButtonPressedListener(View view, Card card) {
                String toastText = movieCard.getTaste() ? "Me gusta" : "Me disgusta";
                Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
            }
        });
        
        
        tastes_material_list_view.add(movieCard);

        return view;
    }

}
