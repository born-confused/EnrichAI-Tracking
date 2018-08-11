package product.enrichai.com.enrichaitracking;

/**
 * Created by WELCOME on 6/18/2018.
 */

public class Trip {
    private int tripId;
    private String startTime;
    private String endTime;

    public Trip(int tripId, String startTime) {
        this.tripId = tripId;
        this.startTime = startTime;
    }

    /**
     * Returns the trip Id. In simple words, the trip number.
     * @return - return type is Integer
     */
    public int getTripId() {
        return tripId;
    }

    /**
     * Returns the time at which this particular trip started.
     * @return - Return format is - HH:MM
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * Returns the time at which this particular trip ended.
     * @return - Return format is - HH:MM
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * Sets the trip Id of this particular trip. Mostly the trip id is generated automatically.
     * Try not interfering with the trip id
     * @param tripId
     */
    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    /**
     * Sets the starting time of the trip. This is normally called when this trip is created.
     * To elaborate more; when user starts the tracking service, a new trip begins.

     * @param startTime
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * Sets the ending time of the trip. This is normally called when the trip is ended.
     * To elaborate more; when user ends the tracking service, trip ends.
     * @param endTime
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "tripId=" + tripId +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}
