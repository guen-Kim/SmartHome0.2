package org.techtown.smarthome02;

public class HomeState {
    public double Temperature;
    public double Humiduty;


    public HomeState(){};



    public HomeState(double temperature, double humidity) {
        this.Temperature = temperature;
        this.Humiduty = humidity;
    }
}
