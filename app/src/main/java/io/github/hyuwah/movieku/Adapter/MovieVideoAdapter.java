package io.github.hyuwah.movieku.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.hyuwah.movieku.Model.MovieVideo;
import io.github.hyuwah.movieku.Model.MovieVideo.Result;
import io.github.hyuwah.movieku.R;
import java.util.List;

/**
 * Created by hyuwah on 16/02/18.
 */

public class MovieVideoAdapter extends ArrayAdapter<MovieVideo.Result> {

  @BindView(R.id.tv_item_movie_review_author)
  TextView tvMovieReviewAuthor;
  @BindView(R.id.tv_item_movie_review_content)
  TextView tvMovieReviewContent;

  public MovieVideoAdapter(Context context, List<Result> movieVideos) {
    super(context, 0, movieVideos);
  }

  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

    MovieVideo.Result currentMovieVideo = getItem(position);

    ViewHolder viewHolder;
    // If null, inflate
    if(convertView==null){
      viewHolder = new ViewHolder();
      convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_movie_detail_review,null);
      viewHolder.movieReviewAuthor = tvMovieReviewAuthor;
      viewHolder.movieReviewContent = tvMovieReviewContent;
      // cache viewholder
      convertView.setTag(viewHolder);
    }else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    ButterKnife.bind(this,convertView);


    tvMovieReviewContent.setText("\uD83C\uDFA5 "+currentMovieVideo.getType());
    tvMovieReviewAuthor.setText(currentMovieVideo.getName());


    return convertView;
  }

  private static class ViewHolder {
    TextView movieReviewContent, movieReviewAuthor;
  }
}
