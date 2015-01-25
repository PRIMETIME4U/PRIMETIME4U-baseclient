package it.scripto.primetime4u;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import it.scripto.primetime4u.model.Artist;
import it.scripto.primetime4u.model.Detail;
import it.scripto.primetime4u.model.ServerResponse;
import it.scripto.primetime4u.utils.BaseActivity;
import it.scripto.primetime4u.utils.Utils;

public class DetailActivity extends BaseActivity {
    
    public final static String EXTRA_ID_IMDB = "ID_IMDB";
    public final static String EXTRA_ORIGINAL_TITLE = "ORIGINAL_TITLE";
    public final static String EXTRA_CHANNEL = "CHANNEL";
    public final static String EXTRA_TIME = "TIME";
    private String originalTitle;
    private String channel;
    private String time;
    private ProgressBar progressBar;

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

        String idIMDB = null;

        // TODO: pass parcable and not all strings
        // Get movie info
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idIMDB = extras.getString(EXTRA_ID_IMDB);
            originalTitle = extras.getString(EXTRA_ORIGINAL_TITLE);
            channel = extras.getString(EXTRA_CHANNEL);
            time = extras.getString(EXTRA_TIME);
        }
        
        // Set ActionBar title with movie's title
        if (originalTitle != null) {
            getSupportActionBar().setTitle(originalTitle);
        }
        
        // Generate URL
        String url = Utils.SERVER_API + "detail/movie/" + idIMDB;

        // Get detail
        get(url);
    }

    /**
     *
     */
    private void drawResult(ServerResponse.DetailResponse movie) {
        TextView titleTextView = (TextView) findViewById(R.id.title_text_view);
        TextView movieInfoTextView = (TextView) findViewById(R.id.movie_info_text_view);
        TextView timeGenreTextView = (TextView) findViewById(R.id.time_genre_text_view);
        TextView directorsValueTextView = (TextView) findViewById(R.id.directors_value_text_view);
        TextView writersValueTextView = (TextView) findViewById(R.id.writers_value_text_view);
        TextView actorsValueTextView = (TextView) findViewById(R.id.actors_value_text_view);
        TextView plotValueTextView = (TextView) findViewById(R.id.plot_value_text_view);
        TextView directorsTextView = (TextView) findViewById(R.id.directors_text_view);
        TextView writersTextView = (TextView) findViewById(R.id.writers_text_view);
        TextView actorsTextView = (TextView) findViewById(R.id.actors_text_view);
        TextView plotTextView = (TextView) findViewById(R.id.plot_text_view);
        
        Detail detail = movie.data.detail;
        
        titleTextView.setText(originalTitle);
        movieInfoTextView.setText(String.format(getResources().getString(R.string.movie_info_text), channel, time));
        timeGenreTextView.setText(detail.getRunTimes() + " - " + detail.getGenres());
        
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

        plotValueTextView.setText(detail.getPlot());
        directorsTextView.setVisibility(View.VISIBLE);
        writersTextView.setVisibility(View.VISIBLE);
        actorsTextView.setVisibility(View.VISIBLE);
        plotTextView.setVisibility(View.VISIBLE);
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
                            Toast.makeText(getBaseContext(), "Errore di rete", Toast.LENGTH_LONG).show();
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
        //getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
