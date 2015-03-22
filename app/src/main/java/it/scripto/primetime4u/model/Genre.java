package it.scripto.primetime4u.model;

public class Genre extends Taste {
    private String name;

    public Genre() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof Genre){
            Genre param = (Genre) o;
            return ((param.getName().equals(this.getName())));
        }
        return false;
    }
}
