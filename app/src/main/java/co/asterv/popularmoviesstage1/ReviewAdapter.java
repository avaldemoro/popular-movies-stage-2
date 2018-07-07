package co.asterv.popularmoviesstage1;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import co.asterv.popularmoviesstage1.model.Movie;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private static ConstraintLayout mConstraintLayout;
    private Movie[] movies;
    private TextView authorTV;
    private TextView contentsTV;
    private Context context;

    public ReviewAdapter(Movie[] movies) {
        this.movies = movies;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView authorTV;
        TextView contentsTV;
        Button reviewButton;

        public ViewHolder(View itemView) {
            super (itemView);

            authorTV = (TextView) itemView.findViewById (R.id.reviewAuthorTextView);
            contentsTV = (TextView) itemView.findViewById (R.id.reviewContentTextView);
        }
        // each data item is just a string in this case

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_review, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.authorTV.setText(movies[position].getReviewAuthor());
        holder.contentsTV.setText (movies[position].getReviewContents());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (movies == null || movies.length == 0) {
            return -1;
        }
        return movies.length;
    }
}
