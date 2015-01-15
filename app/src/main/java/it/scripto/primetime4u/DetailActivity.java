package it.scripto.primetime4u;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idIMDB = extras.getString(EXTRA_ID_IMDB);
            originalTitle = extras.getString(EXTRA_ORIGINAL_TITLE);
        }

        if (originalTitle != null) {
            getSupportActionBar().setTitle(originalTitle);
        }

        String url = Utils.SERVER_API + "detail/movie/" + idIMDB;

        JsonObjectRequest proposalRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());

                        try {
                            JSONObject data = response.getJSONObject("data");
                            JSONObject detail = data.getJSONObject("detail");

                            Movie movie = new Movie();
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
