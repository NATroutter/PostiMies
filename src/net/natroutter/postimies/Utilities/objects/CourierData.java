package net.natroutter.postimies.Utilities.objects;

import java.util.ArrayList;

public class CourierData {
    public Integer code;
    public String message;
    public ArrayList<courier> data;

    public class courier {
        public String courier_name;
        public String courier_code;
    }

}
