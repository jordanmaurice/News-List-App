package com.toogooddesign.newslist;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class NewsArticleAdapter extends ArrayAdapter<NewsArticle> {

    public NewsArticleAdapter(Context context, List<NewsArticle> newsArticles) {
        super(context, 0, newsArticles);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_article_list, null, false);
        }

        TextView title = (TextView) listItemView.findViewById(R.id.title_text);
        TextView author = (TextView) listItemView.findViewById(R.id.author_text);
        TextView date = (TextView) listItemView.findViewById(R.id.date);
        ImageView thumb = (ImageView) listItemView.findViewById(R.id.thumb);

        // Find the earthquake at the given position in the list of earthquakes
        NewsArticle currentArticle = getItem(position);

        String imgURL = currentArticle.getImageUrl();
        String articleName = currentArticle.getTitle();
        String authorName = currentArticle.getauthor();
        String datePublished = currentArticle.getdatePublished();
        ImageView theImage = (ImageView) listItemView.findViewById(R.id.thumb);
        Drawable drawable = new BitmapDrawable(currentArticle.getImage());
        theImage.setBackgroundDrawable(drawable);

        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        Date result1;
        try {
            result1 = df1.parse(datePublished);
        }
        catch(ParseException e){
            //Handle parse exception
            result1=null;
        }
        if(result1!=null){
            date.setText(result1.toString());
        }
        else{
            date.setText(datePublished);
        }


        title.setText(articleName);
        author.setText("By: "+authorName);

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }


}
