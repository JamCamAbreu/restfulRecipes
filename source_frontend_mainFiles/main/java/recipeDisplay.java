package oregonstate.abreuj.cs496final_restfulrecipes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class recipeDisplay extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_display);

        // Get values from intent:
        String nameVal = getIntent().getStringExtra("item_name");
        String authorVal = getIntent().getStringExtra("item_author");
        String descriptionVal = getIntent().getStringExtra("item_description");
        String directionsVal = getIntent().getStringExtra("item_directions");
        final String secretID = getIntent().getStringExtra("item_id");

        // Store static variables into views:
        final TextView nameView = (TextView)findViewById(R.id.rec_name_val);
        nameView.setText(nameVal);

        final TextView authView = (TextView)findViewById(R.id.rec_author_val);
        authView.setText(authorVal);

        final TextView descView = (TextView)findViewById(R.id.rec_description_val);
        descView.setText(descriptionVal);

        final TextView dirView = (TextView)findViewById(R.id.rec_directions_val);
        dirView.setText(directionsVal);
        dirView.setMovementMethod(new ScrollingMovementMethod()); // also make it scrollable


        // Query for ingredient information:

        // First, get all recipes
        OkHttpClient mOkHttpClient = new OkHttpClient();
        HttpUrl reqUrl = HttpUrl.parse("https://cs496-final-abreuj.appspot.com/recipe/" + secretID);
        Request request = new Request.Builder()
                .url(reqUrl)
                .build();

        // callBack for main request:
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String r = response.body().string();

                // Recipe information
                final List<Map<String, String>> portions = new ArrayList<Map<String, String>>();
                JSONArray ingredientList;

                try {
                    JSONObject j = new JSONObject(r);
                    ingredientList = j.getJSONArray("ingredientList");
                    for (int i = 0; i < ingredientList.length(); i++) {
                        HashMap<String, String> m = new HashMap<String, String>();
                        m.put("ingredient_name", ingredientList.getJSONObject(i).getString("ingredient_name"));
                        m.put("ingredient_unit", ingredientList.getJSONObject(i).getString("ingredient_unit"));
                        m.put("ingredient_id", ingredientList.getJSONObject(i).getString("ingredient_id"));
                        m.put("amount", ingredientList.getJSONObject(i).getString("amount"));
                        portions.add(m);
                    }

                    // Simple adapter to plug these values into our android studio:
                    final SimpleAdapter ingredientAdapter = new SimpleAdapter(
                            recipeDisplay.this,
                            portions,
                            R.layout.recipe_ingredient,
                            new String[]{"ingredient_name", "amount", "ingredient_unit"},
                            new int[]{R.id.rec_ing_name, R.id.rec_ing_amount, R.id.rec_ing_unit}
                    );

                    final ListView rlv = (ListView)(findViewById(R.id.rec_ing_list));

                    // Add a header to the ListView
                    LayoutInflater inflater = getLayoutInflater();
                    ViewGroup header = (ViewGroup)inflater.inflate(R.layout.recipe_ingredient_header,rlv,false);
                    rlv.addHeaderView(header);

                    // Bind queried items to the ListView using adapter:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            rlv.setAdapter(ingredientAdapter);

                        } // end run()
                    }); // end runOnUiThread


                } catch (JSONException e1) { e1.printStackTrace(); }
            } // end response successful
        }); // end main callback



    }




}
