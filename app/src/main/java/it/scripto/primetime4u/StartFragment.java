package it.scripto.primetime4u;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dexafree.materialList.cards.model.Card;
import com.dexafree.materialList.controller.OnButtonPressListener;
import com.dexafree.materialList.view.IMaterialView;
import com.dexafree.materialList.view.MaterialListView;



import org.json.JSONException;
import org.json.JSONObject;

import primetime4u.app.AppController;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class StartFragment extends BaseFragment {


    private MaterialListView start_material_list_view;

    private boolean launchmain;

    public StartFragment() {
        // Required empty public constructor
    }
    public static StartFragment newInstance() {
        return new StartFragment();
    }
    @Override
    protected String getTagLog() {
        return "StartFragment";
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_start;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        launchmain=false;

        StartActivity base = (StartActivity) this.getActivity();
        final String username=base.getAccount();


        start_material_list_view = (MaterialListView) view.findViewById(R.id.start_material_list_view);
        //start_material_list_view.setCardAnimation(IMaterialView.CardAnimation.SWING_BOTTOM_IN);

        WelcomeCard welcome = new WelcomeCard(context);
        welcome.setTitle("Dear "+username);
        welcome.setDescription("Welcome to PRIMETIME4U\n Please, sign-in in order to use our app\n In futuro quest'operazione sara' automatica");
        welcome.setLeftButtonText("No, thanks");
        welcome.setRightButtonText("Okay");
        welcome.setOnRightButtonPressedListener(new OnButtonPressListener() {
            @Override
            public void onButtonPressedListener(View view, Card card) {
                //ho l'email, devo registrarlo al server
                /**
                 * REGISTRATION JSON:
                 * {"user_id":"username", "user_name":null, "user_birth_year":null, "user_gender":null}
                 */

                new JsonPost().execute(username);

            }
        });
        welcome.setOnLeftButtonPressedListener(new OnButtonPressListener() {
            @Override
            public void onButtonPressedListener(View view, Card card) {
                System.out.println("You should sign in");
            }
        });
        Toast.makeText(context,"Accesso in corso....",Toast.LENGTH_LONG).show();

        start_material_list_view.add(welcome);



        return view;
    }

    class JsonPost extends AsyncTask<String,Void,Void>{

        int code;

        @Override
        protected Void doInBackground(String... params) {
            String email = params[0];
            JSONObject obj= new JSONObject();
            String subscribeUrl = "http://hale-kite-786.appspot.com/api/subscribe/";
            /**TODO:
             * ATTENZIONE, IL JSON VA MANDATO COMPLETO PER REGISTRARSI CORRETTAMENTE
             * in caso di errori o utente gia' registrato il server risponde con un altro json
             */
            try {
                obj.put("user_id", email);
                obj.put("user_name", "giovanni");
                obj.put("user_birth_year", "1990");
                obj.put("user_gender", "m");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            System.out.println(obj);
            /**
             * Creates a new request.
             * @param method the HTTP method to use
             * @param url URL to fetch the JSON from
             * @param jsonRequest A {@link org.json.JSONObject} to post with the request. Null is allowed and
             *   indicates no parameters will be posted along with request.
             * @param listener Listener to receive the JSON response
             * @param errorListener Error listener, or null to ignore errors.
             */
            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, subscribeUrl, obj,
                    new Response.Listener<JSONObject>()
                    {
                        @Override
                        public void onResponse(JSONObject response) {
                            // response
                            Log.d("Response", response.toString());

                            //TODO: gestire la risposta, fare parsing della risposta e se correttamente iscritto, lanciare la main
                            Intent i = new Intent(context,MainActivity.class);
                            startActivity(i);

                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            System.out.println("Error.Response");
                        }
                    }
            );


            AppController.getInstance().addToRequestQueue(postRequest);


            return null;
        }

        protected void onPostExecute(Void v){
            launchmain=true;
        }

    }

}
