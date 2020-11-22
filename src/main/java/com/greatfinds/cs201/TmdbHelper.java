package com.greatfinds.cs201;

import com.greatfinds.cs201.db.MediaTitle;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.model.Genre;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.Multi;
import info.movito.themoviedbapi.model.core.ResponseStatusException;
import info.movito.themoviedbapi.model.tv.TvSeries;

import java.util.stream.Collectors;


public class TmdbHelper {
    public final static String imageBaseUrl = "https://image.tmdb.org/t/p/original";
    public final static String imageNotFoundUrl = "image-not-found.svg";

    private static TmdbApi tmdbApi;
    private static TmdbSearch tmdbSearch;

    public static TmdbApi getTmdbApi() {
        if (tmdbApi == null) {
            tmdbApi = new TmdbApi("d22899d42eec592df0e0940d796e6d98");
        }
        return tmdbApi;
    }

    public static void setTmdbApi(TmdbApi tmdbApi) {
        TmdbHelper.tmdbApi = tmdbApi;
    }

    public static TmdbSearch getTmdbSearch() {
        if (tmdbSearch == null) {
            tmdbSearch = getTmdbApi().getSearch();
        }
        return tmdbSearch;
    }

    public static void setTmdbSearch(TmdbSearch tmdbSearch) {
        TmdbHelper.tmdbSearch = tmdbSearch;
    }

    public static MediaTitle getMediaTitleFromMulti(Multi multi) {
        MediaTitle title;
        switch (multi.getMediaType()) {
            case MOVIE -> title = TmdbHelper.getMediaTitleFromMovieDb(((MovieDb) multi));
            case TV_SERIES -> title = TmdbHelper.getMediaTitleFromTvSeries(((TvSeries) multi));
            default -> title = null;
        }
        return title;
    }

    public static MediaTitle getMediaTitleFromMovieDb(MovieDb movie) {
        if (movie.getGenres() == null) {
            try {
                movie = TmdbHelper.getTmdbApi().getMovies().getMovie(movie.getId(), "en");
            } catch (ResponseStatusException e) {
                System.out.println(e.toString() + "\n" + movie);
            }
        }
        String genre = "";
        if (movie.getGenres() != null && !movie.getGenres().isEmpty())
            genre = movie.getGenres().stream().map(Genre::getName).collect(Collectors.joining(", "));
        return new MediaTitle(movie.getTitle(), genre, movie.getPosterPath() == null ? TmdbHelper.imageNotFoundUrl : TmdbHelper.imageBaseUrl + movie.getPosterPath());
    }

    public static MediaTitle getMediaTitleFromTvSeries(TvSeries series) {
        if (series.getGenres() == null) {
            try {
                series = TmdbHelper.getTmdbApi().getTvSeries().getSeries(series.getId(), "en");
            } catch (ResponseStatusException e) {
                e.printStackTrace();
                System.out.println(e.toString() + "\n" + series);
            }
        }
        String genre = "";
        if (series.getGenres() != null && !series.getGenres().isEmpty())
            genre = series.getGenres().stream().map(Genre::getName).collect(Collectors.joining(", "));
        return new MediaTitle(series.getName(), genre, series.getPosterPath() == null ? TmdbHelper.imageNotFoundUrl : TmdbHelper.imageBaseUrl + series.getPosterPath());
    }
}
