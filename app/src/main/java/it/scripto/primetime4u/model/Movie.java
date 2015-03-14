package it.scripto.primetime4u.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Movie extends Taste {
    private String date;
    private String channel;
    private String idIMDB;
    private String italianPlot;
    private String time;
    private String title;
    private String originalTitle;
    @SerializedName("simple_plot")
    private String simplePlot;
    private String plot;
    @SerializedName("plot_it")
    private String plotIt;
    private String poster;
    private String genres;
    private String year;
    @SerializedName("run_times")
    private String runTimes;
    private String rated;
    private String trailer;
    private ArrayList<String> keywords;
    private ArrayList<String> countries;
    private ArrayList<Artist> directors;
    private ArrayList<Artist> writers;
    private ArrayList<Artist> actors;

    public Movie() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getIdIMDB() {
        return idIMDB;
    }

    public void setIdIMDB(String idIMDB) {
        this.idIMDB = idIMDB;
    }

    public String getItalianPlot() {
        return italianPlot;
    }

    public void setItalianPlot(String italianPlot) {
        this.italianPlot = italianPlot;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getSimplePlot() {
        return simplePlot;
    }

    public void setSimplePlot(String simplePlot) {
        this.simplePlot = simplePlot;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getPlotIt() {
        return plotIt;
    }

    public void setPlotIt(String plotIt) {
        this.plotIt = plotIt;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getRunTimes() {
        return runTimes;
    }

    public void setRunTimes(String runTimes) {
        this.runTimes = runTimes;
    }

    public String getRated() {
        return rated;
    }

    public void setRated(String rated) {
        this.rated = rated;
    }

    public String getTrailer() {
        return trailer;
    }

    public void setTrailer(String trailer) {
        this.trailer = trailer;
    }

    public ArrayList<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(ArrayList<String> keywords) {
        this.keywords = keywords;
    }

    public ArrayList<String> getCountries() {
        return countries;
    }

    public void setCountries(ArrayList<String> countries) {
        this.countries = countries;
    }

    public ArrayList<Artist> getDirectors() {
        return directors;
    }

    public void setDirectors(ArrayList<Artist> directors) {
        this.directors = directors;
    }

    public ArrayList<Artist> getWriters() {
        return writers;
    }

    public void setWriters(ArrayList<Artist> writers) {
        this.writers = writers;
    }

    public ArrayList<Artist> getActors() {
        return actors;
    }

    public void setActors(ArrayList<Artist> actors) {
        this.actors = actors;
    }
}