package codepath.gauravbajaj.com.flickster;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import codepath.gauravbajaj.com.flickster.adapters.MovieArrayAdapter;
import codepath.gauravbajaj.com.flickster.models.Movie;
import codepath.gauravbajaj.com.flickster.network.Request;
import codepath.gauravbajaj.com.flickster.parser.ResultsParser;

public class MainActivity extends AppCompatActivity implements MovieArrayAdapter.PlayYouTube, YouTubePlayer.OnInitializedListener,
        YouTubePlayer.PlayerStateChangeListener {

    private static final int RECOVERY_REQUEST = 1;
    private ArrayList<Movie> movies = new ArrayList<>();
    private MovieArrayAdapter movieAdapter;
    private YouTubePlayer youTubePlayer;
    private Handler handler = new Handler();
    private String videoId;

    //View Members
    @BindView(R.id.lvMovies)
    ListView lvItems;


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


    @Override
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
        YouTubePlayer player = youTubePlayer;
        if (player != null) {
            youTubePlayer.setFullscreen(false);
            youTubePlayer.pause();
            youTubePlayer.release();
            youTubePlayer = null;
            return true;
        }
        return false;
    }
}
