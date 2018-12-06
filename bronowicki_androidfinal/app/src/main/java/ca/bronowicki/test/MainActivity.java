package ca.bronowicki.test;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View v)
    {
        if (v.getId() == R.id.score_button)
        {
            SharedPreferences preferences = getSharedPreferences("mysettings",
                    Context.MODE_PRIVATE);
            Toast.makeText(this, "SCORE: " + preferences.getInt("topScore",0), Toast.LENGTH_SHORT).show();
        }
        else
        {
            startActivity(new Intent(this, GameActivity.class));
        }
    }
}
