package it.scripto.primetime4u;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dexafree.materialList.cards.model.Card;
import com.dexafree.materialList.controller.OnButtonPressListener;
import com.dexafree.materialList.view.MaterialListView;

public class WatchedFragment extends BaseFragment {

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

        MaterialListView tastes_material_list_view = (MaterialListView) view.findViewById(R.id.watched_material_list_view);

        final WatchedCard watchedCard = new WatchedCard(context);
        watchedCard.setTitle("Dead Poets Society");
        watchedCard.setDate("18 june 1993");
        watchedCard.setDismissible(false);
        watchedCard.setDrawable(R.drawable.ic_launcher);
        watchedCard.setOnTasteButtonPressedListener(new OnButtonPressListener() {
            @Override
            public void onButtonPressedListener(View view, Card card) {
                String toastText = watchedCard.getTaste() ? "Me gusta" : "Me disgusta";
                Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
            }
        });

        tastes_material_list_view.add(watchedCard);

        return view;
    }

}
