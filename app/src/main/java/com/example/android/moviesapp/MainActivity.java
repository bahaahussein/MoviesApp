package com.example.android.moviesapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity implements MovieListener {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    boolean mIsTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainFragment mMainFragment = new MainFragment();
        mMainFragment.setMovieListener(this);
//        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, mMainFragment,"")
                    .commit();
//        }
        if(null!=findViewById(R.id.detail_activity))
            mIsTwoPane = true;
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

            if(id==R.id.action_settings){
                Intent sett = new Intent(this, SettingsActivity.class);
                startActivity(sett);
                return true;
            }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setDetail(String title, String poster, String overview, String average, String date, int id) {
        if(mIsTwoPane) {
            PlaceholderFragment mDetailsFragment = new PlaceholderFragment();
            Bundle extras= new Bundle();
            extras.putString("title",title);
            extras.putString("poster",poster);
            extras.putString("overview",overview);
            extras.putString("average",average);
            extras.putString("date",date);
            extras.putInt("movie_id",id);
            mDetailsFragment.setArguments(extras);
            getSupportFragmentManager().beginTransaction().replace(R.id.detail_activity,mDetailsFragment,"").commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra("title", title)
                    .putExtra("poster", poster)
                    .putExtra("overview", overview)
                    .putExtra("average", average)
                    .putExtra("date", date)
                    .putExtra("movie_id", id);
            startActivity(intent);
        }
    }
}
