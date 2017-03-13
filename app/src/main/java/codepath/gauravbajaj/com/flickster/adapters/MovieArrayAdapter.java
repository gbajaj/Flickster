package codepath.gauravbajaj.com.flickster.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import codepath.gauravbajaj.com.flickster.Flickster;
import codepath.gauravbajaj.com.flickster.MovieDetailsActivity;
import codepath.gauravbajaj.com.flickster.R;
import codepath.gauravbajaj.com.flickster.models.Movie;
import codepath.gauravbajaj.com.flickster.utils.DeviceDimensionsHelper;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

/**
 * Created by gauravb on 3/6/17.
 */

public class MovieArrayAdapter extends ArrayAdapter<Movie> {
    private static final Flickster flickster = Flickster.instance();
    private float dpWithPortrait = 0;
    private float dpWithBackdrop780 = 0;

    enum ViewType {
        POSTER_PORTRAIT, FULL_BACKDROP_PORTRAIT, BACK_DROP_LANDSCAPE;
    }

    public interface PlayYouTube {
        void playYouTube(String id);
    }

    private PlayYouTube playYouTubeActivity;

    public static class ViewHolder {
        @BindView(R.id.ivMovieImage)
        ImageView lvImageView;
        @BindView(R.id.ivMovieImagePlayIcon)
        ImageView lvImageViewPlayIcon;
        @BindView(R.id.tvTitle)
        TextView title;
        @BindView(R.id.tvOverview)
        TextView overView;
        int position;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public MovieArrayAdapter(Context context, List<Movie> movies) {
        super(context, android.R.layout.simple_list_item_1, movies);
        playYouTubeActivity = (PlayYouTube) context;
        dpWithPortrait = DeviceDimensionsHelper.convertDpToPixel(171, getContext());
        dpWithBackdrop780 = DeviceDimensionsHelper.convertDpToPixel(780, getContext());
    }

    // Returns the number of types of Views that will be created by getView(int, View, ViewGroup)
    @Override
    public int getViewTypeCount() {
        // Returns the number of types of Views that will be created by this adapter
        // Each type represents a set of views that can be converted
        return ViewType.values().length;
    }

    // Get the type of View that will be created by getView(int, View, ViewGroup)
    // for the specified item.
    @Override
    public int getItemViewType(int position) {
        // Return an integer here representing the type of View.
        // Note: Integers must be in the range 0 to getViewTypeCount() - 1
        int orientation = getContext().getResources().getConfiguration().orientation;
        Movie movie = getItem(position);
        Double voteAverage = movie.getAverageRatings();
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return ViewType.BACK_DROP_LANDSCAPE.ordinal();
        }
        if (voteAverage != null && voteAverage > 5.0) {
            return ViewType.FULL_BACKDROP_PORTRAIT.ordinal();
        }

        return ViewType.POSTER_PORTRAIT.ordinal();

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //get the data item for position
        final Movie movie = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row

            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.item_movie, parent, false);
            viewHolder = new ViewHolder(convertView);

            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ViewHolder v = (ViewHolder) view.getTag();
                    Movie m = getItem(v.position);
                    Intent i = new Intent(getContext(), MovieDetailsActivity.class);
                    i.putExtra(MovieDetailsActivity.MOVIE, m);
                    getContext().startActivity(i);
                }
            });
            viewHolder.lvImageViewPlayIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playYouTubeActivity.playYouTube("WFbKXY8_Y74");
                }
            });
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //clear out the image from convert view
        viewHolder.lvImageView.setImageResource(0);

        viewHolder.position = position;

        //Populate the data
        viewHolder.title.setText(movie.getOriginalTitle());
        viewHolder.overView.setText(movie.getOverview());
        String imagePath = movie.getPosterPath();
        int type = getItemViewType(position);
        int placeHolder = R.drawable.place_holder_backdrop;

        viewHolder.title.setVisibility(View.VISIBLE);
        viewHolder.overView.setVisibility(View.VISIBLE);
        if (type == ViewType.BACK_DROP_LANDSCAPE.ordinal()) {
            imagePath = movie.getBackdrop780Path();
        } else {
            if (type == ViewType.FULL_BACKDROP_PORTRAIT.ordinal()) {
                viewHolder.title.setVisibility(View.GONE);
                viewHolder.overView.setVisibility(View.GONE);
                imagePath = movie.getBackdrop780Path();
                viewHolder.lvImageView.getLayoutParams().width = (int) dpWithBackdrop780;
            } else {
                viewHolder.lvImageView.getLayoutParams().width = (int) dpWithPortrait;
                placeHolder = R.drawable.place_holder_portrait;
            }
        }
        viewHolder.lvImageViewPlayIcon.setVisibility(View.GONE);

        flickster.picasso.load(imagePath).placeholder(placeHolder)
                .transform(new RoundedCornersTransformation(5, 5)).into(viewHolder.lvImageView, new Callback() {
            @Override
            public void onSuccess() {
                Double voteAverage = movie.getAverageRatings();
                if (voteAverage != null && voteAverage > 5.0f) {
                    viewHolder.lvImageViewPlayIcon.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError() {

            }
        });
        return convertView;
    }

}
