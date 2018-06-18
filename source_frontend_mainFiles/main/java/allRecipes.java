package oregonstate.abreuj.cs496final_restfulrecipes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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

public class allRecipes extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_recipes);

        // Submit Button Functionality:
        Button getButton = (Button) findViewById(R.id.all_rec_pull_button);
        getButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getRecipes();
            }
        });

    } // end onCreate method




    public void getRecipes() {

        // First, get all recipes
        OkHttpClient mOkHttpClient = new OkHttpClient();
        HttpUrl reqUrl = HttpUrl.parse("https://cs496-final-abreuj.appspot.com/recipe");
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

                // DO STUFF WITH BODY HERE

                final List<Map<String, String>> recipes = new ArrayList<Map<String, String>>();

                try {
                    JSONArray items = new JSONArray(r);
                    for (int i = 0; i < items.length(); i++) {
                        HashMap<String, String> m = new HashMap<String, String>();
                        m.put("name", items.getJSONObject(i).getString("name"));
                        m.put("author", items.getJSONObject(i).getString("author"));
                        m.put("description", items.getJSONObject(i).getString("description"));
                        m.put("hidden_directions", items.getJSONObject(i).getString("directions"));
                        m.put("hidden_id", items.getJSONObject(i).getString("id"));
                        recipes.add(m);
                    }

                    // Simple adapter to plug these values into our android studio:
                    final SimpleAdapter recipeAdapter = new SimpleAdapter(
                            allRecipes.this,
                            recipes,
                            R.layout.recipe_item,
                            new String[]{"name", "author", "description"},
                            new int[]{R.id.rec_name_val, R.id.rec_author_val, R.id.rec_description_val}
                    );

                    final ListView rlv = (ListView)(findViewById(R.id.v_recipes));

                    // Bind queried items to the ListView using adapter:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            rlv.setAdapter(recipeAdapter);

                            // Add onClick listener:
                            rlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                    // intent to send to edit activity:
                                    Intent intent = new Intent(allRecipes.this, recipeDisplay.class);

                                    // get data:
                                    Map<String, String> item = recipes.get(position);
                                    intent.putExtra("item_name", item.get("name"));
                                    intent.putExtra("item_author", item.get("author"));
                                    intent.putExtra("item_description", item.get("description"));
                                    intent.putExtra("item_directions", item.get("hidden_directions"));
                                    intent.putExtra("item_id", item.get("hidden_id"));



                                    // start edit activity
                                    startActivity(intent);
                                }
                            });
                        }
                    }); // end runOnUiThread

                } catch (JSONException e1) { e1.printStackTrace(); }
            } // end response successful
        }); // end main callback


    } // end getRecipes method


}
