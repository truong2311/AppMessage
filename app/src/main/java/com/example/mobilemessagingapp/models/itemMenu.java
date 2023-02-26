package com.example.mobilemessagingapp.models;

public class itemMenu {
    private int iditem;
    private int image;
    private String name;

    public itemMenu(int iditem, int image, String name) {
        this.iditem = iditem;
        this.image = image;
        this.name = name;
    }

    public int getIditem() {
        return iditem;
    }

    public void setIditem(int iditem) {
        this.iditem = iditem;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
