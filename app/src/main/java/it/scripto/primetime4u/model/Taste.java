package it.scripto.primetime4u.model;

public class Taste {
    private int tasted;

    public boolean isTaste() {
        return tasted == 1;
    }

    public int getTasted() {
        return tasted;
    }

    public void setTasted(int tasted) {
        this.tasted = tasted;
    }

}
