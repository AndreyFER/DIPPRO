package fer.hr.photomap.data.model;

import java.io.Serializable;
import java.util.Objects;

public class EventData implements Serializable {
    private String description;
    private double latitude;
    private double longitude;
    private String image;
    private int type;
    private String user;


    public EventData(String description, double latitude, double longitude, String image, int type, String user) {
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.image = image;
        this.type = type;
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventData eventData = (EventData) o;
        return Double.compare(eventData.latitude, latitude) == 0 && Double.compare(eventData.longitude, longitude) == 0 && type == eventData.type && description.equals(eventData.description) && Objects.equals(image, eventData.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, latitude, longitude, image, type);
    }

    @Override
    public String toString() {
        return "EventData{" +
                "description='" + description + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", image='" + image + '\'' +
                ", type=" + type +
                '}';
    }
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
