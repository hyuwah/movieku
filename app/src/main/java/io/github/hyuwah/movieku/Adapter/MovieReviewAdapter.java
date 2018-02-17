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
import io.github.hyuwah.movieku.Model.MovieReview;
import io.github.hyuwah.movieku.Model.MovieReview.Result;
import io.github.hyuwah.movieku.R;
import java.util.List;

/**
 * Created by hyuwah on 16/02/18.
 */

public class MovieReviewAdapter extends ArrayAdapter<MovieReview.Result> {

  @BindView(R.id.tv_item_movie_review_author)
  TextView tvMovieReviewAuthor;
  @BindView(R.id.tv_item_movie_review_content)
  TextView tvMovieReviewContent;

  public MovieReviewAdapter (Context context, List<Result> movieReviews){
    super(context,0,movieReviews);
  }

  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

    MovieReview.Result currentMovieReview = getItem(position);

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


    tvMovieReviewContent.setText(currentMovieReview.getContent());
    tvMovieReviewAuthor.setText("\uD83D\uDDE3️ "+currentMovieReview.getAuthor());


    return convertView;
  }

  private static class ViewHolder {
    TextView movieReviewContent, movieReviewAuthor;
  }

}
