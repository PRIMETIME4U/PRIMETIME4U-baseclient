package it.scripto.primetime4u;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dexafree.materialList.cards.model.Card;
import com.dexafree.materialList.controller.OnButtonPressListener;
import com.dexafree.materialList.view.MaterialListView;

public class ProposalFragment extends BaseFragment {

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

        MaterialListView proposal_material_list_view = (MaterialListView) view.findViewById(R.id.proposal_material_list_view);

        ProposalCard card = new ProposalCard(context);
        card.setTitle("Your title");
        card.setDescription("Your description");
        card.setLeftButtonText("Detail");
        card.setRightButtonText("I'll watch it");
        card.setDismissible(false);

        card.setOnRightButtonPressedListener(new OnButtonPressListener() {
            @Override
            public void onButtonPressedListener(View view, Card card) {
                Toast.makeText(context, "You have pressed the right button", Toast.LENGTH_SHORT).show();
            }
        });

        card.setOnLeftButtonPressedListener(new OnButtonPressListener() {
            @Override
            public void onButtonPressedListener(View view, Card card) {
                Toast.makeText(context, "You have pressed the left button", Toast.LENGTH_SHORT).show();

            }
        });

        proposal_material_list_view.add(card);
        
        return view;
    }
}
