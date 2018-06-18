package oregonstate.abreuj.cs496final_restfulrecipes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




    // Button 1
    Button button1 = findViewById(R.id.newIngredient);
    button1.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, newIngredient.class);
            startActivity(intent);
        }
    });


    // Button 2
    Button button2 = findViewById(R.id.newRecipe);
    button2.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, newRecipe.class);
            startActivity(intent);
        }
    });


    // Button 3
    Button button3 = findViewById(R.id.ingredients);
    button3.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, allIngredients.class);
            startActivity(intent);
        }
    });



    // Button 4
    Button button4 = findViewById(R.id.recipes);
    button4.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, allRecipes.class);
            startActivity(intent);
        }
    });



    } // end MainActivity class
}
