package com.example.sale_cars;

public class Car {
    private String carId, userId, carTitle, carDescription, imageUrl;

    public Car(String carId, String userId, String carTitle, String carDescription, String imageUrl) {
        this.carId = carId;
        this.userId = userId;
        this.carTitle = carTitle;
        this.carDescription = carDescription;
        this.imageUrl = imageUrl;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCarTitle() {
        return carTitle;
    }

    public void setCarTitle(String carTitle) {
        this.carTitle = carTitle;
    }

    public String getCarDescription() {
        return carDescription;
    }

    public void setCarDescription(String carDescription) {
        this.carDescription = carDescription;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
