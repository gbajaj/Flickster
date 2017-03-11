package codepath.gauravbajaj.com.flickster;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import codepath.gauravbajaj.com.flickster.adapters.MovieArrayAdapter;
import codepath.gauravbajaj.com.flickster.models.Movie;
import codepath.gauravbajaj.com.flickster.network.Request;
import codepath.gauravbajaj.com.flickster.parser.ResultsParser;

public class MainActivity extends AppCompatActivity {
    ArrayList<Movie> movies = new ArrayList<>();
    MovieArrayAdapter movieAdapter;
    @BindView(R.id.lvMovies)
    ListView lvItems;
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
}
