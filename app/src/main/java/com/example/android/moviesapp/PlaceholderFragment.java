package com.example.android.moviesapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.moviesapp.com.example.android.moviesapp.data.MovieContract;
import com.squareup.picasso.Picasso;

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

public class PlaceholderFragment extends Fragment {

    private ArrayAdapter<String> mTrailerAdapter;
    private JSONArray array;
    private JSONArray array1;
    private TextView reviews;
    private ListView listView;
    private static DetailListener mListener;


    public PlaceholderFragment() {
        setHasOptionsMenu(true);
    }
    static void setDetailListener(DetailListener detailListener) {
        mListener = detailListener;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detailfragment, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.action_share && array!=null) {
            String key = null;
            try {
                key = array.getJSONObject(0).getString("key");
            } catch (JSONException e) {
                e.printStackTrace();
                return super.onOptionsItemSelected(item);
            }
            String url = "http://www.youtube.com/watch?v=" + key;
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, url);
            startActivity(Intent.createChooser(shareIntent, "send to "));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Bundle sentBundle = getArguments();
        final int id = sentBundle.getInt("movie_id", 0);
        final String poster = sentBundle.getString("poster");
        final String title = sentBundle.getString("title");
        final String overview = sentBundle.getString("overview");
        final String rate = sentBundle.getString("average");
        final String date = sentBundle.getString("date");
        ((TextView) rootView.findViewById(R.id.title)).setText(title);
        ((TextView) rootView.findViewById(R.id.overview)).setText(overview);
        ((TextView) rootView.findViewById(R.id.rate)).setText(rate + "/10");
        ((TextView) rootView.findViewById(R.id.date)).setText(date);
        ImageView imageView = ((ImageView) rootView.findViewById(R.id.poster));
        Picasso.with(getContext()).load("http://image.tmdb.org/t/p/w185/" + poster).into(imageView);
        Log.v(DetailActivity.class.getSimpleName(), "hereee " +id );
        reviews = (TextView)rootView.findViewById(R.id.review);
        mTrailerAdapter = new ArrayAdapter<String>(getContext(),
                R.layout.list_item_trailer,
                R.id.list_item_forecast_trailer,
                new ArrayList<String>());
        listView = (ListView)rootView.findViewById(R.id.trailer);
        listView.setAdapter(mTrailerAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    String key = array.getJSONObject(i).getString("key");
                    String url = "http://www.youtube.com/watch?v="+key;
                    Intent trailerIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    trailerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    startActivity(trailerIntent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Button button = (Button) rootView.findViewById(R.id.favorite);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Cursor cursor = getContext().getContentResolver().query(
                        MovieContract.MovieEntry.CONTENT_URI,
                        new String[]{MovieContract.MovieEntry.MOVIE_ID},
                        MovieContract.MovieEntry.MOVIE_ID + " = ?",
                        new String[]{""+id},
                        null);
                if (cursor.moveToFirst()) {
                    getContext().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI
                            , MovieContract.MovieEntry.MOVIE_ID + " = ?",
                            new String[]{""+id});
                    if(mListener!=null)
                        mListener.updateMain();
                    cursor.close();
                }else {
                    ContentValues values = new ContentValues();
                    values.put(MovieContract.MovieEntry.MOVIE_ID, id);
                    values.put(MovieContract.MovieEntry.MOVIE_POSTER, poster);
                    values.put(MovieContract.MovieEntry.MOVIE_TITLE, title);
                    values.put(MovieContract.MovieEntry.MOVIE_OVERVIEW, overview);
                    values.put(MovieContract.MovieEntry.MOVIE_RATE, rate);
                    values.put(MovieContract.MovieEntry.MOVIE_DATE, date);
                    getContext().getContentResolver().insert(
                            MovieContract.MovieEntry.CONTENT_URI, values);
                    cursor.close();
                }
            }
        });
        return rootView;
    }
    @Override
    public void onStart() {
        super.onStart();
        FetchTrailers trailersTask = new FetchTrailers();
        FetchReviews reviewsTask = new FetchReviews();
        Bundle sentBundle = getArguments();
        String id = "" + sentBundle.getInt("movie_id", 0);
        reviewsTask.execute(id);
        trailersTask.execute(id);
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public class FetchTrailers extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchTrailers.class.getSimpleName();

        @Override
        protected String[] doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            String id = params[0];
            final String key = BuildConfig.OPEN_WEATHER_MAP_API_KEY;

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;


            try {

                String ur = "http://api.themoviedb.org/3/movie/" + id + "/videos?api_key=" + key;
                URL url = new URL(ur);

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
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

            try {
                JSONObject forecastJson = new JSONObject(forecastJsonStr);
                array = forecastJson.getJSONArray("results");
                String[] trailers = new String[array.length()];
                Log.d(LOG_TAG, "number of trailers "+trailers.length );
                for (int i = 0; i < trailers.length; i++)
                    trailers[i] = "Trailer " + (i+1);
                return trailers;
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                mTrailerAdapter.clear();
                for(String dayForecastStr : result) {
                    mTrailerAdapter.add(dayForecastStr);
                }

                setListViewHeightBasedOnChildren(listView);
            }
        }
    }

    public class FetchReviews extends AsyncTask<String, Void, String> {

        private final String LOG_TAG = FetchTrailers.class.getSimpleName();

        @Override
        protected String doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            String id = params[0];
            final String key = BuildConfig.OPEN_WEATHER_MAP_API_KEY;

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;


            try {

                String ur = "http://api.themoviedb.org/3/movie/"+id+"/reviews?api_key="+ key;
                URL url = new URL(ur);

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
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

            try {
                JSONObject forecastJson = new JSONObject(forecastJsonStr);
                array1 = forecastJson.getJSONArray("results");
                String result = "";
                for (int i = 0; i < array1.length(); i++)
                    result += array1.getJSONObject(i).getString("content")+"\n";
                return result;
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Log.d(DetailActivity.class.getSimpleName(), "onPostExecute: ");
                reviews.setText(result);
            }
        }

    }
}