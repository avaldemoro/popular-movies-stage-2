package co.asterv.popularmoviesstage1;
import co.asterv.popularmoviesstage1.model.Movie;

import android.content.*;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.widget.*;
import android.view.ViewGroup;
import com.squareup.picasso.*;


public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private Movie[] mMovies;
    private Context mContext;

    public ImageAdapter(Context mContext, Movie[] mMovies) {
        this.mMovies = mMovies;

        if (mContext == null) {
            try{
                Thread.sleep(1000);
            } catch(InterruptedException exception){
                exception.printStackTrace();
            }
        } else {
            this.mContext = mContext;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;

        public ViewHolder(ImageView v) {
            super(v);
            mImageView = v;
        }
    }
    @NonNull
    @Override
    // Create new views (Invoked by the Layout Manager)
    public ImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Create a new view
        ImageView v = (ImageView) LayoutInflater.from(parent.getContext ())
                .inflate (R.layout.image_thumb_view, parent, false);

        ViewHolder vh = new ViewHolder (v);
        return vh;
    }

    @Override
    // Replace the contents of a view (Invoked by the layout manager)
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Picasso picasso = Picasso.with(mContext);
        //picasso.setLoggingEnabled(true);
        picasso.load(mMovies[position].getPosterPath())
                .fit()
                .error(R.mipmap.ic_launcher)
                .placeholder(R.mipmap.ic_launcher_round)
                .into((ImageView) holder.mImageView.findViewById (R.id.image_view));

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, MovieDetails.class);
            intent.putExtra("movie", mMovies[position]);
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        if (mMovies == null || mMovies.length == 0) {
            return -1;
        }
        return mMovies.length;
    }

    public Movie[] getMovies() {
        return mMovies;
    }

    public void setMovies(Movie[] movies) {
        mMovies = movies;
    }

}