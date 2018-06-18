package oregonstate.abreuj.cs496final_restfulrecipes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;

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

public class newRecipe extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipe);


        // Get all ingredients:
        OkHttpClient mOkHttpClient = new OkHttpClient();
        HttpUrl reqUrl = HttpUrl.parse("https://cs496-final-abreuj.appspot.com/ingredient");

        // Put this into a function later
        // REQUEST
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
                final List<Map<String, String>> ingredients = new ArrayList<Map<String, String>>();

                try {
                    JSONArray items = new JSONArray(r);
                    for (int i = 0; i < items.length(); i++) {
                        HashMap<String, String> m = new HashMap<String, String>();
                        m.put("name", items.getJSONObject(i).getString("name"));
                        m.put("baseUnit", items.getJSONObject(i).getString("baseUnit"));
                        m.put("id", items.getJSONObject(i).getString("id"));
                        ingredients.add(m);
                    }

                    // Simple adapter to plug these values into our android studio:
                    final SimpleAdapter ingredientAdapter = new SimpleAdapter(
                            newRecipe.this,
                            ingredients,
                            R.layout.new_recipe_ingredient_checkbox,
                            new String[]{"name", "baseUnit"},
                            new int[]{R.id.new_recipe_ingredient_check, R.id.new_recipe_ingredient_unit_val}
                    );

                    final ListView lv = (ListView)(findViewById(R.id.new_rec_ingredient_list));

                    // Bind queried items to the ListView using adapter:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            lv.setAdapter(ingredientAdapter);





                        }
                    }); // end runOnUiThread


                } catch (JSONException e1) {
                    e1.printStackTrace();
                }


            } // end response successfull
        }); // end main ingredient request





    } // end onCreate




}
