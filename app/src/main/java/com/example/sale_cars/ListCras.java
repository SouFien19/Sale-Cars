package com.example.sale_cars;

public class ListCras {
    private String carId;
    private String imageUrl;
    private String carTitle;
    public ListCras(String carId, String imageUrl, String carTitle) {
        this.carId = carId;
        this.imageUrl = imageUrl;
        this.carTitle = carTitle;
    }

    public String getCarId() {
        return carId;
    }
    public String getCarTitle() {
        return carTitle;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
