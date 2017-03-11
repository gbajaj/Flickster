package codepath.gauravbajaj.com.flickster.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import codepath.gauravbajaj.com.flickster.models.Movie;

/**
 * Created by gauravb on 3/10/17.
 */

public class ResultsParser {
    public ArrayList<Movie> nowPlaying(String responseData) throws JSONException {
        JSONObject jsonResponse = new JSONObject(responseData);
        JSONArray movieJsonResults = null;
        movieJsonResults = jsonResponse.getJSONArray("results");
        return Movie.fromJSONArray(movieJsonResults);
    }
}
