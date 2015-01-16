package it.scripto.primetime4u;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import primetime4u.app.AppController;
import primetime4u.model.Movie;
import primetime4u.util.Utils;

public class DetailActivity extends BaseActivity {
    
    public final static String EXTRA_ID_IMDB = "ID_IMDB";
    public final static String EXTRA_ORIGINAL_TITLE = "ORIGINAL_TITLE";
    public final static String EXTRA_CHANNEL = "CHANNEL";
    public final static String EXTRA_TIME = "TIME";
    private String channel;
    private String time;

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

        String idIMDB = null;
        String originalTitle = null;
        
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
    private void drawResult(Movie movie) {
        TextView titleTextView = (TextView) findViewById(R.id.title_text_view);
        TextView movieInfoTextView = (TextView) findViewById(R.id.movie_info_text_view);
        TextView timeGenreTextView = (TextView) findViewById(R.id.time_genre_text_view);
        TextView directorsTextView = (TextView) findViewById(R.id.directors_value_text_view);
        TextView writersTextView = (TextView) findViewById(R.id.writers_value_text_view);
        TextView actorsTextView = (TextView) findViewById(R.id.actors_value_text_view);
        TextView plotTextView = (TextView) findViewById(R.id.plot_value_text_view);
        
        titleTextView.setText(movie.getOriginalTitle());
        movieInfoTextView.setText(String.format(getResources().getString(R.string.movie_info_text), movie.getChannel(), movie.getTime()));
        timeGenreTextView.setText(movie.getRunTimes() + " - " + movie.getGenres());
        
        // Set actors
        String actorsText = "";
        for (String actor : movie.getActors()) {
            actorsText = actorsText + (!(actorsText).equals("") ? ", " : "") + actor;
        }
        actorsTextView.setText(actorsText);

        // Set directors
        String directorsText = "";
        for (String director : movie.getDirectors()) {
            directorsText = directorsText + (!(directorsText).equals("") ? ", " : "") + director;
        }
        directorsTextView.setText(directorsText);

        // Set writers
        String writersText = "";
        for (String writer : movie.getWriters()) {
            writersText = writersText + (!(writersText).equals("") ? ", " : "") + writer;
        }
        writersTextView.setText(writersText);

        plotTextView.setText(movie.getPlot());
    }
    
    /**
     *
     */
    private Movie parseResponse(JSONObject response) {

        Movie movie = new Movie();
        movie.setChannel(channel);
        movie.setTime(time);

        try {
            JSONObject data = response.getJSONObject("data");
            JSONObject detail = data.getJSONObject("detail");
    
            movie.setOriginalTitle(detail.getString("original_title"));
            movie.setPlot(detail.getString("plot"));
            movie.setPoster(detail.getString("poster"));
            movie.setRated(detail.getString("rated"));
            movie.setRunTimes(detail.getString("run_times"));
            movie.setSimplePlot(detail.getString("simple_plot"));
            movie.setTitle(detail.getString("title"));
            movie.setTrailer(detail.getString("trailer"));
            movie.setYear(detail.getString("year"));
            movie.setGenres(detail.getString("genres"));
            
            // Get actors
            ArrayList<String> actors = new ArrayList<>();
            JSONArray actorsJSON = detail.getJSONArray("actors");
            for (int i = 0; i < actorsJSON.length(); i++) {
                JSONObject actorJSON = actorsJSON.getJSONObject(i);
                actors.add(actorJSON.getString("name"));
            }
            movie.setActors(actors);

            // Get directors
            ArrayList<String> directors = new ArrayList<>();
            JSONArray directorsJSON = detail.getJSONArray("directors");
            for (int i = 0; i < directorsJSON.length(); i++) {
                JSONObject actorJSON = directorsJSON.getJSONObject(i);
                directors.add(actorJSON.getString("name"));
            }
            movie.setDirectors(directors);
            
            // Get writers
            ArrayList<String> writers = new ArrayList<>();
            JSONArray writersJSON = detail.getJSONArray("writers");
            for (int i = 0; i < writersJSON.length(); i++) {
                JSONObject actorJSON = writersJSON.getJSONObject(i);
                writers.add(actorJSON.getString("name"));
            }
            movie.setWriters(writers);
            
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
        return movie;
    }

    /**
     *
     */
    private void get(String url) {
        JsonObjectRequest proposalRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());

                        Movie movie = parseResponse(response);

                        drawResult(movie);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                    }
                }
        );

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(proposalRequest);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
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
