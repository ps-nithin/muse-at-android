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
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class OpenFragment extends Fragment {

    String muse_id="fragment_default";
    SwipeRefreshLayout swipeRefreshLayoutOpen;
    private WebView webView=null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_open,container,false);
        webView=view.findViewById(R.id.open_webview_id);
        swipeRefreshLayoutOpen=view.findViewById(R.id.refreshLayoutOpen);
        MainActivity activity= (MainActivity) getActivity();
        muse_id=activity.getMuseID();
        webView.loadUrl("https://www.muse-at.com/android/open.php?id="+muse_id);
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
                        webView.loadUrl("https://www.muse-at.com/android/open.php?id="+muse_id);
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