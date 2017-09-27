package mobile.app.svgloadapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    JavaScriptInterface JSInterface;
    ProgressDialog pd;
    PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WebView myWebView = (WebView) findViewById(R.id.web_view);
        preferenceManager = new PreferenceManager(this);
        //WebView blocking pop up windows?
        myWebView.getSettings().setAllowFileAccess(true);

        myWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        getWindow().setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);
        JSInterface = new JavaScriptInterface(this);
        myWebView.addJavascriptInterface(JSInterface, "JSInterface");

        pd = new ProgressDialog(MainActivity.this);
        pd.setMessage("loading");
        pd.show();
        myWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {

            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                //Required functionality here
                return super.onJsAlert(view, url, message, result);
            }

        });
        //Denied starting an intent without a user gesture, URI file:///android_res/raw/first.svg
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            public void onPageFinished(WebView view, String url) {
                // do your stuff here
            }
        });
        myWebView.getSettings().setJavaScriptEnabled(true);

        myWebView.getSettings().setBuiltInZoomControls(true);
        myWebView.getSettings().setDisplayZoomControls(false);
        myWebView.loadUrl("file:///android_res/raw/index.html");

    }

    //customize your web view client to open links from your own site in the
    //same web view otherwise just open the default browser activity with the URL
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Uri.parse(url).getHost().equals("demo.mysamplecode.com")) {
                return false;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }
    }

    private class MyWebChromeClient extends WebChromeClient {

        //display alert message in Web View
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            Log.d("", message);
            new AlertDialog.Builder(view.getContext())
                    .setMessage(message).setCancelable(true).show();
            result.confirm();
            return true;
        }

    }

    public class JavaScriptInterface {
        Context mContext;

        /**
         * Instantiate the interface and set the context
         */
        JavaScriptInterface(Context c) {
            mContext = c;
        }
        @android.webkit.JavascriptInterface
        public void dismissDilog() {
            pd.dismiss();
        }
        @android.webkit.JavascriptInterface
        public void showToastId(String id) {
            Toast.makeText(mContext, id, Toast.LENGTH_SHORT).show();
            preferenceManager.putString("id",id);

        }
    }

}
