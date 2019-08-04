package com.andela.travelmantic.model;

public class PostModel {

    //  travelmantic list

    public String name;
    public String userID, itemPrice, itemDescription, itemImageUrl;

    public String getName() {
        return name;
    }


    public String getItemPrice() {
        return itemPrice;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public String getItemImageUrl() {
        return itemImageUrl;
    }


    public String getUserID() {
        return userID;
    }

    public PostModel(String userID, String name, String price, String description, String imgUrl) {
        this.userID = userID;
        this.name = name;
        this.itemPrice = price;
        this.itemDescription = description;
        this.itemImageUrl = imgUrl;
    }

    public PostModel() {
    }


}
