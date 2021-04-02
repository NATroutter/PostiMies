package net.natroutter.postimies.Utilities.objects;

public class Package {

    private Integer id;
    private String userID;
    private String userName;
    private String PackageName;
    private String trackingCode;
    private String courierName;
    private String couriercode;
    private String lastUpdate;

    public Package(Integer id, String userID, String userName, String PackageName, String trackingCode, String courierName, String courierCode, String lastUpdate) {
        this.id = id;
        this.userID = userID;
        this.userName = userName;
        this.PackageName = PackageName;
        this.trackingCode = trackingCode;
        this.courierName = courierName;
        this.couriercode = courierCode;
        this.lastUpdate = lastUpdate;
    }

    public Integer getId() {
        return id;
    }

    public String getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }

    public String getPackageName() {return PackageName;}

    public String getTrackingCode() {
        return trackingCode;
    }

    public String getCourierName() {
        return courierName;
    }

    public String getCourierCode() {
        return couriercode;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }
}
