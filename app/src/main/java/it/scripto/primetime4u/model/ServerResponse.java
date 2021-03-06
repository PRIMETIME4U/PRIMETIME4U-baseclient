package it.scripto.primetime4u.model;

import java.util.List;

public class ServerResponse {
    
    public static class Response {
        public String code;
        public String errorMessage = null;
        public String errorType = null;
    }

    public static class PaginateResponse {
        public String nextPage;
        public String previousPage;
    }

    public static class DetailResponse extends Response {
        public DetailData data;
        public String idIMDB;
        public String type;
    }
    
    public static class DetailData {
        public Movie detail;
    }
    
    public static class ProposalResponse extends Response {
        public ProposalData data;
    }

    public static class ProposalData {
        public List<Movie> proposal;
        public String user_id;
    }
    public static class TasteResponseExtraMovies extends Response{
        public TasteDataExtraMovies data;
    }
    public static class TasteResponseExtraGenres extends Response{
        public TasteDataExtraGenres data;
    }
    public static class TasteResponseExtraArtists extends Response{
        public TasteDataExtraArtists data;
    }
    public static class TasteResponse extends Response {
        public TasteData data;
    }
    public static class TasteDataExtraMovies extends PaginateResponse{
        public List<Movie> tastes;
        public String next_page;
    }
    public static class TasteDataExtraArtists extends PaginateResponse{
        public List<Artist> tastes;
        public String next_page;
    }

    public static class TasteDataExtraGenres extends PaginateResponse{
        public List<Genre> tastes;
        public String next_page;
    }


    public static class TasteData{
        public TastesData tastes;
        public String type;
        public String user_id;
    }
    
    public static class TastesData {
        //all page tastes response
        public List<Artist> artists;
        public List<Movie> movies;
        public List<Genre> genres;
        public String next_page_artists;
        public String next_page_movies;
        public String next_page_genres;
    }

    public static class WatchedResponse extends Response {
        public WatchedData data;
        public String user_id;
    }
    
    public static class WatchedData extends PaginateResponse {
        public List<Movie> watched;
    }

    public static class SuggestResponse extends Response {
        public SuggestData data;
        public String query;
    }

    public static class SuggestData {
        public List<Artist> artists;
        public List<Genre> genres;
        public List<Movie> movies;
    }
}