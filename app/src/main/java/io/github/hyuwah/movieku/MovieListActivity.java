package io.github.hyuwah.movieku;

import static android.view.View.GONE;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.hyuwah.movieku.Adapter.MovieListAdapter;
import io.github.hyuwah.movieku.Adapter.MovieListAdapter.OnItemClickListener;
import io.github.hyuwah.movieku.Model.MovieList;
import io.github.hyuwah.movieku.Network.ServiceGenerator;
import io.github.hyuwah.movieku.Network.TheMovieDbService;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieListActivity extends AppCompatActivity implements
    NavigationView.OnNavigationItemSelectedListener {

  private final String TAG = getClass().getSimpleName();

  @BindView(R.id.rv_movie_list)
  RecyclerView rvMovieList;
  @BindView(R.id.pb_movie_list)
  ProgressBar pbMovieList;
  @BindView(R.id.tv_empty_view)
  TextView tvEmptyView;

  // Double tap back to exit
  private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
  private long mBackPressed;

  private List<MovieList.Results> mMovieList = new ArrayList<>();
  private MovieListAdapter movieListAdapter;

  private TheMovieDbService theMovieDbService;

  // Category path
  private final String CATEGORY_NOW_PLAYING = "now_playing";
  private final String CATEGORY_POPULAR = "popular";
  private final String CATEGORY_TOP_RATED = "top_rated";
  private final String CATEGORY_LATEST = "latest";
  private final String CATEGORY_UPCOMING = "upcoming";


  private int mGridColCount = 2;
  private String mCategory = CATEGORY_NOW_PLAYING;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
//    setContentView(R.layout.activity_movie_list);
    setContentView(R.layout.activity_main_drawer);

    setupView();
    fetchMovies();

  }

  @Override
  public void onBackPressed() {

    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {

      if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
        super.onBackPressed();
        return;
      } else {
        Toast.makeText(getBaseContext(), "Tap back again to exit", Toast.LENGTH_SHORT).show();
      }

      mBackPressed = System.currentTimeMillis();

    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_movie_list, menu);

    // Associate searchable configuration with the SearchView
    SearchManager searchManager =
        (SearchManager) getSystemService(Context.SEARCH_SERVICE);
    SearchView searchView =
        (SearchView) menu.findItem(R.id.search).getActionView();
    searchView.setSearchableInfo(
        searchManager.getSearchableInfo(getComponentName()));

    searchView.setSubmitButtonEnabled(true);
    searchView.setQueryHint("Search movies");
    searchView.setOnQueryTextListener(new OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        searchMovies(query);
        return true;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        return false;
      }
    });

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    switch (item.getItemId()) {

      case R.id.action_category_now_playing:
      case R.id.action_category_popular:
      case R.id.action_category_top_rated:
      case R.id.action_category_latest:
      case R.id.action_category_upcoming:
        fetchMoviesByCategory(item.getItemId());
        return true;

      default:
        return super.onOptionsItemSelected(item);

    }

  }

  @SuppressWarnings("StatementWithEmptyBody")
  @Override
  public boolean onNavigationItemSelected(MenuItem item) {
    // Handle navigation view item clicks here.
    int id = item.getItemId();

    if (id == R.id.nav_home) {

      if (getClass().getSimpleName().equals("MovieListActivity")) {
        Snackbar
            .make(findViewById(R.id.main_coordinator), "Arrived at home~", Snackbar.LENGTH_SHORT)
            .show();
      }

    } else if (id == R.id.nav_preference) {

      Snackbar snackbar = Snackbar
          .make(findViewById(R.id.main_coordinator), "Feature coming soon!", Snackbar.LENGTH_SHORT);
      snackbar.show();

    } else if (id == R.id.nav_about) {

      Intent aboutIntent = new Intent(this, AboutActivity.class);
      startActivity(aboutIntent);

    }

    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }

  private void setupView() {
    ButterKnife.bind(this);

    setupDrawerView();

    setTitle("MovieKu: Now Playing");

    movieListAdapter = new MovieListAdapter(this, mMovieList);
    movieListAdapter.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(View itemView, int position) {
        MovieList.Results clickedMovie = mMovieList.get(position);

        String urlImage = clickedMovie.getBackdropPath();
        if (TextUtils.isEmpty(urlImage)) {
          urlImage = clickedMovie.getPosterPath();
        }

        Intent intent = new Intent(MovieListActivity.this, MovieDetailActivity.class);
        intent.putExtra(MovieDetailActivity.KEY_TITLE, clickedMovie.getTitle());
        intent.putExtra(MovieDetailActivity.KEY_OVERVIEW, clickedMovie.getOverview());
        intent.putExtra(MovieDetailActivity.KEY_RELEASE_DATE, clickedMovie.getReleaseDate());
        intent.putExtra(MovieDetailActivity.KEY_BACKDROP_URL, urlImage);
        intent.putExtra(MovieDetailActivity.KEY_ID, clickedMovie.getId());

        startActivity(intent);

      }
    });

    RecyclerView.LayoutManager rvLayout = new GridLayoutManager(this, mGridColCount);
    rvMovieList.setLayoutManager(rvLayout);
    rvMovieList.setItemAnimator(new DefaultItemAnimator());
    rvMovieList.setAdapter(movieListAdapter);

  }

  // Setup Drawer
  private void setupDrawerView() {
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    toggle.syncState();

    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);
  }

  private void fetchMoviesByCategory(int categoryId) {

    switch (categoryId) {

      case R.id.action_category_now_playing:
        setTitle("MovieKu: Now Playing");
        mCategory = CATEGORY_NOW_PLAYING;
        break;
      case R.id.action_category_popular:
        setTitle("MovieKu: Popular");
        mCategory = CATEGORY_POPULAR;
        break;
      case R.id.action_category_top_rated:
        setTitle("MovieKu: Top Rated");
        mCategory = CATEGORY_TOP_RATED;
        break;
      case R.id.action_category_latest:
        setTitle("MovieKu: Latest");
        mCategory = CATEGORY_LATEST;
        break;
      case R.id.action_category_upcoming:
        setTitle("MovieKu: Upcoming");
        mCategory = CATEGORY_UPCOMING;
        mMovieList.clear();
        break;

    }

    mMovieList.clear();
    movieListAdapter.notifyDataSetChanged();
    fetchMovies();

  }

  private void fetchMovies() {

    // Loading
    tvEmptyView.setVisibility(GONE);
    pbMovieList.setVisibility(View.VISIBLE);

    theMovieDbService = ServiceGenerator.createService(TheMovieDbService.class);

    Call<MovieList> call = theMovieDbService
        .getMoviesByCategory(mCategory, 1, "ID", TheMovieDbService.API_KEY);

    call.enqueue(
        new Callback<MovieList>() {

          @Override
          public void onResponse(Call<MovieList> call, Response<MovieList> response) {
            tvEmptyView.setText(R.string.list_category_empty);
            if (response.isSuccessful()) {
              if (response.body().results != null && !response.body().results.isEmpty()) {
                mMovieList.addAll(response.body().results);
                // Show RecyclerView
                // Hide EmptyView
                tvEmptyView.setVisibility(GONE);
              } else {
                // Show empty view
                tvEmptyView.setVisibility(View.VISIBLE);
              }
            }
            movieListAdapter.notifyDataSetChanged();
            pbMovieList.setVisibility(GONE);
          }

          @Override
          public void onFailure(Call<MovieList> call, Throwable t) {

            Toast.makeText(MovieListActivity.this, "Failed fetching movie list", Toast.LENGTH_SHORT)
                .show();
            Log.e(TAG, "onFailure: " + t.getMessage());

            tvEmptyView.setVisibility(View.VISIBLE);
            tvEmptyView.setText(R.string.list_network_error);
            pbMovieList.setVisibility(GONE);
          }
        });
  }

  private void searchMovies(String query) {

    setTitle("Results for \"" + query + "\"");

    mMovieList.clear();
    movieListAdapter.notifyDataSetChanged();

    // Loading
    tvEmptyView.setVisibility(GONE);
    pbMovieList.setVisibility(View.VISIBLE);

    theMovieDbService = ServiceGenerator.createService(TheMovieDbService.class);

    Call<MovieList> call = theMovieDbService
        .searchMovies(query, "ID", TheMovieDbService.API_KEY);

    call.enqueue(new Callback<MovieList>() {
      @Override
      public void onResponse(Call<MovieList> call, Response<MovieList> response) {
        tvEmptyView.setText(R.string.list_search_not_found);
        if (response.isSuccessful()) {
          if (response.body().results != null && !response.body().results.isEmpty()) {
            mMovieList.addAll(response.body().results);
            // Show RecyclerView
            // Hide EmptyView
            tvEmptyView.setVisibility(GONE);
          } else {
            // Show empty view
            tvEmptyView.setVisibility(View.VISIBLE);
          }
        }
        movieListAdapter.notifyDataSetChanged();
        pbMovieList.setVisibility(GONE);
      }

      @Override
      public void onFailure(Call<MovieList> call, Throwable t) {
        Toast.makeText(MovieListActivity.this, "Failed fetching movie list", Toast.LENGTH_SHORT)
            .show();
        Log.e(TAG, "onFailure: " + t.getMessage());

        tvEmptyView.setVisibility(View.VISIBLE);
        tvEmptyView.setText(R.string.list_network_error);
        pbMovieList.setVisibility(GONE);
      }
    });

  }

}
