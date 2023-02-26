package com.example.mobilemessagingapp.models;

public class Contact {
    private String Image, UserName, Email, Password, Gender, DateDfBirth, Address;

    public Contact(){}

    public Contact(String Image, String userName, String email, String password, String genDer, String dateofBirth, String Address) {
        this.Image = Image;
        this.UserName = userName;
        this.Email = email;
        this.Password = password;
        this.Gender = genDer;
        this.DateDfBirth = dateofBirth;

    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        this.UserName = userName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getGenDer() {
        return Gender;
    }

    public void setGenDer(String genDer) {
        Gender = genDer;
    }

    public String getDateofBirth() {
        return DateDfBirth;
    }

    public void setDateofBirth(String dateofBirth) {
        DateDfBirth = dateofBirth;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
}
