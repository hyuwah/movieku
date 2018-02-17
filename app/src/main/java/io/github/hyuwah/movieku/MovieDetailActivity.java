package io.github.hyuwah.movieku;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.squareup.picasso.Picasso;
import io.github.hyuwah.movieku.Adapter.MovieReviewAdapter;
import io.github.hyuwah.movieku.Adapter.MovieVideoAdapter;
import io.github.hyuwah.movieku.Model.MovieDetail;
import io.github.hyuwah.movieku.Model.MovieReview;
import io.github.hyuwah.movieku.Model.MovieVideo;
import io.github.hyuwah.movieku.Network.ServiceGenerator;
import io.github.hyuwah.movieku.Network.TheMovieDbService;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailActivity extends AppCompatActivity {

  // TODO Tambah Review, Link Video Trailer

  @BindView(R.id.movie_detail_image)
  ImageView ivMovieDetailImage;
  @BindView(R.id.movie_detail_title)
  TextView tvMovieDetailTitle;
  @BindView(R.id.movie_detail_overview)
  TextView tvMovieDetailOverview;
  @BindView(R.id.movie_detail_release)
  TextView tvMovieDetailRelease;
  @BindView(R.id.movie_detail_rating)
  TextView tvMovieDetailRating;
  @BindView(R.id.movie_detail_runtime)
  TextView tvMovieDetailRuntime;

  @BindView(R.id.btn_movie_detail_video)
  Button btnMovieDetailVideo;
  @BindView(R.id.btn_movie_detail_review)
  Button btnMovieDetailReview;


  MenuItem actDetailShare;

  private final String TAG = getClass().getSimpleName();

  public static final String KEY_ID = "movie_id";
  public static final String KEY_TITLE = "movie_title";
  public static final String KEY_BACKDROP_URL = "movie_backdrop_url";
  public static final String KEY_RELEASE_DATE = "movie_release";
  public static final String KEY_OVERVIEW = "movie_overview";

  Bundle bundle;

  private TheMovieDbService theMovieDbService;

  private MovieDetail currentMovie;

  List<MovieVideo.Result> videoResult;
  MovieVideoAdapter movieVideoAdapter;
  List<MovieReview.Result> reviewResult;
  MovieReviewAdapter movieReviewAdapter;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_movie_detail);

    if (savedInstanceState != null) {
      bundle = savedInstanceState;
    } else {
      bundle = getIntent().getExtras();
    }

    setupView();
    fetchMovieDatabyId(bundle.getInt(KEY_ID));

  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {

    outState.putAll(bundle);
    super.onSaveInstanceState(outState);
  }

  @Override
  public void onBackPressed() {
    finish();
  }


  /**
   * Menu related
   */

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_movie_detail, menu);

    actDetailShare = menu.findItem(R.id.action_detail_share);

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {

      case R.id.action_detail_share:

        shareMovie();

        return true;

      default:
        return super.onOptionsItemSelected(item);
    }

  }

  private void setupView() {
    ButterKnife.bind(this);

    setTitle(bundle.getString(KEY_TITLE));

    Log.i(TAG, "setupView: " + bundle.getString(KEY_TITLE));
    Log.i(TAG, "setupView: " + bundle.getString(KEY_BACKDROP_URL));

    tvMovieDetailTitle.setText(bundle.getString(KEY_TITLE));
    tvMovieDetailOverview.setText(bundle.getString(KEY_OVERVIEW));
    tvMovieDetailRelease.setText("\uD83D\uDCC5 "+bundle.getString(KEY_RELEASE_DATE));

    if (!TextUtils.isEmpty(bundle.getString(KEY_BACKDROP_URL))) {
      Picasso.with(this)
          .load("https://image.tmdb.org/t/p/w780" + bundle.getString(KEY_BACKDROP_URL))
          .placeholder(R.drawable.bg_movieku)
          .fit()
          .centerCrop()
          .into(ivMovieDetailImage);
    }

    // Experimental
    btnMovieDetailReview.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        showReviewModal();
      }
    });

    btnMovieDetailVideo.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        showVideoModal();
      }
    });


  }

  private void fetchMovieDatabyId(int movieId) {

    theMovieDbService = ServiceGenerator.createService(TheMovieDbService.class);

    // Movie Detail
    theMovieDbService.getMovieDetailById(movieId, TheMovieDbService.API_KEY).enqueue(
        new Callback<MovieDetail>() {

          @Override
          public void onResponse(Call<MovieDetail> call, Response<MovieDetail> response) {
            if (response.isSuccessful()) {
              currentMovie = response.body();

              actDetailShare.setEnabled(true);


              tvMovieDetailRelease.append(" (" + currentMovie.getStatus() + ")");

              if(currentMovie.getVoteAverage()!=0) {
                tvMovieDetailRating.setText("\uD83C\uDF1F Rating: "+currentMovie.getVoteAverage()+"/10.0");
              }

              if(currentMovie.getRuntime()!=null && currentMovie.getRuntime()!=0){
                tvMovieDetailRuntime.setText("⏳ Runtime: "+currentMovie.getRuntime()+" mins");
              }

              if (!currentMovie.getTagline().isEmpty()) {
                tvMovieDetailTitle.setText(currentMovie.getTagline());
              }
            }
          }

          @Override
          public void onFailure(Call<MovieDetail> call, Throwable t) {
            Toast.makeText(MovieDetailActivity.this, "Failed fetching movie detail",
                Toast.LENGTH_SHORT).show();
            Log.e(TAG, "onFailure: " + t.getMessage());
          }
        });

    // Movie Review
    theMovieDbService.getMovieReviewById(movieId, TheMovieDbService.API_KEY).enqueue(
        new Callback<MovieReview>() {
          @Override
          public void onResponse(Call<MovieReview> call, Response<MovieReview> response) {
            if (response.isSuccessful()) {

              if (response.body().getTotalResults() > 0) {
                // Show Review Element
                btnMovieDetailReview.setVisibility(View.VISIBLE);

                //Prepare Data Review
                reviewResult = response.body().getResults();
                movieReviewAdapter = new MovieReviewAdapter(getBaseContext(), reviewResult);

              } else {
                //Hide Review Element
                btnMovieDetailReview.setVisibility(View.GONE);
              }

            }
          }

          @Override
          public void onFailure(Call<MovieReview> call, Throwable t) {
            Toast.makeText(MovieDetailActivity.this, "Failed fetching movie review",
                Toast.LENGTH_SHORT).show();
            Log.e(TAG, "onFailure: " + t.getMessage());
          }
        });

    // Movie Video
    theMovieDbService.getMovieVideoById(movieId, TheMovieDbService.API_KEY).enqueue(
        new Callback<MovieVideo>() {
          @Override
          public void onResponse(Call<MovieVideo> call, Response<MovieVideo> response) {
            if (response.isSuccessful()) {

              Log.i(TAG, "onResponse: " + response.message());

              if (response.body().getResults() != null &&
                  !response.body().getResults().isEmpty() &&
                  response.body().getResults().size() != 0) {
                btnMovieDetailVideo.setVisibility(View.VISIBLE);

                videoResult = response.body().getResults();
                movieVideoAdapter = new MovieVideoAdapter(getBaseContext(), videoResult);

              } else {
                btnMovieDetailVideo.setVisibility(View.GONE);
              }

            }
          }

          @Override
          public void onFailure(Call<MovieVideo> call, Throwable t) {
            Toast.makeText(MovieDetailActivity.this, "Failed fetching movie video",
                Toast.LENGTH_SHORT).show();
            Log.e(TAG, "onFailure: " + t.getMessage());
          }
        });

  }

  /**
   * Modals
   */

  private void showVideoModal() {
    ListView lvVideo = new ListView(MovieDetailActivity.this);
    lvVideo.setAdapter(movieVideoAdapter);

    lvVideo.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Uri youtube = Uri.parse("https://youtube.com/watch?v=" + videoResult.get(i).getKey());

        Intent intent = new Intent(Intent.ACTION_VIEW, youtube);
        startActivity(intent);
      }
    });

    AlertDialog.Builder builder = new Builder(MovieDetailActivity.this);
    builder.setTitle("Video");
    builder.setView(lvVideo);
    builder.setPositiveButton("Close", null);

    AlertDialog reviewDialog = builder.create();
    reviewDialog.show();
  }

  private void showReviewModal() {

    ListView lvReview = new ListView(MovieDetailActivity.this);
    lvReview.setAdapter(movieReviewAdapter);

    AlertDialog.Builder builder = new Builder(MovieDetailActivity.this);
    builder.setTitle("Review");
    builder.setView(lvReview);
    builder.setPositiveButton("Close", null);

    AlertDialog reviewDialog = builder.create();
    reviewDialog.show();


  }

  private void shareMovie() {
    try {

      // Assemble share message
      String message = "Check out this movie!\nTitle: " + currentMovie.getTitle() +
          "\nReleased: " + currentMovie.getReleaseDate();

      if (currentMovie.getImdbId() != null) {
        message += "\nhttp://www.imdb.com/title/" + currentMovie.getImdbId() + "/\n";
      } else {
        message += "\nThis movie's not listed on IMDb :(\n";
      }

      message += "\nSent from MovieKu";

      // Create share intent
      Intent sendIntent = new Intent();
      sendIntent.setAction(Intent.ACTION_SEND);
      sendIntent.putExtra(Intent.EXTRA_TEXT, message);
      sendIntent.setType("text/plain");
      startActivity(Intent.createChooser(sendIntent, "Share"));

    } catch (Exception e) {

      e.printStackTrace();
      Toast.makeText(this, "Refreshing data. Please try again.", Toast.LENGTH_SHORT).show();
      if (bundle != null) {
        fetchMovieDatabyId((bundle.getInt(KEY_ID)));
      }

    }
  }

}
