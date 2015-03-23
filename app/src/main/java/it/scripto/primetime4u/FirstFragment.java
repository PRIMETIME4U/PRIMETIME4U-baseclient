package it.scripto.primetime4u;

import java.util.Random;

import it.scripto.primetime4u.utils.ColorFragment;

public class FirstFragment extends ColorFragment {

    private Random random = new Random();

    public static FirstFragment newInstance() {
        return new FirstFragment();
    }

    public FirstFragment() {
        // Required empty public constructor
    }

    @Override
    protected String getTagLog() {
        return "FirstFragment";
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_first;
    }

    @Override
    protected int getColor() {
        return getResources().getColor(R.color.primary_dark);
        //return (0xff000000 | random.nextInt(0x00ffffff));
    }
}
