package com.example.android.moviesapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent sentIntent = getIntent();
        Bundle sentBundle = sentIntent.getExtras();
        //Inflate Details Fragment & Send the Bundle to it
        PlaceholderFragment mDetailsFragment = new PlaceholderFragment();
        mDetailsFragment.setArguments(sentBundle);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_activity, mDetailsFragment)
                    .commit();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_settings) {
            Intent sett = new Intent(this, SettingsActivity.class);
            startActivity(sett);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
