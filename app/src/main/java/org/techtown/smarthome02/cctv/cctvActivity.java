package org.techtown.smarthome02.cctv;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import org.techtown.smarthome02.R;

public class cctvActivity extends AppCompatActivity {


    WebView webView;


    TextView tv_titlebar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cctv);
        tv_titlebar = findViewById(R.id.tv_titlebar);
        tv_titlebar.setText( "Smart Home CCTV" );
        webView = findViewById(R.id.webView);
        // WebSettings 객체 참조 -> javaScript 동작 환경 설정
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);


        //화면에 추가된 WebView 객체에 웹페이지를 보여주기 위해서는
        // WebViewClient를 상속해 만들어 WebView에 설정해야 한다.
        webView.setWebViewClient(new ViewClient());



        //loadUrl: 웹페이지를  로딩하여 화면에 보여준다.
        // 원격지의 웹페이지를 열거나 로컬에 저장된 Html 파일을 열 수 있음.
        // 이렇게 나타난 웹페이지는 확대/축소 기능이 설정되어 있으면 화면 상에서
        // 확대/축소가 가능하며 웹뷰 객체의 goForward(), goBack() 메서드를
        // 이용하면 앞 페이지 또는 뒤 페이지로도 이동할 수 있다.
        webView.loadUrl("http://192.168.148.112:8080/?action=stream");

    }
    //  WebViewClient를 상속한 ViewClient class
    private class ViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // 웹페이지 로딩
            view.loadUrl(url);

            return true;
        }
    }
}