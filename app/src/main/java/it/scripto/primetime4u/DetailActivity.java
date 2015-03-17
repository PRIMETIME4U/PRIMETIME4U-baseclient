package it.scripto.primetime4u;



import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mrengineer13.snackbar.SnackBar;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.Locale;

import it.scripto.primetime4u.model.Artist;
import it.scripto.primetime4u.model.Movie;
import it.scripto.primetime4u.model.ServerResponse;
import it.scripto.primetime4u.utils.BaseActivity;
import it.scripto.primetime4u.utils.Utils;

public class DetailActivity extends BaseActivity {
    
    private ProgressBar progressBar;
    private boolean italian;
    private Movie movie;
    private SharedPreferences preferences;

    @Override
    protected String getTagLog() {
        return "DetailActivity";
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_detail;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get and set toolbar as action bar
        Toolbar detailActivityToolbar = (Toolbar) findViewById(R.id.detail_activity_toolbar);
        setSupportActionBar(detailActivityToolbar);

        // Set home back/home button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get progress bar
        progressBar = (ProgressBar) findViewById(R.id.detail_progress_bar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor(getString(R.color.accent)), PorterDuff.Mode.SRC_IN);

        // Pref
        preferences = getSharedPreferences(TutorialActivity.PREFERENCES,Context.MODE_PRIVATE);

        // Get if is italian or not
        italian = Locale.getDefault().getLanguage().equals("it");

        // Get movie
        movie = getIntent().getParcelableExtra(ProposalFragment.EXTRA_MOVIE);

        // Set ActionBar title with movie's title
        getSupportActionBar().setTitle(italian ? movie.getTitle() : movie.getOriginalTitle());

        // Generate URL
        String url = Utils.SERVER_API + "detail/movie/" + movie.getIdIMDB();

        // Get detail
        get(url);
    }

    /**
     *
     */
    private void drawResult(ServerResponse.DetailResponse result) {
        // Get all TextView
        TextView titleTextView = (TextView) findViewById(R.id.title_text_view);
        TextView channelTextView = (TextView) findViewById(R.id.channel_text_view);
        TextView timeTextView = (TextView) findViewById(R.id.time_text_view);
        TextView timeGenreTextView = (TextView) findViewById(R.id.time_genre_text_view);
        TextView directorsValueTextView = (TextView) findViewById(R.id.directors_value_text_view);
        TextView writersValueTextView = (TextView) findViewById(R.id.writers_value_text_view);
        TextView actorsValueTextView = (TextView) findViewById(R.id.actors_value_text_view);
        TextView plotValueTextView = (TextView) findViewById(R.id.plot_value_text_view);
        CardView cardView = (CardView) findViewById(R.id.detail_card_view);

        // Get detail
        Movie detail = result.data.detail;

        // Set info
        titleTextView.setText(italian ? detail.getTitle() : detail.getOriginalTitle());
        channelTextView.setText(String.format(getResources().getString(R.string.channel_text), movie.getChannel(), movie.getChannelNumber()));
        timeTextView.setText(String.format(getResources().getString(R.string.time_text), movie.getTime()));
        timeGenreTextView.setText(detail.getRunTimes() + " - " + detail.getGenres());
        plotValueTextView.setText(italian ? detail.getItalianPlot() : detail.getPlot());

        // Set actors
        String actorsText = "";
        for (Artist actor : detail.getActors()) {
            actorsText = actorsText + (!(actorsText).equals("") ? ", " : "") + actor.getName();
        }
        actorsValueTextView.setText(actorsText);

        // Set directors
        String directorsText = "";
        for (Artist director : detail.getDirectors()) {
            directorsText = directorsText + (!(directorsText).equals("") ? ", " : "") + director.getName();
        }
        directorsValueTextView.setText(directorsText);

        // Set writers
        String writersText = "";
        for (Artist writer : detail.getWriters()) {
            writersText = writersText + (!(writersText).equals("") ? ", " : "") + writer.getName();
        }
        writersValueTextView.setText(writersText);

        cardView.setVisibility(View.VISIBLE);
    }

    /**
     *
     */
    private void get(String url) {
        progressBar.setVisibility(View.VISIBLE);
        Ion.with(this)
                .load(url)
                .as(new TypeToken<ServerResponse.DetailResponse>() {
                })
                .setCallback(new FutureCallback<ServerResponse.DetailResponse>() {
                    @Override
                    public void onCompleted(Exception e, ServerResponse.DetailResponse result) {
                        if (e != null) {
                            Log.e(TAG, e.toString());
                            Toast.makeText(getBaseContext(), getString(R.string.generic_error) , Toast.LENGTH_LONG).show();
                            return;
                        }
                        drawResult(result);
                        
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        for(int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            SpannableString spanString = new SpannableString(menu.getItem(i).getTitle().toString());
            spanString.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0, spanString.length(), 0); //fix the color to white
            item.setTitle(spanString);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        SharedPreferences preferences = getSharedPreferences(TutorialActivity.PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor;

        // Get account
        String account = preferences.getString(TutorialActivity.ACCOUNT, null);



        switch (item.getItemId()) {
            case R.id.details_dislike:

                String url = url = Utils.SERVER_API+"untaste/"+account;
                JsonObject json = new JsonObject();
                json.addProperty("data",movie.getIdIMDB());
                Ion.with(this)
                        .load("POST", url)
                        .setJsonObjectBody(json)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                if (e != null) {
                                    Log.e(TAG, e.toString());
                                    Toast.makeText(getBaseContext(),getString(R.string.generic_error) ,Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                String msg= String.format(getResources().getString(R.string.dislike), italian ? movie.getTitle() : movie.getOriginalTitle());
                Toast t = new Toast(getBaseContext());
                t.setText(msg);
                t.setDuration(Toast.LENGTH_LONG);
                t.show();

                editor = preferences.edit();
                if (!preferences.contains("ALREADY_WATCHED_LIST")){
                    editor.putString("ALREADY_WATCHED_LIST", movie.getTitle());
                } else {
                    String s = preferences.getString("ALREADY_WATCHED_LIST","");
                    s = s + "|" + movie.getTitle();
                    editor.putString("ALREADY_WATCHED_LIST",s);
                }
                editor.apply();


                return true;
            case R.id.details_already_watched:
                editor = preferences.edit();
                if (!preferences.contains("ALREADY_WATCHED_LIST")){
                    editor.putString("ALREADY_WATCHED_LIST", movie.getTitle());
                } else {
                    String s = preferences.getString("ALREADY_WATCHED_LIST","");
                    s = s + "|" + movie.getTitle();
                    editor.putString("ALREADY_WATCHED_LIST",s);
                }
                editor.apply();


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
