package com.amelierosseneu.voicenotebook;

import java.util.HashMap;
import java.util.Map;

public class MemoClassType {

    public String memo;
    public String time;
    public String user;


    /* Needed to read data when fetched from firebase */
    public MemoClassType(){
    }

    /* Might be needed to upload data */
    public MemoClassType(String memo, String timestamp, String user){
        this.memo = memo;
        this.time = timestamp;
        this.user = user;
    }

    /* We might need the map if we use the Android app to upload data to the firebase */
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("memo", memo);
        result.put("time",time);
        result.put("user", user);
        return result;
    }


    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getTimestamp() {
        return time;
    }

    public void setTimestamp(String timestamp) {
        this.time = timestamp;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

}
