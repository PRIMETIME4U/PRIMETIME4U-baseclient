package primetime4u.model;

public class Proposal {
    
    private String channel;
    private String idIMDB;
    private String originalTitle;
    private String poster;
    private String simplePlot;
    private String time;

    public Proposal() {
    }

    public Proposal(String channel, String idIMDB, String originalTitle, String poster, String simplePlot, String time) {
        this.channel = channel;
        this.idIMDB = idIMDB;
        this.originalTitle = originalTitle;
        this.poster = poster;
        this.simplePlot = simplePlot;
        this.time = time;
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
}
