package com.example.turistmedia;

public class ModelUser {
    String name,email,search,phone,image,cover;

    public ModelUser() {

    }

    public ModelUser(String name, String email, String search, String phone, String image, String cover) {
        this.name = name;
        this.email = email;
        this.search = search;
        this.phone = phone;
        this.image = image;
        this.cover = cover;
    }
}
