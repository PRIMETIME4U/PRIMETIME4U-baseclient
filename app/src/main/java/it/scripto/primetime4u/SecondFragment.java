package it.scripto.primetime4u;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.Random;

import it.scripto.primetime4u.utils.ColorFragment;
import it.scripto.primetime4u.utils.Utils;


public class SecondFragment extends ColorFragment {

    private Random random = new Random();
    private String account;
    private ProgressBar progressBar;

    public static SecondFragment newInstance() {
        return new SecondFragment();

    }

    public SecondFragment() {
        // Required empty public constructor
    }
    @Override
    protected String getTagLog() {
        return "SecondFragment";
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_second;
    }

    @Override
    protected int getColor() {
        return getResources().getColor(R.color.primary_dark);
        //return (0xff000000 | random.nextInt(0x00ffffff));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Get user_id
        TutorialActivity base = (TutorialActivity) this.getActivity();
        account = base.getAccount();

        // Get progress bar
        progressBar = (ProgressBar) view.findViewById(R.id.taste_progress_bar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor(getString(R.color.accent)), PorterDuff.Mode.SRC_IN);
        progressBar.setVisibility(View.INVISIBLE);
        final TextView action = (TextView) view.findViewById(R.id.genre_action);
        action.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toBeAdded = "Action";
                String url = Utils.SERVER_API + "tastes/" + account + "/genre";
                addTaste(url, toBeAdded);
                action.setVisibility(View.INVISIBLE);
            }
        });
        final TextView adventure = (TextView) view.findViewById(R.id.genre_adventure);
        adventure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toBeAdded = "Adventure";
                String url = Utils.SERVER_API + "tastes/" + account + "/genre";
                addTaste(url, toBeAdded);
                adventure.setVisibility(View.INVISIBLE);
            }
        });
        final TextView comedy = (TextView)  view.findViewById(R.id.genre_comedy);
        comedy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toBeAdded = "Comedy";
                String url = Utils.SERVER_API + "tastes/" + account + "/genre";
                addTaste(url, toBeAdded);
                comedy.setVisibility(View.INVISIBLE);
            }
        });
        final TextView fantasy = (TextView)  view.findViewById(R.id.genre_fantasy);
        comedy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toBeAdded = "Fantasy";
                String url = Utils.SERVER_API + "tastes/" + account + "/genre";
                addTaste(url, toBeAdded);
                fantasy.setVisibility(View.INVISIBLE);
            }
        });
        final TextView romance = (TextView) view.findViewById(R.id.genre_romantic);
        romance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toBeAdded = "Romance";
                String url = Utils.SERVER_API + "tastes/" + account + "/genre";
                addTaste(url, toBeAdded);
                romance.setVisibility(View.INVISIBLE);
            }
        });
        final TextView scifi = (TextView) view.findViewById(R.id.genre_scifi);
        scifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toBeAdded = "Sci-Fi";
                String url = Utils.SERVER_API + "tastes/" + account + "/genre";
                addTaste(url, toBeAdded);
                scifi.setVisibility(View.INVISIBLE);
            }
        });
        final TextView horror = (TextView) view.findViewById(R.id.genre_horror);
        horror.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toBeAdded = "Horror";
                String url = Utils.SERVER_API + "tastes/" + account + "/genre";
                addTaste(url, toBeAdded);
                horror.setVisibility(View.INVISIBLE);
            }
        });
        final TextView war = (TextView) view.findViewById(R.id.genre_war);
        war.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toBeAdded = "War";
                String url = Utils.SERVER_API + "tastes/" + account + "/genre";
                addTaste(url, toBeAdded);
                war.setVisibility(View.INVISIBLE);
            }
        });
        final TextView western = (TextView) view.findViewById(R.id.genre_western);
        western.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toBeAdded = "Western";
                String url = Utils.SERVER_API + "tastes/" + account + "/genre";
                addTaste(url, toBeAdded);
                western.setVisibility(View.INVISIBLE);
            }
        });
        final TextView thriller = (TextView) view.findViewById(R.id.genre_thriller);
        thriller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toBeAdded = "Thriller";
                String url = Utils.SERVER_API + "tastes/" + account + "/genre";
                addTaste(url, toBeAdded);
                thriller.setVisibility(View.INVISIBLE);
            }
        });

        return view;
    }

    private void addTaste(String url, final String id) {

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

                        // Unset progressbar
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(context,R.string.taste_added,Toast.LENGTH_LONG).show();
                    }
                });
    }

}
