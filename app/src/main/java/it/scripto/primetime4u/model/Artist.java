package it.scripto.primetime4u.model;

public class Artist extends Taste {
    private String idIMDB;
    private String name;
    private String photo;
    public Artist(){

    }
    public String getIdIMDB(){
        return idIMDB;
    }
    public String getName(){
        return name;
    }
    public String getPhoto(){
        return photo;
    }

    public void setIdIMDB(String idIMDB) {
        this.idIMDB = idIMDB;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
