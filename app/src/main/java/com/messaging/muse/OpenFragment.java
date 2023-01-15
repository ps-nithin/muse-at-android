package com.messaging.muse;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_open,container,false);
        WebView webView=view.findViewById(R.id.open_webview_id);
        webView.loadUrl("https://www.muse-at.com/android/open.php");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        // Inflate the layout for this fragment
        return view;
    }
}