package it.scripto.primetime4u.model;

public class Proposal {
    private String channel;
    private String idIMDB;
    private String italianPlot;
    private String originalTitle;
    private String poster;
    private String simplePlot;
    private String time;
    private String runTimes;
    private String title;

    public Proposal() {
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

    public String getSimplePlot() {
        return simplePlot;
    }

    public void setSimplePlot(String simplePlot) {
        this.simplePlot = simplePlot;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setRunTimes(String runTimes) {
        this.runTimes = runTimes;
    }
    public String getRunTimes(){
        return runTimes;
    }
    public void setTitle(String title){
        this.title=title;
    }
    public String getTitle(){
        return title;
    }
}
