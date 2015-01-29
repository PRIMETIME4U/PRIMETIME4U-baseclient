package it.scripto.primetime4u.model;

public class Watched {
    private String date;
    private String idIMDB;
    private String title;
    private String originalTitle;
    private String poster;
    private int tasted;

    public Watched() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIdIMDB() {
        return idIMDB;
    }

    public void setIdIMDB(String idIMDB) {
        this.idIMDB = idIMDB;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public int getTasted() {
        return tasted;
    }

    public void setTasted(int tasted) {
        this.tasted = tasted;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
