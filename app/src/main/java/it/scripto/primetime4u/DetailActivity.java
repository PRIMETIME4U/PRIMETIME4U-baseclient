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

import org.json.JSONException;
import org.json.JSONObject;

import primetime4u.app.AppController;
import primetime4u.model.Movie;
import primetime4u.util.Utils;


public class DetailActivity extends BaseActivity {
    
    public final static String EXTRA_ID_IMDB = "ID_IMDB";
    public final static String EXTRA_ORIGINAL_TITLE = "ORIGINAL_TITLE";
    public final static String EXTRA_CHANNEL = "CHANNEL";
    public final static String EXTRA_TIME = "TIME";

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
        String channel = null;
        String time = null;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idIMDB = extras.getString(EXTRA_ID_IMDB);
            originalTitle = extras.getString(EXTRA_ORIGINAL_TITLE);
            channel = extras.getString(EXTRA_CHANNEL);
            time = extras.getString(EXTRA_TIME);
        }

        if (originalTitle != null) {
            getSupportActionBar().setTitle(originalTitle);
        }

        String url = Utils.SERVER_API + "detail/movie/" + idIMDB;

        final String finalTime = time;
        final String finalChannel = channel;
        JsonObjectRequest proposalRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());

                        Movie movie = new Movie();
                        movie.setChannel(finalChannel);
                        movie.setTime(finalTime);

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
                        } catch (JSONException e) {
                            Log.e(TAG, e.toString());
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                        }
                        
                        drawResult(movie);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(proposalRequest);
    }

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
        timeGenreTextView.setText(movie.getRunTimes());
        directorsTextView.setText("Claudio Pastorini");
        writersTextView.setText("Giovanni Colonna");
        actorsTextView.setText("Dorel Coman, Marius Ionita");
        plotTextView.setText(movie.getPlot());
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
