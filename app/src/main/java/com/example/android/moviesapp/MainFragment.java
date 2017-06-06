package com.example.android.moviesapp;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.android.moviesapp.com.example.android.moviesapp.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Bo2o on 11/1/2016.
 */
public class MainFragment extends Fragment implements DetailListener {

    private final String TAG = MainFragment.class.getSimpleName();
    private ImageAdapter myAdapter;
    private JSONArray array;
    private Cursor cursor;
    private MovieListener mListener;

    public MainFragment(){

    }
    void setMovieListener(MovieListener movieListener) {
        this.mListener = movieListener;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        PlaceholderFragment.setDetailListener(this);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridview = (GridView) rootView.findViewById(R.id.gridview);
        Point size = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        int width = size.x;
        gridview.setColumnWidth(width/2);
        myAdapter = new ImageAdapter(getContext(), new ArrayList<String>());
        gridview.setAdapter(myAdapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Intent intent = null;
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String sort = prefs.getString(getString(R.string.pref_sort_key),
                        getString(R.string.pref_sort_default));
                String title = "";
                String poster = "";
                String overview = "";
                String average = "";
                String date = "";
                int id = 0;
                if(sort.equals("favorite")) {
                    cursor.moveToPosition(i);
                    cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.MOVIE_TITLE));
                    title = cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.MOVIE_TITLE));
                    poster = cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.MOVIE_POSTER));
                    overview = cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.MOVIE_OVERVIEW));
                    average = cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.MOVIE_RATE));
                    date = cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.MOVIE_DATE));
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.MOVIE_ID));
                } else {
                    try {
                        title = array.getJSONObject(i).getString("original_title");
                        poster = array.getJSONObject(i).getString("poster_path");
                        overview = array.getJSONObject(i).getString("overview");
                        average = array.getJSONObject(i).getString("vote_average");
                        date = array.getJSONObject(i).getString("release_date");
                        id = array.getJSONObject(i).getInt("id");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                mListener.setDetail(title, poster, overview, average, date, id);
            }
        });
        return rootView;
    }
    public void onStart() {
        super.onStart();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort = prefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_default));
        if(sort.equals("favorite"))
            new FetchFavorite().execute();
        else {
            new Fetch().execute(sort);
        }
    }

    @Override
    public void updateMain() {
        onStart();
    }


    class Fetch extends AsyncTask<String, Void, ArrayList<String> > {
        private final String LOG_TAG = Fetch.class.getSimpleName();

        @Override
        protected ArrayList<String> doInBackground(String... params) {

            if(params.length==0)
                return null;
            String sort = params[0];
            ArrayList<String> photos;
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {



                final String key = BuildConfig.OPEN_WEATHER_MAP_API_KEY;
                String ur = "http://api.themoviedb.org/3/movie/"+sort+"?api_key="+key;
                URL url = new URL(ur);
                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it'forecastfragment JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
                try {
                    JSONObject forecastJson = new JSONObject(forecastJsonStr);
                    array = forecastJson.getJSONArray("results");
                    photos = new ArrayList<String>(array.length());
                    for(int i=0; i<array.length(); i++){
                        photos.add(array.getJSONObject(i).getString("poster_path"));
//                        Log.e(LOG_TAG, "the array "+i+" "+photos.get(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
//                return getWeatherDataFromJson(forecastJsonStr, 7);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(getString(R.string.pref_sort_key),"favorite");
                editor.commit();
                return null;
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return photos;
        }
        protected void onPostExecute(ArrayList<String> strings) {
            if(strings!=null && strings.size()>0) {
                myAdapter.getPhotos().clear();
                for(int i=0; i<strings.size(); i++)
                    myAdapter.getPhotos().add(strings.get(i));
                myAdapter.notifyDataSetChanged();
            } else {
                onStart();
            }
        }
    }
    class FetchFavorite extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
             cursor = getContext().getContentResolver().query(
                    MovieContract.MovieEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);
            return cursor;
        }
        protected void onPostExecute(Cursor cursor) {
            if(cursor!=null) {
                myAdapter.getPhotos().clear();
                if (cursor.moveToFirst()) {
                    do {
                        String x =cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.MOVIE_POSTER));
                        myAdapter.getPhotos().add(x);
                    } while (cursor.moveToNext());
                }
                myAdapter.notifyDataSetChanged();
            }
        }
    }
}
