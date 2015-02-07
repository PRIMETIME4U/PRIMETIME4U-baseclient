package it.scripto.primetime4u.model;

import java.util.List;

public class ServerResponse {
    
    public static class Response {
        public String code;
        public String errorMessage = null;
        public String errorType = null;
    }

    public static class DetailResponse extends Response {
        public DetailData data;
        public String idIMDB;
        public String type;
    }
    
    public static class DetailData {
        public Detail detail;
    }
    
    public static class ProposalResponse extends Response {
        public ProposalData data;
    }

    public static class ProposalData {
        public List<Proposal> proposal;
        public String user_id;
    }
    
    public static class TasteResponse extends Response {
        public TasteData data;
    }

    public static class TasteData {
        public TastesData tastes;
        public String type;
        public String user_id;
    }
    
    public static class TastesData {
        public List<Artist> artists;
        public List<Movie> movies;
        public List<Genre> genres;
    }

    public static class WatchedResponse extends Response {
        public WatchedData data;
        public String user_id;
    }
    
    public static class WatchedData {
        public List<Watched> watched;
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