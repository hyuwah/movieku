package io.github.hyuwah.movieku;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutActivity extends AppCompatActivity {


  @BindView(R.id.btn_about_source)
  Button btnAboutSource;
  @BindView(R.id.btn_about_homepage)
  Button btnAboutHomepage;
  @BindView(R.id.btn_about_iak)
  Button btnAboutIak;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_about);
    setTitle("About");

    ButterKnife.bind(this);

    btnAboutHomepage.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://hyuwah.github.io")));
      }
    });

    btnAboutSource.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/hyuwah/movieku")));
      }
    });

    btnAboutIak.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://events.withgoogle.com/indonesiaandroidkejar/")));
      }
    });

  }
}
