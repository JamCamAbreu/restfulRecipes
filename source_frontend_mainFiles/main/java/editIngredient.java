package oregonstate.abreuj.cs496final_restfulrecipes;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class editIngredient extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ingredient);


        // Fill the layout with the passed variables:
        // NAME
        final TextInputEditText nameVal = (TextInputEditText)findViewById(R.id.edit_name_val);
        nameVal.setHint(getIntent().getStringExtra("item_name"));

        // UNIT
        final TextInputEditText unitVal = (TextInputEditText)findViewById(R.id.edit_unit_val);
        unitVal.setHint(getIntent().getStringExtra("item_unit"));

        // CALORIES
        final TextInputEditText CalVal = (TextInputEditText)findViewById(R.id.edit_cal_val);
        CalVal.setHint(getIntent().getStringExtra("item_calories"));

        // COST
        final TextInputEditText CostVal = (TextInputEditText)findViewById(R.id.edit_cost_val);
        CostVal.setHint(getIntent().getStringExtra("item_cost"));

        // ID
        final String secretID = getIntent().getStringExtra("item_id");


        // Submit Button
        Button submitButton = findViewById(R.id.edit_ing_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Values entered by user (default blank):
                String newName = "";
                String newUnit = "";
                String newCal = "";
                String newCost = "";
                int counter = 0;

                // BUILD UP STRINGS OPTIONALLY OMIT SOME OR NONE
                if (!(nameVal.getText().toString().isEmpty())) {
                    newName += "\"name\": \"" + nameVal.getText().toString() + "\"";
                    counter++;
                }

                if (!(unitVal.getText().toString().isEmpty())) {
                    if (counter > 0)
                        newUnit += ", ";
                    newUnit += "\"baseUnit\": \"" + unitVal.getText().toString() + "\"";
                    counter++;
                }

                if (!(CalVal.getText().toString().isEmpty())) {
                    if (counter > 0)
                        newCal += ", ";
                    newCal += "\"caloriesPerUnit\": \"" + CalVal.getText().toString() + "\"";
                    counter++;
                }

                if (!(CostVal.getText().toString().isEmpty())) {
                    if (counter > 0)
                        newCost += ", ";
                    newCost += "\"costPerUnit\": \"" + CostVal.getText().toString() + "\"";
                    counter++;
                }

                // JSON body is made up of all or some of the above
                String JSONString = "{" + newName + newUnit + newCal + newCost + "}";

                // URL for patch:
                String fullURL = "https://cs496-final-abreuj.appspot.com/ingredient/" + getIntent().getStringExtra("item_id");
                HttpUrl reqUrl = HttpUrl.parse(fullURL);

                // Send patch request:
                final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(JSON, JSONString);
                Request request = new Request.Builder()
                        .url(reqUrl)
                        .patch(body)
                        .build();

                // callBack for main request:
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        //String r = response.body().string();
                        // Do something with response
                        goto_mainActivity();
                    }
                }); // end callback
            } // end onclick
        }); // end submit button listener



        // Delete Button
        Button deleteButton = findViewById(R.id.delete_ing_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // URL for delete:
                String fullURL = "https://cs496-final-abreuj.appspot.com/ingredient/" + getIntent().getStringExtra("item_id");
                HttpUrl reqUrl = HttpUrl.parse(fullURL);

                // Send delete request:
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(reqUrl)
                        .delete()
                        .build();

                // callBack for main request:
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        //String r = response.body().string();
                        // Do something with response
                        goto_mainActivity();
                    }
                }); // end callback
            } // end onclick
        }); // end delete button listener


    } // end onCreate method

    void goto_mainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


}
