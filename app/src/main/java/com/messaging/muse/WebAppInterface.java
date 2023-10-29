package com.messaging.muse;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.webkit.JavascriptInterface;
import android.widget.ImageView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

public class WebAppInterface {
    Context mContext;
    URL url=null;
    ImageView imageView;
    /** Instantiate the interface and set the context. */
    WebAppInterface(Context c) {
        mContext = c;
    }

    /** Show a toast from the web page. */
    @JavascriptInterface
    public void showToast(String toast) {
        //Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        try {
            url=new URL(toast);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        HttpURLConnection connection=null;
        try {
            connection= (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            connection.connect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        InputStream input=null;
        try {
            input=connection.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Random rand = new Random();
        int randNo = rand.nextInt(100000);
        Bitmap imgBitmap = BitmapFactory.decodeStream(input);
        String imgBitmapPath = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), imgBitmap, "IMG:" + randNo, null);
        Uri imgBitmapUri = Uri.parse(imgBitmapPath);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imgBitmapUri);
        shareIntent.setType("image/png");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(mContext,shareIntent,null);
    }




}