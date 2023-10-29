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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import kotlin.coroutines.jvm.internal.RunSuspendKt;
public class WelcomeFragment extends Fragment {
    private CookieManager cookieManager=null;
    private boolean is_logged_in=false;
    @Override
    public void onStop() {
        if(is_logged_in){
            //Toast.makeText(getContext(),"onStop",Toast.LENGTH_SHORT).show();
            webView.loadUrl("https://www.muse-at.com/android/login.php?exit=1");
        }
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onStart() {
        String cookie=getCookie("https://www.muse-at.com/android/","loggedin");
        if(cookie.equals("1")) {
            //Toast.makeText(getContext(),"Logged in.", Toast.LENGTH_SHORT).show();
            is_logged_in=true;
        }else if (cookie.equals("0")){
            //Toast.makeText(getContext(),"Not logged in.",Toast.LENGTH_SHORT).show();
            is_logged_in=false;
        }
        if(is_logged_in){
            //Toast.makeText(getContext(),"onStart",Toast.LENGTH_SHORT).show();
            webView.loadUrl("https://www.muse-at.com/android/login.php?exit=1");
        }
        EventBus.getDefault().register(this);
        super.onStart();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        webView.loadUrl("https://www.muse-at.com/android/login.php");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTabChangedEvent(TabChangedEvent event) {
        webView.loadUrl("https://www.muse-at.com/android/login.php?exit=1");
    }

    public String getCookie(String siteName,String cookieName) {
        String CookieValue = "";

        cookieManager = CookieManager.getInstance();
        String cookies = cookieManager.getCookie(siteName);
        if (cookies==null){
            return CookieValue;
        }
        String[] temp=cookies.split(";");
        for (String ar1 : temp ){
            if(ar1.contains(cookieName)){
                String[] temp1=ar1.split("=");
                CookieValue = temp1[1];
                break;
            }
        }
        return CookieValue;
    }
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
        //webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);



        cookieManager.setAcceptThirdPartyCookies(webView, true);

        webView.setWebViewClient(new myWebViewClient());
        currentTokenFragment=getToken();
        webView.setVerticalScrollBarEnabled(false);
        webView.loadUrl("https://www.muse-at.com/android/splash.php");
        webView.addJavascriptInterface(new WebAppInterface(getContext()), "Android");
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

        webView.setOnKeyListener(new View.OnKeyListener(){

            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    webView.loadUrl("https://www.muse-at.com/android/welcome.php");
                    return true;
                }
                return true;
            }

        });
        String cookie=getCookie("https://www.muse-at.com/android/","loggedin");
        if(cookie.equals("1")) {
            //Toast.makeText(getContext(),"Logged in.", Toast.LENGTH_SHORT).show();
            is_logged_in=true;
        }else if (cookie.equals("0")){
            //Toast.makeText(getContext(),"Not logged in.",Toast.LENGTH_SHORT).show();
            is_logged_in=false;
        }
        if(is_logged_in){
            //Toast.makeText(getContext(),"Redirecting...",Toast.LENGTH_SHORT).show();
            webView.loadUrl("https://www.muse-at.com/android/login.php?exit=1");
        }
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
                webView.loadUrl("https://www.muse-at.com/android/login.php?token="+tokenNew);
                Log.d("TOKEN", tokenNew);
            }
        });


        SharedPreferences prefs=getContext().getSharedPreferences("TOKEN_PREF",MODE_PRIVATE);
        return prefs.getString("token","notfound");
    }

    public class myWebViewClient extends WebViewClient{
        @Override
        public void onPageFinished(WebView view, String url) {
            CookieManager.getInstance().setAcceptCookie(true);
            CookieManager.getInstance().acceptCookie();
            CookieManager.getInstance().flush();
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            view.loadUrl("file:///android_asset/no_internet.html");
        }
    }
}