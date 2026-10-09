package com.yallajamal.app;

import android.os.Bundle;
import android.webkit.WebView;
import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {

    /**
     * Hardware/gesture back: navigate inside the app first (window.YallaJamal.back()
     * returns "false" when the home screen is showing), otherwise leave the app.
     */
    @Override
    public void onBackPressed() {
        WebView webView = getBridge() != null ? getBridge().getWebView() : null;
        if (webView == null) {
            super.onBackPressed();
            return;
        }
        webView.evaluateJavascript(
            "(window.YallaJamal && window.YallaJamal.back()) ? 'true' : 'false'",
            result -> {
                if (!"true".equals(result)) {
                    runOnUiThread(super::onBackPressed);
                }
            }
        );
    }
}
