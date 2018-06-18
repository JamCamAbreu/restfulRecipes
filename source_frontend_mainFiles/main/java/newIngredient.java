package oregonstate.abreuj.cs496final_restfulrecipes;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class newIngredient extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_ingredient);

        // Fill the layout with the passed variables:
        // NAME
        final TextInputEditText nameVal = (TextInputEditText)findViewById(R.id.new_name_val);
        nameVal.setHint("Flour");

        // UNIT
        final TextInputEditText unitVal = (TextInputEditText)findViewById(R.id.new_unit_val);
        unitVal.setHint("cups");

        // CALORIES
        final TextInputEditText CalVal = (TextInputEditText)findViewById(R.id.new_cal_val);
        CalVal.setHint("455");

        // COST
        final TextInputEditText CostVal = (TextInputEditText)findViewById(R.id.new_cost_val);
        CostVal.setHint("0.2");


        // Submit Button
        Button submitButton = findViewById(R.id.new_ing_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Values entered by user (default blank):
                String newName = "";
                String newUnit = ", ";
                String newCal = ", ";
                String newCost = ", ";

                // BUILD UP STRINGS OPTIONALLY OMIT SOME OR NONE
                // Name
                if (!(nameVal.getText().toString().isEmpty())) { newName += "\"name\": \"" + nameVal.getText().toString() + "\""; }
                else { newName += "\"name\": \"" + "undefined" + "\""; }

                // Base Unit
                if (!(unitVal.getText().toString().isEmpty())) { newUnit += "\"baseUnit\": \"" + unitVal.getText().toString() + "\""; }
                else { newUnit += "\"baseUnit\": \"" + "undefined" + "\""; }

                // Calories
                if (!(CalVal.getText().toString().isEmpty())) { newCal += "\"caloriesPerUnit\": " + CalVal.getText().toString(); }
                else { newCal += "\"caloriesPerUnit\": " + "0"; }

                // COST
                if (!(CostVal.getText().toString().isEmpty())) { newCost += "\"costPerUnit\": " + CostVal.getText().toString(); }
                else { newCost += "\"costPerUnit\": " + "0"; }

                // JSON body is made up of all or some of the above
                String JSONString = "{" + newName + newUnit + newCal + newCost + "}";

                // URL for patch:
                String fullURL = "https://cs496-final-abreuj.appspot.com/ingredient";
                HttpUrl reqUrl = HttpUrl.parse(fullURL);

                // Send patch request:
                final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(JSON, JSONString);
                Request request = new Request.Builder()
                        .url(reqUrl)
                        .post(body)
                        .build();

                // callBack for main request:
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        // Future: display error:
                        // if (response.code() != 200)

                        // Do something with response
                        goto_mainActivity();
                    }
                }); // end callback
            } // end onclick
        }); // end submit button listener


    } // end onCreate Method




    void goto_mainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
