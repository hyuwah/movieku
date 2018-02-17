package io.github.hyuwah.movieku.Network;

import io.github.hyuwah.movieku.Model.MovieList;
import io.github.hyuwah.movieku.Model.MovieDetail;
import io.github.hyuwah.movieku.Model.MovieReview;
import io.github.hyuwah.movieku.Model.MovieVideo;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by hyuwah on 14/02/18.
 */

public interface TheMovieDbService {

  String API_KEY = "***REMOVED***";

  @GET("movie/{category}")
  Call<MovieList> getMoviesByCategory(
      @Path("category") String category,
      @Query("page") int page,
      @Query("region") String region,
      @Query("api_key") String apiKey
  );

  @GET("movie/{movie_id}")
  Call<MovieDetail> getMovieDetailById(
      @Path("movie_id") int movieId,
      @Query("api_key") String apiKey

  );

  @GET("movie/{movie_id}/reviews")
  Call<MovieReview> getMovieReviewById(
      @Path("movie_id") int movieId,
      @Query("api_key") String apiKey
  );

  @GET("movie/{movie_id}/videos")
  Call<MovieVideo> getMovieVideoById(
      @Path("movie_id") int movieId,
      @Query("api_key") String apiKey
  );

  //Default region = ID
  @GET("search/movie?region=ID")
  Call<MovieList> searchMovies(
      @Query("query") String query,
      @Query("api_key") String apiKey
  );

  @GET("search/movie")
  Call<MovieList> searchMovies(
      @Query("query") String query,
      @Query("region") String region,
      @Query("api_key") String apiKey
  );

}
