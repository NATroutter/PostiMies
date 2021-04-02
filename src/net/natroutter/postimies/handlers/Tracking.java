package net.natroutter.postimies.handlers;

import com.google.gson.Gson;
import net.natroutter.postimies.Postimies;
import net.natroutter.postimies.Utilities.Config;
import net.natroutter.postimies.Utilities.Logger;
import net.natroutter.postimies.Utilities.objects.CourierData;
import net.natroutter.postimies.Utilities.objects.TrackingData;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

import java.util.List;

public class Tracking {

    private static final Config cfg = Postimies.getConfig();

    private Gson gson = new Gson();

    String key = cfg.getTrackingMoreApiKey();


    public CourierData.courier detectCourier(String TrackingNumber) {
        if (!ValidateApiKey(key)) {return null;}

        try {
            Connection con = Jsoup.connect("https://api.trackingmore.com/v3/trackings/detect");
            con.ignoreContentType(true);
            con.method(Connection.Method.POST);
            con.header("Content-Type", "application/json");
            con.header("Tracking-Api-Key", key);
            con.requestBody("{\"tracking_number\":\"" + TrackingNumber + "\"}");
            Response resp = con.execute();
            if (ValidateStatus(resp.statusCode())) {
                CourierData json = gson.fromJson(resp.body(), CourierData.class);
                return json.data.get(0);
            }
        } catch (Exception e) {
            Logger.Error("Failed to get carrier from api!");
            e.printStackTrace();
        }
        return null;
    }

    public boolean create(String trackingNumber, String courierCode) {
        if (!ValidateApiKey(key)) {return false;}

        try {
            Connection con = Jsoup.connect("https://api.trackingmore.com/v3/trackings/create");
            con.ignoreContentType(true);
            con.method(Connection.Method.POST);
            con.header("Content-Type", "application/json");
            con.header("Tracking-Api-Key", key);
            con.requestBody("{\"tracking_number\":\""+trackingNumber+"\",\"courier_code\":\""+courierCode+"\"}");
            Response resp = con.execute();
            if (ValidateStatus(resp.statusCode())) {
                return true;
            }
        } catch (Exception e) {
            Logger.Error("Failed to create tracking!");
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(String trackingNumber, String courierCode) {
        if (!ValidateApiKey(key)) {return false;}

        try {
            Connection con = Jsoup.connect("https://api.trackingmore.com/v3/trackings/delete");
            con.ignoreContentType(true);
            con.method(Connection.Method.POST);
            con.header("Content-Type", "application/json");
            con.header("Tracking-Api-Key", key);
            con.requestBody("{\"tracking_number\":\""+trackingNumber+"\",\"courier_code\":\""+courierCode+"\"}");
            Response resp = con.execute();
            if (ValidateStatus(resp.statusCode())) {
                return true;
            }
        } catch (Exception e) {
            Logger.Error("Failed to create tracking!");
            e.printStackTrace();
        }
        return false;
    }

    public TrackingData getResult(List<String> trackingNumbers) {
        if (!ValidateApiKey(key)) {return null;}

        String TrackingCodes = "";
        if (trackingNumbers.size() > 2000) {
            Logger.Error("Too many tracking numbers in one request!");
            return null;
        }
        if (trackingNumbers.size() == 1) {
            TrackingCodes = trackingNumbers.get(0);
        }
        if (trackingNumbers.size() > 1) {
            TrackingCodes = String.join(",", trackingNumbers);
        }

        try {
            Connection con = Jsoup.connect("https://api.trackingmore.com/v3/trackings/get?tracking_numbers=" + TrackingCodes);
            con.ignoreContentType(true);
            con.method(Connection.Method.GET);
            con.header("Content-Type", "application/json");
            con.header("Tracking-Api-Key", key);
            Response resp = con.execute();
            if (ValidateStatus(resp.statusCode())) {
                return gson.fromJson(resp.body(), TrackingData.class);
            }
        } catch (Exception e) {
            Logger.Error("Failed to get trackingdata from api!");
            e.printStackTrace();
        }
        return null;
    }


    public enum DeliveryStatysType {
        normal, subStatus
    }

    public String translateStatus(DeliveryStatysType type, String status) {

        if (type.equals(DeliveryStatysType.normal)) {
            switch (status) {
                case "pendding":
                    return "New package added that are pending to track";
                case "notfound":
                    return "Package tracking information is no available yet";
                case "transit":
                    return "Courier has picked up package from shipper, the package is on the way to destination";
                case "pickup":
                    return "Also known as \"Out For Delivery\", courier is about to deliver the package, or the package is wating for addressee to pick up";
                case "delivered":
                    return "The package was delivered successfully";
                case "expired":
                    return "No tracking information for 30days for express service, or no tracking information for 60 days for postal service since the package added";
                case "undelivered":
                    return "Also known as \"Failed Attempt\", courier attempted to deliver but failded, usually left a notice and will try to delivery again";
                case "exception":
                    return "Package missed, addressee returned package to sender or other exceptions";
            }
        } else if (type.equals(DeliveryStatysType.subStatus)) {
            switch(status) {
                case "notfound001":
                    return "The package is waiting for courier to pick up";
                case "notfound002":
                    return "No tracking information found";
                case "transit001":
                    return "Package is on the way to destination";
                case "transit002":
                    return "Package arrived at a hub or sorting center";
                case "transit003":
                    return "Package arrived at delivery facility";
                case "transit004":
                    return "Package arrived at destination country";
                case "transit005":
                    return "Customs clearance completed";
                case "delivered001":
                    return "Package delivered successfully";
                case "delivered002":
                    return "Package picked up by the addressee";
                case "delivered003":
                    return "Package received and signed by addressee";
                case "delivered004":
                    return "Package was left at the front door or left with your neighbour";
                case "exception004":
                    return "The package is unclaimed";
                case "exception005":
                    return "Other exceptions";
                case "exception006":
                    return "Package was detained by customs";
                case "exception007":
                    return "Package was lost or damaged during delivery";
                case "exception008":
                    return "Logistics order was cancelled before courier pick up the package";
                case "exception009":
                    return "Package was refused by addressee";
                case "exception010":
                    return "Package has been returned to sender";
                case "exception011":
                    return "Package is beening sent to sender";
            }
        }
        return null;
    }









    //Validation methods

    private boolean ValidateApiKey(String key) {
        if (key != null && key.length() > 5) {
            return true;
        }
        Logger.Error("Invalid Tracking apikey!");
        return false;
    }

    private boolean ValidateStatus(Integer status) {
        switch (status) {
            case 200:
                return true;
            case 203:
                Logger.Error("[TrackingApi] PaymentRequired :: API service is only available for paid account");
                return false;
            case 204:
                Logger.Error("[TrackingApi] NoContent :: Request was successful, but no data returned");
                return false;
            case 400:
                Logger.Error("[TrackingApi] BadRequest :: Request type error");
                return false;
            case 401:
                Logger.Error("[TrackingApi] Unauthorized :: Authentication failed or has no permission");
                return false;
            case 403:
                Logger.Error("[TrackingApi] BadRequest :: Page does not exist");
                return false;
            case 404:
                Logger.Error("[TrackingApi] NotFound :: Page does not exist");
                return false;
            case 408:
                Logger.Error("[TrackingApi] TimeOut :: Request timeout");
                return false;
            case 411:
                Logger.Error("[TrackingApi] BadRequest :: Specified request parameter length exceeds length limit");
                return false;
            case 412:
                Logger.Error("[TrackingApi] BadRequest :: Specified request parameter format doesn't meet requirements");
                return false;
            case 413:
                Logger.Error("[TrackingApi] OutLimited :: The number of request parameters exceeds the limit");
                return false;
            case 417:
                Logger.Error("[TrackingApi] BadRequest :: Missing request parameters or request parameters cannot be parsed");
                return false;
            case 421:
                Logger.Error("[TrackingApi] BadRequest :: Some of required parameters are empty");
                return false;
            case 422:
                Logger.Error("[TrackingApi] BadRequest :: Unidentifiable courier code");
                return false;
            case 423:
                Logger.Error("[TrackingApi] BadRequest :: Tracking No. already exists");
                return false;
            case 424:
                Logger.Error("[TrackingApi] BadRequest :: Tracking No. no exists");
                return false;
            case 429:
                Logger.Error("[TrackingApi] BadRequest :: Exceeded API request limits, please try again later");
                return false;
            case 511:
            case 512:
            case 513:
                Logger.Error("[TrackingApi] ServerError :: Server error");
                return false;
            default:
                Logger.Error("[TrackingApi] UnknownError :: Received unknown error code " + status);
                return false;
        }
    }




}
