package com.messaging.muse;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import kotlin.coroutines.jvm.internal.RunSuspendKt;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WelcomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WelcomeFragment extends Fragment {
    public String currentTokenFragment="notsetfragment";
    myFirebaseMessagingService myFMS;
    WebView webView;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myFMS=new myFirebaseMessagingService();
        currentTokenFragment=getToken();
    }

    SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_welcome,container,false);
        webView=view.findViewById(R.id.welcome_id);
        swipeRefreshLayout=view.findViewById(R.id.refreshLayout);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new myWebViewClient());
        currentTokenFragment=getToken();

        webView.loadUrl("https://www.muse-at.com/android/splash.php");
        Log.d("TOKEN",""+currentTokenFragment);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        currentTokenFragment=getToken();
                        webView.loadUrl("https://www.muse-at.com/android/login.php?token="+currentTokenFragment);
                        Log.d("TOKEN",""+currentTokenFragment);
                    }
                },3000);
            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    public String getToken(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.w("TOKEN", "Token fetching failed.");
                    return;
                }
                String tokenNew = task.getResult();
                SharedPreferences.Editor editor = getContext().getSharedPreferences("TOKEN_PREF", MODE_PRIVATE).edit();
                editor.putString("token",tokenNew);
                editor.apply();
                Log.d("TOKEN", tokenNew);
            }
        });


        SharedPreferences prefs=getContext().getSharedPreferences("TOKEN_PREF",MODE_PRIVATE);
        return prefs.getString("token","notfound");
    }
    public void updateURL(String token){
        webView.loadUrl("https://www.muse-at.com/android/login.php?token="+token);
    }
    public class myWebViewClient extends WebViewClient{
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            view.loadUrl("file:///android_asset/no_internet.html");
        }
    }
}