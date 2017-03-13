package codepath.gauravbajaj.com.flickster;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import codepath.gauravbajaj.com.flickster.adapters.MovieArrayAdapter;
import codepath.gauravbajaj.com.flickster.models.Movie;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import okhttp3.OkHttpClient;

/**
 * Created by gauravb on 3/8/17.
 */

public class MovieDetailsActivity extends AppCompatActivity implements YouTubePlayer.OnInitializedListener,
        YouTubePlayer.PlayerStateChangeListener {
    public static final String MOVIE = MovieDetailsActivity.class.getName() + "." + "MOVIE";
    private static final int RECOVERY_REQUEST = 1;
    @BindView(R.id.ivMovieImageLayout)
    ViewGroup ivMovieImageLayout;

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
    YouTubePlayer youTubePlayer;
    String videoId;

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
            ratingBar.setRating((float) (Float.parseFloat(ratingStr) / 2.0));
        }

        avgRating.setText("" + movie.getAverageRatings() / 2);
        releaseDate.setText(movie.getReleaseDate());
        //Set Movie overvie
        synopsisText.setText(movie.getOverview());
        String imagePath = movie.getBackdrop780Path();
        int placeHolder = R.drawable.place_holder_backdrop;
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            imagePath = movie.getPosterPath();
            placeHolder = R.drawable.place_holder_portrait;
        }

        OkHttpClient client = new OkHttpClient();
        Picasso picasso = new Picasso.Builder(getApplicationContext()).downloader(new OkHttp3Downloader(client)).build();
        picasso.load(imagePath).placeholder(placeHolder).transform(new RoundedCornersTransformation(10, 10)).into(lvMovie);
        ivMovieImageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playYouTube("WFbKXY8_Y74");
            }
        });
    }

    public void playYouTube(String id) {
        YouTubePlayerSupportFragment youTubePlayerSupportFragment = new YouTubePlayerSupportFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.youtube_fragment, youTubePlayerSupportFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        youTubePlayerSupportFragment.initialize("AIzaSyBv7ZolVdfsO1SIHz5SWNaQv_tr43JiWCc", this);
        videoId = id;
    }


    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        if (!wasRestored) {
            this.youTubePlayer = youTubePlayer;
            youTubePlayer.setShowFullscreenButton(false);
            youTubePlayer.setPlayerStateChangeListener(this);
            youTubePlayer.cueVideo(videoId); // Plays https://www.youtube.com/watch?v=fhWaJi1Hsfo
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this, RECOVERY_REQUEST).show();
        } else {
            String error = String.format("Play error", youTubeInitializationResult.toString());
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLoading() {

    }

    @Override
    public void onLoaded(String s) {
        youTubePlayer.play();
    }

    @Override
    public void onAdStarted() {

    }

    @Override
    public void onVideoStarted() {

    }

    @Override
    public void onVideoEnded() {
        showList();
    }

    @Override
    public void onError(YouTubePlayer.ErrorReason errorReason) {

    }

    private boolean showList() {
        if (youTubePlayer != null) {
            youTubePlayer.setFullscreen(false);
            youTubePlayer.pause();
            youTubePlayer.release();
            youTubePlayer = null;
            return true;
        }
        return false;
    }
}
