package oregonstate.abreuj.cs496final_restfulrecipes;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
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

public class allIngredients extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_ingredients);


        // Submit Button Functionality:
        Button getButton = (Button) findViewById(R.id.all_ing_pull_button);
        getButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getIngredients();
            }
        });

    }



    public void getIngredients() {


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
                        m.put("caloriesPerUnit", items.getJSONObject(i).getString("caloriesPerUnit"));
                        m.put("costPerUnit", items.getJSONObject(i).getString("costPerUnit"));
                        m.put("id", items.getJSONObject(i).getString("id"));
                        ingredients.add(m);
                    }

                    // Simple adapter to plug these values into our android studio:
                    final SimpleAdapter ingredientAdapter = new SimpleAdapter(
                            allIngredients.this,
                            ingredients,
                            R.layout.ingredient_item,
                            new String[]{"name", "baseUnit", "caloriesPerUnit", "costPerUnit", "id"},
                            new int[]{R.id.row_name, R.id.row_baseUnit, R.id.row_calories, R.id.row_cost, R.id.ing_hidden_id}
                    );

                    final ListView lv = (ListView)(findViewById(R.id.v_ingredients));

                    // Add a header to the ListView
                    LayoutInflater inflater = getLayoutInflater();
                    ViewGroup header = (ViewGroup)inflater.inflate(R.layout.ingredient_item_header,lv,false);
                    lv.addHeaderView(header);

                    // Bind queried items to the ListView using adapter:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            lv.setAdapter(ingredientAdapter);

                            // Add onClick listener:
                            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                    // intent to send to edit activity:
                                    Intent intent = new Intent(allIngredients.this, editIngredient.class);

                                    // get data:
                                    Map<String, String> item = ingredients.get(position - 1);
                                    intent.putExtra("item_name", item.get("name"));
                                    intent.putExtra("item_unit", item.get("baseUnit"));
                                    intent.putExtra("item_calories", item.get("caloriesPerUnit"));
                                    intent.putExtra("item_cost", item.get("costPerUnit"));
                                    intent.putExtra("item_id", item.get("id"));

                                    // start edit activity
                                    startActivity(intent);
                                }
                            });
                        }
                    }); // end runOnUiThread


                } catch (JSONException e1) {
                    e1.printStackTrace();
                }


            } // end response successfull
        }); // end main ingredient request

    } // end getIngredients method

}
