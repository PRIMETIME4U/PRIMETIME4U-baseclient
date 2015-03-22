package it.scripto.primetime4u.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Movie extends Taste implements Parcelable {
    private String date;
    private String channel;
    private String channelNumber;
    private String idIMDB;
    private String italianPlot;
    private String time;
    private String title;
    private String originalTitle;
    private String simplePlot;
    private String plot;
    private String poster;
    private String genres;
    private String year;
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

    public String getChannelNumber() {
        return channelNumber;
    }

    public void setChannelNumber(String channelNumber) {
        this.channelNumber = channelNumber;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.date);
        dest.writeString(this.channel);
        dest.writeString(this.channelNumber);
        dest.writeString(this.idIMDB);
        dest.writeString(this.italianPlot);
        dest.writeString(this.time);
        dest.writeString(this.title);
        dest.writeString(this.originalTitle);
        dest.writeString(this.simplePlot);
        dest.writeString(this.plot);
        dest.writeString(this.poster);
        dest.writeString(this.genres);
        dest.writeString(this.year);
        dest.writeString(this.runTimes);
        dest.writeString(this.rated);
        dest.writeString(this.trailer);
        dest.writeSerializable(this.keywords);
        dest.writeSerializable(this.countries);
        dest.writeSerializable(this.directors);
        dest.writeSerializable(this.writers);
        dest.writeSerializable(this.actors);
        dest.writeInt(this.tasted);
    }

    private Movie(Parcel in) {
        this.date = in.readString();
        this.channel = in.readString();
        this.channelNumber = in.readString();
        this.idIMDB = in.readString();
        this.italianPlot = in.readString();
        this.time = in.readString();
        this.title = in.readString();
        this.originalTitle = in.readString();
        this.simplePlot = in.readString();
        this.plot = in.readString();
        this.poster = in.readString();
        this.genres = in.readString();
        this.year = in.readString();
        this.runTimes = in.readString();
        this.rated = in.readString();
        this.trailer = in.readString();
        this.keywords = (ArrayList<String>) in.readSerializable();
        this.countries = (ArrayList<String>) in.readSerializable();
        this.directors = (ArrayList<Artist>) in.readSerializable();
        this.writers = (ArrayList<Artist>) in.readSerializable();
        this.actors = (ArrayList<Artist>) in.readSerializable();
        this.tasted = in.readInt();
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public boolean equals(Object o){
        if (o instanceof Movie){
            Movie param = (Movie) o;
            return ((param.getIdIMDB().equals(this.getIdIMDB())));
        }
        return false;
    }
}