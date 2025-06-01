package com.ucucite.block_one_mob_dev;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {
    private int id;
    private String name;
    private String price;
    private int imageResource;
    private String rating;
    private String brand;
    private String ingredients;

    // Constructor
    public Product(int id, String name, String price, int imageResource, String rating, String brand, String ingredients) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageResource = imageResource;
        this.rating = rating;
        this.brand = brand;
        this.ingredients = ingredients;
    }

    // Parcelable constructor
    protected Product(Parcel in) {
        id = in.readInt();
        name = in.readString();
        price = in.readString();
        imageResource = in.readInt();
        rating = in.readString();
        brand = in.readString();
        ingredients = in.readString();
    }

    // Parcelable Creator
    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(price);
        dest.writeInt(imageResource);
        dest.writeString(rating);
        dest.writeString(brand);
        dest.writeString(ingredients);
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getPrice() { return price; }
    public int getImageResource() { return imageResource; }
    public String getRating() { return rating; }
    public String getBrand() { return brand; }
    public String getIngredients() { return ingredients; }

    // Setters (if needed)
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPrice(String price) { this.price = price; }
    public void setImageResource(int imageResource) { this.imageResource = imageResource; }
    public void setRating(String rating) { this.rating = rating; }
    public void setBrand(String brand) { this.brand = brand; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }
}