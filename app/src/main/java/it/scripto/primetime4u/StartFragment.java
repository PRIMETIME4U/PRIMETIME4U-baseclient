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
import com.dexafree.materialList.cards.model.WelcomeCard;
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
    private WelcomeCard welcome;


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

        //leggo l'email dall'activity sottostante al fragment
        StartActivity base = (StartActivity) this.getActivity();
        String username=base.getAccount();


        start_material_list_view = (MaterialListView) view.findViewById(R.id.start_material_list_view);
        //start_material_list_view.setCardAnimation(IMaterialView.CardAnimation.SWING_BOTTOM_IN);

        welcome = new WelcomeCard(context);
        welcome.setTitle("Dear "+username);
        welcome.setDescription("Welcome to PRIMETIME4U\n\n");
        /*welcome.setLeftButtonText("No, thanks");
        welcome.setRightButtonText("Okay");*/
        welcome.setDismissible(false);
        /*welcome.setOnRightButtonPressedListener(new OnButtonPressListener() {
            @Override
            public void onButtonPressedListener(View view, Card card) {
                //ho l'email, devo registrarlo al server




            }
        });
        welcome.setOnLeftButtonPressedListener(new OnButtonPressListener() {
            @Override
            public void onButtonPressedListener(View view, Card card) {
                Toast.makeText(context,"Invece dovresti",Toast.LENGTH_LONG).show();
            }
        });*/


        start_material_list_view.add(welcome);
        if (username.equals("Utente sconosciuto")) return view;

        if (username.charAt(username.length()-1)=='D' && username.charAt(username.length()-2)=='L' && username.charAt(username.length()-3)=='O'){
            //se è vecchio
            int end = username.length()-3;
            username = username.substring(0,end);
            Intent i = new Intent(context,MainActivity.class);
            i.putExtra("email",username);
            startActivity(i);

        }
        else new JsonPost().execute(username);
        return view;
    }

    class JsonPost extends AsyncTask<String,Void,Void>{

        int code;

        @Override
        protected Void doInBackground(String... params) {
            final String email = params[0];
            JSONObject obj= new JSONObject();
            String subscribeUrl = "http://hale-kite-786.appspot.com/api/subscribe/";
            /**
             * ATTENZIONE, IL JSON VA MANDATO COMPLETO PER REGISTRARSI CORRETTAMENTE
             * in caso di errori o utente gia' registrato il server risponde con un altro json
             */
            try {
                obj.put("userId", email);
                obj.put("userName", "giovanni");
                obj.put("userBirthYear", "1990");
                obj.put("userGender", "m");
            } catch (JSONException e) {
                e.printStackTrace();
            }

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
                            //gestione risultato subscribe
                            try{
                                code = response.getInt("code");
                                if (code==1){
                                    //utente già iscritto, no tutorial
                                    Toast.makeText(context, "Bentornato "+email,Toast.LENGTH_LONG).show();
                                }
                                if (code==0){
                                    //nuovo utente, si tutorial
                                    //TODO: qui è il punto in cui si dovrà far partire l'activity del tutorial
                                    Toast.makeText(context,"Benvenuto nuovo utente",Toast.LENGTH_LONG).show();
                                }
                            }
                            catch (JSONException e){

                            }

                            //chiamo la mainactivity e passo l'email
                            Intent i = new Intent(context,MainActivity.class);
                            i.putExtra("email",email);
                            startActivity(i);

                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            System.out.println("Error.Response");
                            System.out.println(error.toString());
                        }
                    }
            );


            AppController.getInstance().addToRequestQueue(postRequest);


            return null;
        }

        protected void onPostExecute(Void v){

        }

    }

}
