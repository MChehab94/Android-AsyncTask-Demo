package mchehab.com.asynctaskdemo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements AsyncListener, AsyncImageListener{

    private TextView textView;
    private ImageView imageView;
    private ProgressBar progressBar;
    private final String URL = "http://validate.jsontest.com/?json=%7B%22key%22:%22value%22%7D";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        imageView = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBar);

        Button button = findViewById(R.id.button);
        Button buttonImage = findViewById(R.id.buttonImage);

        button.setOnClickListener(e -> {
            new GetJSON(this).execute(URL);
        });
        buttonImage.setOnClickListener(e -> {
            progressBar.setVisibility(View.VISIBLE);
            new AsyncImageDownloader(this).execute("https://www.w3schools.com/bootstrap/paris.jpg");
        });
    }

    @Override
    public void getResult(String result) {
        textView.setText(result);
    }

    @Override
    public void getBitmap(Bitmap bitmap) {
        progressBar.setVisibility(View.GONE);
        imageView.setImageBitmap(bitmap);
    }
}