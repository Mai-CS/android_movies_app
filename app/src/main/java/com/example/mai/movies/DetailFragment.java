package com.example.mai.movies;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.text.Html;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.mai.movies.data.MoviesDbHelper;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment {

    private static final String MOVIE_SHARE_HASH_TAG = "#MovieApp\n";
    private String[] keys; //trailers array
    private int[] isFullReview; // determine whether text is expanded or collapsed

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        ((TextView) rootView.findViewById(R.id.title)).setText(MovieFragment.movie.getTitle());

        ImageView cover_image_view = (ImageView) rootView.findViewById(R.id.cover);
        String coverUrl = "http://image.tmdb.org/t/p/w342/" + MovieFragment.movie.getBackdrop_path();
        Glide.with(getActivity())
                .load(coverUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(cover_image_view);

        ImageView poster_image_view = (ImageView) rootView.findViewById(R.id.poster);
        String posterUrl = "http://image.tmdb.org/t/p/w185/" + MovieFragment.movie.getPoster_path();
        Glide.with(getActivity())
                .load(posterUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(poster_image_view);

        String release_dataStr = MovieFragment.movie.getRelease_date();
        TextView release_date = ((TextView) rootView.findViewById(R.id.release_date));
        release_date.setText(release_dataStr);

        String vote_averageStr = MovieFragment.movie.getVote_average() + "/10";
        TextView vote_average = ((TextView) rootView.findViewById(R.id.vote_average));
        vote_average.setText(vote_averageStr);

        String overviewStr = MovieFragment.movie.getOverview();
        TextView overview = (TextView) rootView.findViewById(R.id.overview);
        overview.setText(overviewStr);
        overview.setPadding(0, 16, 0, 0);

        addTrailers(rootView);

        addReviews(rootView);

        return rootView;
    }

    //show list of trailers on demand
    public void addTrailers(View rootView) {
        Button trailers_button = (Button) rootView.findViewById(R.id.trailers_button);
        keys = MovieFragment.movie.getKey().split("#");
        //hide trailers_button if no trailer found
        if (keys[0].equals(""))
            trailers_button.setVisibility(View.GONE);
        else {
            String[] trailerText = new String[keys.length];
            for (int i = 0; i < trailerText.length; i++)
                trailerText[i] = "Trailer " + (i + 1);

            ArrayAdapter<String> keysAdapter =
                    new ArrayAdapter<>(
                            getActivity(),
                            R.layout.list_item_trailer,
                            R.id.trailer_text_view,
                            new ArrayList<>(Arrays.asList(trailerText)));

            final ListPopupWindow trailerList = new ListPopupWindow(getActivity());
            trailerList.setAdapter(keysAdapter);
            trailerList.setAnchorView(trailers_button);

            trailers_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.startAnimation(new AlphaAnimation(1F, 0.8F));
                    trailerList.show();
                }
            });

            trailerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        //play video through youtube
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                parseTrailer(keys[position])));
                    } catch (Exception ex) {
                        Toast.makeText(getActivity(), "Sorry, no trailer is available!",
                                Toast.LENGTH_SHORT).show();
                    }
                    trailerList.dismiss();
                }
            });
        }

    }

    //draw number of Reviews
    public void addReviews(View rootView) {
        LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.reviews_linear_layout);
        String[] authors = MovieFragment.movie.getAuthor().split("#");
        String[] contents = MovieFragment.movie.getContent().split("#");
        isFullReview = new int[authors.length];

        //hide reviews section if no review found
        if (authors[0].equals("")) {
            linearLayout.setVisibility(View.GONE);
            (rootView.findViewById(R.id.reviews_separator)).setVisibility(View.GONE);
        } else {
            for (int i = 0; i < authors.length; i++) {
                //initialize isFullReview elements = 0
                isFullReview[i] = 0;
                final TextView review_text_view = new TextView(getActivity());
                review_text_view.setPadding(0, 16, 0, 16);
                review_text_view.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        getResources().getDimension(R.dimen.text_body));
                review_text_view.setSingleLine(true);
                review_text_view.setEllipsize(TextUtils.TruncateAt.END);

                //change text color for author
                final String author = "<font color=#000>by " + authors[i] + "</font>";
                final String content = " - " + contents[i];
                review_text_view.setText((Html.fromHtml(author)));
                review_text_view.append(content);

                linearLayout.addView(review_text_view,
                        new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

                //expand/collapse text
                final int index = i;
                review_text_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isFullReview[index] == 0) {
                            //expand TextView
                            review_text_view.setSingleLine(false);
                            review_text_view.setEllipsize(null);
                            isFullReview[index] = 1;
                        } else {
                            //collapse TextView
                            review_text_view.setSingleLine(true);
                            review_text_view.setEllipsize(TextUtils.TruncateAt.END);
                            isFullReview[index] = 0;
                        }
                    }
                });
            }
        }
    }

    //get youtube link of the trailers
    public Uri parseTrailer(String key) {
        return Uri.parse("http://www.youtube.com/watch?v=" + key);
    }

    //insert movie into database
    public void insertFavorite() {
        try {
            MoviesDbHelper moviesDbHelper = new MoviesDbHelper(getActivity());
            moviesDbHelper.insertFavorite(MovieFragment.movie);
            MovieFragment.favoritesList.add(MovieFragment.movie);
            Toast.makeText(getActivity(), "Now, you can view movie details offline",
                    Toast.LENGTH_SHORT).show();

        } catch (Exception ex) {
            Toast.makeText(getActivity(), "Can't add this movie",
                    Toast.LENGTH_SHORT).show();
        }
    }

    //delete movie from database
    public void deleteFavorite() {
        try {
            MoviesDbHelper moviesDbHelper = new MoviesDbHelper(getActivity());
            moviesDbHelper.deleteFavorite(MovieFragment.movie);
            MovieFragment.favoritesList.remove(MovieFragment.movie);
            Toast.makeText(getActivity(), "Removed from favorites",
                    Toast.LENGTH_SHORT).show();

        } catch (Exception ex) {
            Toast.makeText(getActivity(), "Can't remove this movie",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                if (keys[0].equals(""))
                    Toast.makeText(getActivity(), "No trailer found",
                            Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_favorite:
                if (!MovieFragment.movie.isFavorite()) {
                    insertFavorite();
                    MovieFragment.movie.setIsFavorite(true);
                    item.setIcon(R.drawable.ic_favorite_white_48px);
                } else {
                    deleteFavorite();
                    MovieFragment.movie.setIsFavorite(false);
                    item.setIcon(R.drawable.ic_favorite_border_white_48px);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_detail, menu);

        // Retrieve the share menu item
        MenuItem shareMenuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        ShareActionProvider shareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(shareMenuItem);

        // Attach an intent to this ShareActionProvider.
        if (shareActionProvider != null) {
            if (!keys[0].equals(""))
                shareActionProvider.setShareIntent(shareTrailerIntent());
        } else {
            Toast.makeText(getActivity(), "Can't share the trailer",
                    Toast.LENGTH_SHORT).show();
        }

        //check whether the movie is favorite or not
        MenuItem favoriteMenuItem = menu.findItem(R.id.action_favorite);
        if (!MovieFragment.movie.isFavorite())
            //if the item isn't a favorite, user may add it to favorites
            favoriteMenuItem.setIcon(R.drawable.ic_favorite_border_white_48px);
        else
            //if the item is already a favorite, user may remove it from favorites
            favoriteMenuItem.setIcon(R.drawable.ic_favorite_white_48px);
    }

    private Intent shareTrailerIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        //share the first movie trailer
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                MOVIE_SHARE_HASH_TAG + "http://www.youtube.com/watch?v=" + keys[0]);
        return shareIntent;
    }


}
