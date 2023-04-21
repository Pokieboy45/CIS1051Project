package CIS1051.pokedex;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PokemonActivity extends AppCompatActivity {
    private TextView nameTextView;
    private TextView numberTextView;
    private TextView typesTextView;
    //private TextView type2TextView;
    private String url;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        url = getIntent().getStringExtra("url");
        nameTextView = findViewById(R.id.pokemon_name);
        numberTextView = findViewById(R.id.pokemon_number);
        typesTextView = findViewById(R.id.pokemon_types);
        //type2TextView = findViewById(R.id.pokemon_type2);

        load();

    }
    public void load(){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                typesTextView.setText("");

                try {
                    String NAME = response.getString("name");
                    nameTextView.setText(NAME.substring(0,1).toUpperCase()+NAME.substring(1));
                    numberTextView.setText(String.format("#%03d",response.getInt("id")));
                    JSONArray typeEntries = response.getJSONArray("types");
                    String types = "";
                    for(int i = 0; i<typeEntries.length(); i++) {
                        JSONObject typeEntry = typeEntries.getJSONObject(i);
                        int slot = typeEntry.getInt("slot");
                        String type = typeEntry.getJSONObject("type").getString("name");

                        if (slot == 1 ) {
                            types = type.substring(0,1).toUpperCase()+type.substring(1);
                        } else if (slot == 2) {
                            types = types + "\t\t\t\t" + type.substring(0,1).toUpperCase()+type.substring(1);
                        }
                        typesTextView.setText(types);
                    }
                } catch (JSONException e) {
                    Log.e("CIS1051", "Pokemon Json error", e);
                }
                try {
                    String imagURL = response.getJSONObject("sprites").getJSONObject("other").getJSONObject("home").getString("front_default");
                    Log.d("Test?", imagURL);
                    ImageView spriteView = findViewById(R.id.PokePic);;
                    Picasso.get().load(imagURL).into(spriteView);
                    String shinyURL = response.getJSONObject("sprites").getJSONObject("other").getJSONObject("home").getString("front_shiny");
                    Log.d("shiny url", shinyURL);
                    ImageView shinyView = findViewById(R.id.PokePicShiny);;
                    Picasso.get().load(shinyURL).into(shinyView);
                } catch (JSONException e) {
                    Log.e("Image", "IMAGE URL ERROR", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("CIS1051", "Pokemon details error");
            }
        });

        requestQueue.add(request);
    }
}