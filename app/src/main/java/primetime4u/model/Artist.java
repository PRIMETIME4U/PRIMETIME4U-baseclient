package primetime4u.model;

/**
 * Created by Giovanni on 19/01/2015.
 */
public class Artist {
    private String idIMDB;
    private String name;
    private String poster;
    public Artist(){

    }
    public String getIdIMDB(){
        return idIMDB;
    }
    public String getName(){
        return name;
    }
    public String getPoster(){
        return poster;
    }

    public void setIdIMDB(String idIMDB) {
        this.idIMDB = idIMDB;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }
}
