package com.toogooddesign.newslist;


import android.graphics.Bitmap;

public class NewsArticle {


    private String title;
    private String author;
    private String datePublished;
    private String url;
    private String imageUrl;
    private Bitmap img;
    public NewsArticle(String mTitle,String mAuthor, String mDatePublished, String mUrl, String mImageUrl,Bitmap mImage){
        if(mTitle == "null"){
            title = "No title specified";
        }
        else{
            title = mTitle;
        }
        if(mAuthor == "null"){
            author = "Unknown Author";
        }
        else{
            author = mAuthor;
        }
        if(mDatePublished == "null"){
            datePublished = "Unknown Published Date";
        }
        else{
            datePublished=mDatePublished;
        }
        img = mImage;
        url = mUrl;
        imageUrl = mImageUrl;

    }

    public String getTitle(){
        return title;
    }
    public void  setBitmap(Bitmap in){
        img = in;
    }
    public String getauthor(){
        return author;
    }

    public String getdatePublished(){
        return datePublished;
    }

    public Bitmap getImage(){
        return img;
    }
    public String getURL(){
        return url;
    }

    public String getImageUrl(){
        return imageUrl;
    }
}
