package com.example.test1;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.TreeMap;

public class APIcall_Disk extends APIcall_main {
    /**
     * @return Disk 이름, 용량, 구분, 위치 등 정보들을 담고 있는 arraylist 출력
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws IOException
     * @throws ParseException
     * @brief Server 기능에 해당하는, 사용자가 보유하고있는 Disk 리스트 출력을 위한 함수
     **/
    public static ArrayList<String[]> listDisk() throws InvalidKeyException, NoSuchAlgorithmException, ParseException, IOException {
        int button = 15;

        TreeMap<String, String> request = new TreeMap<String, String>();
        request = generateRequire(button, request);

        request.put("response", "json");
        request.put("apiKey", getApikey());

        String req_message = generateReq(request);

        JSONObject obj = readJsonFromUrl(req_message);

        JSONObject parse_listvolumesresponse = (JSONObject) obj.get("listvolumesresponse");

        JSONArray parse_volume = (JSONArray) parse_listvolumesresponse.get("volume");

        JSONObject volume;

        ArrayList<String[]> list = new ArrayList<String[]>();

        for (int i = 0; i < parse_volume.size(); i++) {
            volume = (JSONObject) parse_volume.get(i);

            String s = String.valueOf((int)((long) volume.get("size") / Math.pow(10,10)) * 10);

            String diskofferingname = String.valueOf(volume.get("diskofferingname"));

            if(diskofferingname.equals("SSD_C_100G")) diskofferingname = "SSD-Provisioned";
            else  diskofferingname = "일반";

            //disk명, 용량, 구분, 위치, 상태, 적용 서버, 타입, 생성일자
            list.add(new String[]{(String) volume.get("name"),
                    s+ ".00 GB", diskofferingname, (String) volume.get("zonename"),
                    (String) volume.get("state"), (String)volume.get("vmname"), (String)volume.get("type"), (String)volume.get("created")});

        }
        return list;
    }
}
