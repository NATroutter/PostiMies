package net.natroutter.postimies.Utilities.objects;

import java.util.ArrayList;

public class TrackingData {
    public Integer code;
    public String message;
    public ArrayList<Package> data;

    public class Package {
        public String tracking_number;
        public String courier_code;
        public String delivery_status;
        public String archived;
        public String created_at;
        public String update_date;
        public String destination;
        public String weight;
        public String substatus;
        public String lasest_event;
        public String lastest_checkpoint_time;
        public originInfo origin_info;

        public class originInfo {
            public String arrived_destination_date;
            public ArrayList<trackInfo> trackinfo;

            public class trackInfo {
                public String checkpoint_date;
                public String tracking_detail;
                public String checkpoint_delivery_status;
                public String checkpoint_delivery_substatus;
            }
        }


    }
}
