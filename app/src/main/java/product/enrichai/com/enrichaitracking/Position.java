/*
 * Copyright 2015 - 2018 Anton Tananaev (anton.tananaev@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package product.enrichai.com.enrichaitracking;

import android.location.Location;
import android.location.LocationManager;
import android.os.Build;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Date;

public class Position implements Serializable{

    /**
     * List of features that characterize any Location on map
     * NOTE : Data Member "id" - Don't change the value of this field as it has been set to AutoIncrement
     */

    @SerializedName("id")
    private long id;

    @SerializedName("tripId")
    private int tripId;

    @SerializedName("deviceId")
    private String deviceId;

    @SerializedName("deviceType")
    private String deviceType;

    @SerializedName("timeStamp")
    private Date time;

    @SerializedName("lat")
    private double latitude;

    @SerializedName("lng")
    private double longitude;

    @SerializedName("altd")
    private double altitude;

    @SerializedName("speed")
    private double speed;

    @SerializedName("course")
    private double course;

    @SerializedName("horizontalAccuracy")
    private double accuracy;

    @SerializedName("battery")
    private int battery;

    @SerializedName("mock")
    private boolean mock;

    public Position() {
    }

    public Position(int tripId, String deviceId, Location location, int battery) {
        this.tripId = tripId;
        this.deviceId = deviceId;
        this.deviceType = "android";
        time = new Date(location.getTime());
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        altitude = location.getAltitude();
        speed = location.getSpeed() * 1.943844; // speed in knots
        course = location.getBearing();
        if (location.getProvider() != null && !location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            accuracy = location.getAccuracy();
        }
        this.battery = battery;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            this.mock = location.isFromMockProvider();
        }
    }

    public void setId(long id) {
        this.id = id;
    }
    public void setTripId(int tripId){
        this.tripId = tripId;
    }
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    public void setDeviceType(String deviceType){
        this.deviceType = deviceType;
    }
    public void setTime(Date time) {
        this.time = time;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }
    public void setSpeed(double speed) {
        this.speed = speed;
    }
    public void setCourse(double course) {
        this.course = course;
    }
    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }
    public void setBattery(int battery) {
        this.battery = battery;
    }
    public void setMock(boolean mock) {
        this.mock = mock;
    }


    public long getId() {
        return id;
    }
    public int getTripId(){
        return tripId;
    }
    public String getDeviceId() {
        return deviceId;
    }
    public String getDeviceType(){
        return deviceType;
    }
    public Date getTime() {
        return time;
    }
    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public double getAltitude() {
        return altitude;
    }
    public double getSpeed() {
        return speed;
    }
    public double getCourse() {
        return course;
    }
    public double getAccuracy() {
        DecimalFormat formatter = new DecimalFormat("#0.0000");
        double new_accuracy = Double.parseDouble(formatter.format(accuracy));
        if(new_accuracy == 0.0000)
            return -1;
        return accuracy;
    }
    public int getBattery() {
        return battery;
    }
    public boolean getMock() {
        return mock;
    }

    @Override
    public String toString() {
        return "Position{" +
                "id=" + id +
                ", tripId=" + tripId +
                ", deviceId='" + deviceId + '\'' +
                ", deviceType=" + deviceType +
                ", timeStamp=" + time +
                ", lat=" + latitude +
                ", lng=" + longitude +
                ", altitude=" + altitude +
                ", speed=" + speed +
                ", course=" + course +
                ", horizontalAccuracy=" + accuracy +
                ", battery=" + battery +
                ", mock=" + mock +
                '}';
    }
}
