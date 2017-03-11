package codepath.gauravbajaj.com.flickster;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import codepath.gauravbajaj.com.flickster.models.Movie;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import okhttp3.OkHttpClient;

/**
 * Created by gauravb on 3/8/17.
 */

public class MovieDetailsActivity extends AppCompatActivity {
    public static final String MOVIE = MovieDetailsActivity.class.getName() + "." + "MOVIE";
    Movie movie;
    @BindView(R.id.ivMovieImage)
    ImageView lvMovie;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.votes)
    TextView votes;
    @BindView(R.id.ratingBar)
    RatingBar ratingBar;
    @BindView(R.id.avgRating)
    TextView avgRating;
    @BindView(R.id.releaseDate)
    TextView releaseDate;
    @BindView(R.id.synopsisText)
    TextView synopsisText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.move_details);
        ButterKnife.bind(this);
        Intent i = getIntent();
        movie = i.getParcelableExtra(MOVIE);
        if (movie == null) throw new IllegalArgumentException("Movie Object should not be null");
        title.setText(movie.getOriginalTitle());

        votes.setText("" + movie.getRatingsCount());

        ratingBar.setRating(0);
        if (movie.getAverageRatings() != null) {
            String ratingStr = "" + movie.getAverageRatings();
            ratingBar.setRating((float)(Float.parseFloat(ratingStr)/2.0));
        }

        avgRating.setText("" + movie.getAverageRatings()/2);
        releaseDate.setText(movie.getReleaseDate());
        //Set Movie overview
        synopsisText.setText(movie.getOverview());
        String imagePath = movie.getBackdropPath();
        int placeHolder = R.drawable.place_holder_backdrop;
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            imagePath = movie.getPosterPath();
            placeHolder = R.drawable.place_holder_portrait;
        }

        OkHttpClient client = new OkHttpClient();
        Picasso picasso = new Picasso.Builder(getApplicationContext()).downloader(new OkHttp3Downloader(client)).build();
        picasso.load(imagePath).placeholder(placeHolder).transform(new RoundedCornersTransformation(10, 10)).into(lvMovie);
    }
}
