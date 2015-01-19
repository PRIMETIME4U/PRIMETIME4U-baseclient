package it.scripto.primetime4u;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.astuetz.PagerSlidingTabStrip;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import primetime4u.app.AppController;
import primetime4u.util.Utils;

public class MainActivity extends BaseActivity {

    private String account;

    private SharedPreferences preferences;

    private SharedPreferences.Editor editor;

    public MainAdapter adapter;

    private ViewPager pager;

    private Toolbar main_activity_toolbar;

    private PagerSlidingTabStrip tabs;

    //boolean which acts like a refresh alert
    public boolean shouldIRefresh;

    //boolean which says if i'm in the Tastes tab
    public boolean tasteTab;

    //boolean to avoid double inflations
    public boolean inflate;

    private String IMDB_SEARCH_LINK = "http://www.imdb.com/xml/find?json=1&q=";

    @Override
    protected String getTagLog() {
        return "MainActivity";
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //I close the login activity
        Intent myIntent = new Intent(StartActivity.ACTION_CLOSE);
        sendBroadcast(myIntent);
        inflate = true;
        shouldIRefresh = false;
        preferences = getPreferences(Context.MODE_PRIVATE);

        //ricavo l'email passata dalla startActivity e la salvo in maniera permanente
        if (!preferences.contains("ACCOUNT")){
            account = getIntent().getExtras().getString("email");
            editor = preferences.edit();
            editor.putString("ACCOUNT",account);
            editor.commit();
        }
        else {
            account = preferences.getString("ACCOUNT","");
        }
        // Get and set toolbar as action bar
        main_activity_toolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(main_activity_toolbar);
 
        // Initialize the ViewPager and set an adapter
        pager = (ViewPager) findViewById(R.id.main_activity_view_pager);
        adapter = new MainAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);

        // Bind the tabs to the ViewPager
        tabs = (PagerSlidingTabStrip) findViewById(R.id.main_activity_pager_sliding_tab_strip);
        tabs.setViewPager(pager);

        tabs.setOnPageChangeListener( new ViewPager.OnPageChangeListener() {
            private int current;
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                current = position;
            }

            @Override
            public void onPageSelected(int position) {
                if (position==1) {
                    //change toolbar style
                    tasteTab = true;
                    invalidateOptionsMenu();
                }
                else{
                    tasteTab = false;
                    invalidateOptionsMenu();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == pager.SCROLL_STATE_IDLE){
                    if (shouldIRefresh){
                        //if alert of refreshing has been activated, i reload fragments
                        refreshTastes();
                        shouldIRefresh = false;
                    }
                }
            }
        });





    }
    public void refreshTastes(){
       adapter.notifyDataSetChanged();
    }

    public String getAccount(){
        if (account!=null)
            return account;
        else return "";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (!tasteTab) getMenuInflater().inflate(R.menu.menu_main_nosearch, menu);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        //questo metodo viene chiamato per cambiare dinamicamente il menu

        if (tasteTab){
            if (inflate) {
                getMenuInflater().inflate(R.menu.menu_main, menu);
                inflate = false;
            }
            MenuItem searchItem = menu.findItem(R.id.searchButton);
            if (searchItem!=null){
                final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
                searchView.setQueryHint("Movie/artist, es: Matrix, Di Caprio"); //suggerimento ricerca

                searchView.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        if (s==null || s.isEmpty() || s.length()==0){
                            Toast.makeText(getBaseContext(),"Non hai cercato nulla",Toast.LENGTH_LONG).show();
                        }
                        String rebuilt = s.replace(" ","+"); //sostituisco spazi con +
                        String url = IMDB_SEARCH_LINK + rebuilt;

                        addTaste(url);

                        //Toast.makeText(getBaseContext(),url,Toast.LENGTH_LONG).show();
                        System.out.println("cerco su "+url);
                        searchView.clearFocus();
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        return false;
                    }
                });


            }

        }
        else inflate = true;
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

            //gestione del tasto impostazioni, modificare impostazioni su notifiche
        }
        if (id == R.id.searchButton){

            //gestione in onPrepareOptionMenu

        }

        return super.onOptionsItemSelected(item);
    }

    private void addTaste(String url){
        //metodo che legge da imdb il json dell'url e aggiunge l'attore/film cercato ai gusti
        /**
         * Formato risposta da IMDB, se artista:
         * {
         *   "name_popular" : [ { "id":nm010101, ... } ]
         *   "name_approx" : [ { .... } ]
         * }
         *
         * se film:
         *
         * {
         *    "title_popular" : [ { "id":tt9191919, .. } ]
         *
         * }
         *
         */
        JsonObjectRequest imdbReq = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //tenere conto caso film/artista
                        if (response.has("title_popular")){
                            //film
                            try {
                                Toast.makeText(getBaseContext(),"Il film verrà aggiunto alla tua lista gusti",Toast.LENGTH_LONG).show();
                                JSONArray popArray= response.getJSONArray("title_popular");
                                JSONObject movie = popArray.getJSONObject(0);
                                String id = movie.getString("id");
                                addToServer(id, "movie");
                            }
                            catch (JSONException e){
                                Toast.makeText(getBaseContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                            }

                        }
                        if (response.has("name_popular")){
                            //artista
                            try {
                                Toast.makeText(getBaseContext(),"L'attore verrà aggiunto alla tua lista gusti",Toast.LENGTH_LONG).show();
                                JSONArray popArray= response.getJSONArray("name_popular");
                                JSONObject movie = popArray.getJSONObject(0);
                                String id = movie.getString("id");
                                addToServer(id,"artist");
                            }
                            catch (JSONException e){
                                Toast.makeText(getBaseContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }
                        else Toast.makeText(getBaseContext(),"Provare con una ricerca più specifica",Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        VolleyLog.d(TAG,"Error: "+volleyError.getMessage());
                    }
                }
        );
        AppController.getInstance().addToRequestQueue(imdbReq);
    }

    private void addToServer(String idimdb,String type){
        /**
         * POST su nostro server:
         * /api/tastes/<user_id>/<type>  POST dove type: artist , movie o genre
         * {
         *     "idIMDB" = id
         * }
         */

        String url = Utils.SERVER_API + "tastes/" + account + "/" + type;
        JSONObject toBePosted = new JSONObject();
        try{
            toBePosted.put("idIMDB",idimdb);
        }
        catch (JSONException e ){
            Toast.makeText(getBaseContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }

        JsonObjectRequest postRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                toBePosted,
                new Response.Listener<JSONObject>() {

                        @Override
                            public void onResponse(JSONObject jsonObject) {

                                refreshTastes();

                            }
                },
                new Response.ErrorListener() {

                        @Override
                            public void onErrorResponse(VolleyError volleyError) {

                                refreshTastes();
                            }
                }
        );
        AppController.getInstance().addToRequestQueue(postRequest);


    }
    private class MainAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {
                getResources().getString(R.string.proposal_tab),
                getResources().getString(R.string.tastes_tab),
                getResources().getString(R.string.watched_tab)};

        public MainAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:

                    return ProposalFragment.newInstance();
                case 1:

                    return TastesFragment.newInstance();
                case 2:

                    return WatchedFragment.newInstance();
                default:
                    break;
            }
            return null;
        }

        @Override
        public int getItemPosition(Object object){
            return POSITION_NONE;
        }


    }
    
}
