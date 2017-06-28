package com.toogooddesign.newslist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{
    String selectedDrop;
    String theFinalURL;
    ProgressBar theBar;
    Boolean hasInternet;
    ArrayList<NewsArticle> news_articles_array_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hasInternet = checkConnect();
        setSpinner();
    }

    public Boolean checkConnect(){
        ConnectivityManager connection = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connection.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {

            View loadingIndicator = findViewById(R.id.progressBar);
            loadingIndicator.setVisibility(View.VISIBLE);
            TextView connectionT = (TextView) findViewById(R.id.connectionText);
            connectionT.setVisibility(View.GONE);
            return true;
        }
        else {
            View loadingIndicator = findViewById(R.id.progressBar);
            loadingIndicator.setVisibility(View.INVISIBLE);
            TextView connectionT = (TextView) findViewById(R.id.connectionText);
            connectionT.setText("No internet connection, try again later");
            connectionT.setVisibility(View.VISIBLE);
            return false;
        }
    }

    public void getURL(String drop){
        getNewsTask task = new getNewsTask();

        switch(drop) {
            case "Ars Technica":
                theFinalURL = "https://newsapi.org/v1/articles?source=ars-technica&sortBy=top&apiKey=81afe753178c485aa164181478bf2325";
                task.execute();
                break;
            case "CNN":
                theFinalURL = "https://newsapi.org/v1/articles?source=cnn&sortBy=top&apiKey=81afe753178c485aa164181478bf2325";
                task.execute();
                break;
            case "Google":
                theFinalURL = "https://newsapi.org/v1/articles?source=google-news&sortBy=top&apiKey=81afe753178c485aa164181478bf2325";
                task.execute();
                break;
            case "IGN":
                theFinalURL = "https://newsapi.org/v1/articles?source=ign&sortBy=top&apiKey=81afe753178c485aa164181478bf2325";
                task.execute();
                break;
            case "TechCrunch":
                theFinalURL = "https://newsapi.org/v1/articles?source=techcrunch&sortBy=top&apiKey=81afe753178c485aa164181478bf2325";
                task.execute();
                break;
            case "Reddit":
                theFinalURL = "https://newsapi.org/v1/articles?source=reddit-r-all&sortBy=top&apiKey=81afe753178c485aa164181478bf2325";
                task.execute();
                break;
            case "The Verge":
                theFinalURL = "https://newsapi.org/v1/articles?source=the-verge&sortBy=top&apiKey=81afe753178c485aa164181478bf2325";
                task.execute();
                break;
            case "TIME":
                theFinalURL = "https://newsapi.org/v1/articles?source=time&sortBy=top&apiKey=81afe753178c485aa164181478bf2325";
                task.execute();
                break;
        }
    }

    public void setSpinner(){
        Spinner theSpin = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sources_list, R.layout.spinner);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        theSpin.setAdapter(adapter);

        theSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View v,
                                       int position, long id) {
                selectedDrop = adapter.getItemAtPosition(position).toString();


                if(hasInternet) {
                    getURL(selectedDrop);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    public class getNewsTask extends AsyncTask<URL,Void,Void> {
        @Override
        protected void onPreExecute(){
            theBar = (ProgressBar) findViewById(R.id.progressBar);
            theBar.setVisibility(View.VISIBLE);
        }

        protected Void doInBackground(URL... urls){
            URL newURL = createURL(theFinalURL);
            String jsonReturned;
            try{
                news_articles_array_list.clear();
                jsonReturned = makeRequest(newURL);
                extractInfo(jsonReturned);
            }
            catch(IOException e){
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void result){
            updateUI();
            theBar = (ProgressBar) findViewById(R.id.progressBar);
            theBar.setVisibility(View.GONE);
        }
    }

    private void updateUI(){
        ListView newsArticleListView = (ListView) findViewById(R.id.list);

        final NewsArticleAdapter adapter = new NewsArticleAdapter(this, news_articles_array_list);

        newsArticleListView.setAdapter(adapter);
        newsArticleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                NewsArticle currentArticle = adapter.getItem(position);
                Uri articleUri = Uri.parse(currentArticle.getURL());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, articleUri);
                startActivity(websiteIntent);
            }
        });
    }

    //Takes string as an argument and returns a URL object
    private URL createURL(String stringUrl) {
        URL url;
        try {
            url = new URL(stringUrl);
        }
        catch (MalformedURLException exception) {
            Log.e("meh", "Error with creating URL", exception);
            return null;
        }
        return url;
    }

    private String makeRequest(URL url) throws IOException {
        String jsonReturned = "";
        HttpURLConnection urlConnection = null;
        InputStream input = null;
        try{
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(20000);
            urlConnection.connect();
            int code = urlConnection.getResponseCode();
            if(code!=200){
                               return jsonReturned = "";
            }
            else{
                Log.e("meh",jsonReturned);
                input = urlConnection.getInputStream();
                jsonReturned = parseStream(input);
            }
        }

        catch(IOException e){
            e.printStackTrace();
        }
        finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (input != null) {
                input.close();
            }
        }
        return jsonReturned;
    }

    //Actively parses the return http request and translates it into a readable string
    private String parseStream(InputStream input) throws IOException{
        StringBuilder output = new StringBuilder();
        if (input != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(input, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    //Takes the string of JSON and returns (currently the first of the articles)
    private Void extractInfo(String jsonOutput){
        if(TextUtils.isEmpty(jsonOutput)){
            return null;
        }
        try {
            JSONObject baseJSON = new JSONObject(jsonOutput);
            JSONArray articleArray = baseJSON.getJSONArray("articles");
            for(int i = 0;i<articleArray.length();i++) {
                JSONObject firstArticle = articleArray.getJSONObject(i);
                String title = firstArticle.getString("title");
                String author = firstArticle.optString("author");
                String url = firstArticle.getString("url");
                String date = firstArticle.optString("publishedAt");
                String imageURL = firstArticle.getString("urlToImage");
                Bitmap bitmapScaled;
                Bitmap icon = null;

                try {
                    InputStream in = new java.net.URL(imageURL).openStream();
                    icon = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }

                if (icon!=null) {
                    bitmapScaled = Bitmap.createScaledBitmap(icon, 1250, 1250, true);
                }
                else{
                    bitmapScaled = BitmapFactory.decodeResource(null, R.drawable.questionmark);
                }
                news_articles_array_list.add(new NewsArticle(title, author, date, url,imageURL,bitmapScaled));
            }
            return null;
            }

        catch(JSONException e){
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
