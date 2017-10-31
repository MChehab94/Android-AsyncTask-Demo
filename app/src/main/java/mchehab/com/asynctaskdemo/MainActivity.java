package mchehab.com.asynctaskdemo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements AsyncListener{

    private TextView textView;
    private final String URL = "http://validate.jsontest.com/?json=%7B%22key%22:%22value%22%7D";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        Button button = findViewById(R.id.button);

        button.setOnClickListener(e -> {
            new GetJSON(this).execute(URL);
        });
    }

    @Override
    public void getResult(String result) {
        textView.setText(result);
    }
}