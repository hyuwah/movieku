package io.github.hyuwah.movieku.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.squareup.picasso.Picasso;
import io.github.hyuwah.movieku.Model.MovieList;
import io.github.hyuwah.movieku.R;
import java.util.List;

/**
 * Created by hyuwah on 14/02/18.
 */

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ViewHolder> {

  private final String TAG = getClass().getSimpleName();

  private List<MovieList.Results> mMovieList;
  private Context mContext;

  // OnItemClickListener
  private OnItemClickListener clickListener;

  public interface OnItemClickListener {

    void onItemClick(View itemView, int position);
  }

  public void setOnItemClickListener(OnItemClickListener clickListener) {
    this.clickListener = clickListener;
  }

  public MovieListAdapter(Context mContext, List<MovieList.Results> mMovieList) {
    this.mMovieList = mMovieList;
    this.mContext = mContext;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_movie_list, parent, false);
    return new ViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {

    MovieList.Results currentMovie = mMovieList.get(position);

    Log.i(TAG, "onBindViewHolder: "+currentMovie.getTitle());

    String voteAvg = String.valueOf(currentMovie.getVoteAverage());

    holder.tvMovieListTitle.setText(currentMovie.getTitle());
    holder.tvMovieListReleaseDate.setText(currentMovie.getReleaseDate());

    if (!TextUtils.isEmpty(currentMovie.getPosterPath())) {
      Picasso.with(mContext)
          .load("https://image.tmdb.org/t/p/w185"+currentMovie.getPosterPath())
          .placeholder(R.drawable.bg_movieku)
          .into(holder.ivMovieListImage);
    }


  }

  @Override
  public int getItemCount() {
    return (mMovieList != null ? mMovieList.size() : 0);
  }

  // ViewHolder Inner Class

  public class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.movie_list_image)
    ImageView ivMovieListImage;

    @BindView(R.id.movie_list_title)
    TextView tvMovieListTitle;

    @BindView(R.id.movie_list_release_date)
    TextView tvMovieListReleaseDate;

    public ViewHolder(final View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);

      itemView.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View view) {
          if (clickListener != null) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
              clickListener.onItemClick(itemView, position);
            }
          }
        }
      });
    }

  }

}
