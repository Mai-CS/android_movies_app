package com.example.mai.movies;

public class Movie {

    private String id;
    private String title;
    private String poster_path;
    private String backdrop_path;
    private String release_date;
    private String vote_average;
    private String overview;

    private String author;
    private String content;

    private String key;

    private boolean isFavorite;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        if (title == null || title.equals(""))
            title = "N/A";
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getBackdrop_path() {
        if (backdrop_path != null)
            return backdrop_path;
        else
            return poster_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public String getRelease_date() {
        if (release_date == null || release_date.equals(""))
            release_date = "N/A";
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getVote_average() {
        if (vote_average == null || vote_average.equals(""))
            vote_average = "N/A";
        return vote_average;
    }

    public void setVote_average(String vote_average) {
        this.vote_average = vote_average;
    }

    public String getOverview() {
        if (overview == null || overview.equals(""))
            overview = "N/A";
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getAuthor() {
        if (author == null)
            author = "";
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        if (content == null)
            content = "N/A";
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getKey() {
        //to avoid NullPointerException
        if (key == null)
            key = "";
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    public boolean isFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Movie) {
            Movie movie = (Movie) o;
            return this.id.equals(movie.id);
        }
        return false;
    }


}
