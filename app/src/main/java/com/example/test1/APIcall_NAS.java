package com.example.test1;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;

public class APIcall_NAS extends APIcall_main {
    /**
     * @return 각 NAS의 이름, 위치, 신청용량, 현재 사용량, 프로토콜, id를 담은 ArrayList
     * @throws ParseException
     * @brief NAS 기능에 해당하는, 생성된 NAS 출력을 위한 함수
     **/
    public static ArrayList<String[]> listNas() throws IOException, InvalidKeyException, NoSuchAlgorithmException, ParseException {

        int button = 18;

        TreeMap<String, String> request = new TreeMap<String, String>();
        request = generateRequire(button, request);

        request.put("response", "json");
        request.put("apiKey", getApikey());


        String req_message = generateReq(request);

        System.out.println("Request Message is...");
        System.out.println(req_message);

        JSONObject obj = readJsonFromUrl(req_message);

        JSONObject parse_listvolumesresponsee = (JSONObject) obj.get("listvolumesresponse");

        JSONArray parse_response = (JSONArray) parse_listvolumesresponsee.get("response");

        JSONObject response;

        ArrayList<String[]> list = new ArrayList<String[]>();

        for (int i = 0; i < parse_response.size(); i++) {
            response = (JSONObject) parse_response.get(i);

            String zone = String.valueOf(response.get("zoneid"));
            if (zone.equals(Seoul_M_zoneid)) zone = "KOR-Seoul M";
            else if (zone.equals(Seoul_M2_zoneid)) zone = "KOR-Seoul M2";
            else if (zone.equals(CentralB_zoneid)) zone = "KOR-Central B";
            else if (zone.equals(CentralA_zoneid)) zone = "KOR-Central A";
            else if (zone.equals(HA_zoneid)) zone = "KOR-HA";
            else if (zone.equals(West_zoneid)) zone = "US-West";

            int totalsize = ((int) (((long) response.get("totalsize")) / Math.pow(10, 11))) * 100;
            int usedsize = (int) ((((long) response.get("usedsize")) / Math.pow(10, 9)));

            String id = "";
            if(!zone.equals("KOR-Seoul M2"))id = String.valueOf(response.get("id"));
            else id=(String)response.get("id");

            //이름, 위치, 신청용량, 현재 사용량, 프로토콜
            list.add(new String[]{(String) response.get("name"), zone,
                    String.valueOf(totalsize), String.valueOf(usedsize), (String) response.get("volumetype")
                    , id});
        }
        return list;
    }

    /**
     * @param id   사이즈 변경을 원하는 NAS의 id
     * @param size 변경하기를 원하는 사이즈
     * @throws ParseException
     * @brief NAS 기능에 해당하는, NAS volume 사이즈 변경을 위한 함수
     **/
    public static String updateVolume(String id, String size) throws IOException, InvalidKeyException, NoSuchAlgorithmException, ParseException {

        int button = 19;
        String s = null;

        TreeMap<String, String> request = new TreeMap<String, String>();
        request = generateRequire(button, request);

        System.out.println(id + "   " + size);

        request.put("response", "json");
        request.put("apiKey", getApikey());
        request.put("id", id);

        request.put("totalsize", size);

        String req_message = generateReq(request);

        System.out.println("Request Message is...");
        System.out.println(req_message);

        JSONObject obj = readJsonFromUrl(req_message);

        JSONObject parse_updatevolumeresponse = (JSONObject) obj.get("updatevolumeresponse");

        JSONObject parse_response2 = (JSONObject) parse_updatevolumeresponse.get("response");

        if (parse_response2.size() < 1) s = "사이즈 변경 실패";
        else s = "사이즈 변경 성공";

        return s;
    }
}
