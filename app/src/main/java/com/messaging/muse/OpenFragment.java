package com.messaging.muse;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OpenFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OpenFragment extends Fragment {

    SwipeRefreshLayout swipeRefreshLayoutOpen;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_open,container,false);
        WebView webView=view.findViewById(R.id.open_webview_id);
        swipeRefreshLayoutOpen=view.findViewById(R.id.refreshLayoutOpen);
        webView.loadUrl("https://www.muse-at.com/android/open.php");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new myWebViewClient());
        // Inflate the layout for this fragment
        swipeRefreshLayoutOpen.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayoutOpen.setRefreshing(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayoutOpen.setRefreshing(false);
                        webView.loadUrl("https://www.muse-at.com/android/open.php");
                    }
                },3000);
            }
        });
        return view;
    }
    public class myWebViewClient extends WebViewClient{
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            view.loadUrl("file:///android_asset/no_internet.html");
        }
    }
}