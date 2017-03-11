package codepath.gauravbajaj.com.flickster.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by gauravb on 3/6/17.
 */
public class Movie implements Parcelable {
    String posterPath;
    String backdropPath;
    String originalTitle;
    String overview;
    Double averageRatings;
    Integer ratingsCount;
    String releaseDate;

    public Integer getRatingsCount() {
        return ratingsCount;
    }

    public Double getAverageRatings() {
        return averageRatings;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getPosterPath() {
        return String.format("https://image.tmdb.org/t/p/w342/%s", posterPath);
    }

    public String getBackdropPath() {
        return String.format("https://image.tmdb.org/t/p/w780%s", backdropPath);
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public Movie(JSONObject jsonObject) throws JSONException {
        this.posterPath = jsonObject.getString("poster_path");
        this.originalTitle = jsonObject.getString("original_title");
        this.overview = jsonObject.getString("overview");
        this.backdropPath = jsonObject.getString("backdrop_path");
        this.ratingsCount = jsonObject.getInt("vote_count");
        this.averageRatings = jsonObject.getDouble("vote_average");
        this.releaseDate = jsonObject.getString("release_date");
    }

    public static ArrayList<Movie> fromJSONArray(JSONArray jsonArray) {
        ArrayList<Movie> results = new ArrayList<>();
        for (int x = 0; x < jsonArray.length(); x++) {
            try {
                results.add(new Movie(jsonArray.getJSONObject(x)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return results;
    }

    protected Movie(Parcel in) {
        posterPath = in.readString();
        backdropPath = in.readString();
        originalTitle = in.readString();
        overview = in.readString();
        averageRatings = in.readByte() == 0x00 ? null : in.readDouble();
        ratingsCount = in.readByte() == 0x00 ? null : in.readInt();
        releaseDate = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(posterPath);
        dest.writeString(backdropPath);
        dest.writeString(originalTitle);
        dest.writeString(overview);
        if (averageRatings == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(averageRatings);
        }
        if (ratingsCount == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(ratingsCount);
        }
        dest.writeString(releaseDate);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}