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
    private TextView hpTextView;
    private TextView atkTextView;
    private TextView defTextView;
    private TextView spatkTextView;
    private TextView spdefTextView;
    private TextView spdTextView;
    private TextView Tooltip;

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
        hpTextView    = findViewById(R.id.pokemon_hp);
        atkTextView   = findViewById(R.id.pokemon_atk);
        defTextView   = findViewById(R.id.pokemon_def);
        spdefTextView = findViewById(R.id.pokemon_spdef);
        spatkTextView = findViewById(R.id.pokemon_spatk);
        spdTextView   = findViewById(R.id.pokemon_spd);
        Tooltip       = findViewById(R.id.tooltip);
        load();

    }
    public void load(){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                typesTextView.setText("");

                try {
                    String name1 = response.getString("name");
                    String name = name1.substring(0,1).toUpperCase()+name1.substring(1);
                    nameTextView.setText(name);
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
                    //Log.d("Test?", imagURL);
                    ImageView spriteView = findViewById(R.id.PokePic);;
                    Picasso.get().load(imagURL).into(spriteView);
                    String shinyURL = response.getJSONObject("sprites").getJSONObject("other").getJSONObject("home").getString("front_shiny");
                    //Log.d("shiny url", shinyURL);
                    ImageView shinyView = findViewById(R.id.PokePicShiny);;
                    Picasso.get().load(shinyURL).into(shinyView);
                } catch (JSONException e) {
                    Log.e("Image", "IMAGE URL ERROR", e);
                }
                try {
                    //int hp, atk, def, spdef, spatk, spd;
                    Log.d("test", "ints created");
                    int hp       = response.getJSONArray("stats").getJSONObject(0).getInt("base_stat");
                    int atk      = response.getJSONArray("stats").getJSONObject(1).getInt("base_stat");
                    int def      = response.getJSONArray("stats").getJSONObject(2).getInt("base_stat");
                    int spatk    = response.getJSONArray("stats").getJSONObject(3).getInt("base_stat");
                    int spdef    = response.getJSONArray("stats").getJSONObject(4).getInt("base_stat");
                    int spd      = response.getJSONArray("stats").getJSONObject(5).getInt("base_stat");

                    String str_hp       = Integer.toString(hp);
                    String str_atk      = Integer.toString(atk);
                    String str_def      = Integer.toString(def);
                    String str_spatk    = Integer.toString(spatk);
                    String str_spdef    = Integer.toString(spdef);
                    String str_spd      = Integer.toString(spd);

                    if (str_hp.length()    <3){str_hp    = "  " + str_hp;}
                    if (str_atk.length()   <3){str_atk   = "  " + str_atk;}
                    if (str_def.length()   <3){str_def   = "  " + str_def;}
                    if (str_spatk.length() <3){str_spatk = "  " + str_spatk;}
                    if (str_spdef.length() <3){str_spdef = "  " + str_spdef;}
                    if (str_spd.length()   <3){str_spd   = "  " + str_spd;}

                    hpTextView.setText(   str_hp   );
                    atkTextView.setText(  str_atk  );
                    defTextView.setText(  str_def  );
                    spatkTextView.setText(str_spatk);
                    spdefTextView.setText(str_spdef);
                    spdTextView.setText(  str_spd  );

                    String fight = "";
                    String name1 = response.getString("name");
                    String name = name1.substring(0,1).toUpperCase()+name1.substring(1);
                    if (atk > spatk){fight = "physical";}
                    else if (spatk > atk) {fight = "special";}
                    else {fight = "mixed";}
                    String tip = name + " is best used as a " + fight + " attacker.";
                    Tooltip.setText(tip);

                } catch (JSONException e){
                    Log.e("Stats", "Stats Data ERROR", e);
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