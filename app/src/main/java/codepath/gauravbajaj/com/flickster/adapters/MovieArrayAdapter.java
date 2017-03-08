package codepath.gauravbajaj.com.flickster.adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import codepath.gauravbajaj.com.flickster.R;
import codepath.gauravbajaj.com.flickster.models.Movie;

/**
 * Created by gauravb on 3/6/17.
 */

public class MovieArrayAdapter extends ArrayAdapter<Movie> {
    private static class ViewHolder {
        ImageView lvImageView;
        TextView title;
        TextView overView;
    }

    public MovieArrayAdapter(Context context, List<Movie> movies) {
        super(context, android.R.layout.simple_list_item_1, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //get the data item for position
        Movie movie = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();

            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.item_movie, parent, false);
            // find the image view
            viewHolder.lvImageView = (ImageView) convertView.findViewById(R.id.ivMovieImage);
            // find the title textview
            viewHolder.title = (TextView) convertView.findViewById(R.id.tvTitle);
            // find the overview textview
            viewHolder.overView = (TextView) convertView.findViewById(R.id.tvOverview);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //clear out the image from convert view
        viewHolder.lvImageView.setImageResource(0);

        //Populate the data
        viewHolder.title.setText(movie.getOriginalTitle());
        viewHolder.overView.setText(movie.getOverview());
        int orientation = getContext().getResources().getConfiguration().orientation;
        String imagePath = movie.getPosterPath();
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            imagePath = movie.getBackdropPath();
        }
        Picasso.with(getContext())
        .load(imagePath).into(viewHolder.lvImageView);

        //return the view
        return convertView;
    }
}
