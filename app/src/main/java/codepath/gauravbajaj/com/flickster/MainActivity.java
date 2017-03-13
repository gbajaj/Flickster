package codepath.gauravbajaj.com.flickster;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import codepath.gauravbajaj.com.flickster.adapters.MovieArrayAdapter;
import codepath.gauravbajaj.com.flickster.models.Movie;
import codepath.gauravbajaj.com.flickster.network.Request;
import codepath.gauravbajaj.com.flickster.parser.ResultsParser;

public class MainActivity extends YouTubeBaseActivity implements MovieArrayAdapter.PlayYouTube, YouTubePlayer.OnInitializedListener,
        YouTubePlayer.PlayerStateChangeListener {

    private static final int RECOVERY_REQUEST = 1;
    ArrayList<Movie> movies = new ArrayList<>();
    MovieArrayAdapter movieAdapter;
    @BindView(R.id.lvMovies)
    ListView lvItems;
    @BindView(R.id.player)
    YouTubePlayerView youTubePlayerView;

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        lvItems = (ListView) findViewById(R.id.lvMovies);
        movieAdapter = new MovieArrayAdapter(this, movies);
        lvItems.setAdapter(movieAdapter);

        //Keep Activity as light as possible by moving business logic to helper java classes
        final Request request = new Request();
        final ResultsParser resultsParser = new ResultsParser();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Fetch Network Response
                    String responseData = request.nowPlaying();
                    //Parse and read Network response
                    movies.clear();
                    movies.addAll(resultsParser.nowPlaying(responseData));
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (MainActivity.this.isFinishing()) {
                                return;
                            }
                            movieAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    String thisid;

    @Override
    public void playYouTube(String id) {
        youTubePlayerView.initialize("AIzaSyBv7ZolVdfsO1SIHz5SWNaQv_tr43JiWCc", MainActivity.this);
        thisid = id;
        youTubePlayerView.setVisibility(View.VISIBLE);
    }

    YouTubePlayer youTubePlayer;

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        if (!wasRestored) {
            this.youTubePlayer = youTubePlayer;
            youTubePlayer.setShowFullscreenButton(false);
            youTubePlayer.setPlayerStateChangeListener(this);
            youTubePlayer.cueVideo(thisid); // Plays https://www.youtube.com/watch?v=fhWaJi1Hsfo
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize("AIzaSyBv7ZolVdfsO1SIHz5SWNaQv_tr43JiWCc", this);
        }
    }

    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return youTubePlayerView;
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

    @Override
    public void onBackPressed() {
        if (showList()) {
            return;
        }
        super.onBackPressed();

    }

    private boolean showList() {
        if (youTubePlayer != null && youTubePlayer.isPlaying()) {
            youTubePlayer.setFullscreen(false);
            youTubePlayer.pause();
            youTubePlayer.release();
            youTubePlayerView.setVisibility(View.GONE);
            return true;
        }
        return false;
    }
}
