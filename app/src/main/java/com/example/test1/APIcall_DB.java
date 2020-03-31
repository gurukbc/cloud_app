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

public class APIcall_DB extends APIcall_main {

    /**
     * @brief DB 인스턴스의 zone과 인스턴스 id로 Displayname을 받아오는 함수
     * @param zonename displayname을 알기를 원하는 DB 인스턴스의 zone 이름
     * @param id displayname을 알기를 원하는 DB 인스턴스의 id
     * @return zonename과 id이 일치하는 DB 인스턴스의 displayname, 찾지못했을 경우 "알수없음" 반환
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws IOException
     * @throws ParseException
     **/
    private static String getDisplaynameById(String zonename, String id) throws InvalidKeyException, NoSuchAlgorithmException, ParseException, IOException {
        String displayname = "알수없음";

        int button = 20;


        TreeMap<String, String> request = new TreeMap<String, String>();

        if(zonename.equals("kr-1")) setZone("Seoul-M2");

        request = generateRequire(button, request);

        request.put("response", "json");
        request.put("apiKey", getApikey());
        request.put("instanceids", id);

        String req_message = generateReq(request);

		/*
		System.out.println("Request Message is...");
		System.out.println(req_message);
		 */

        JSONObject obj =  readJsonFromUrl(req_message);

        JSONObject parse_listinstancesresponse = (JSONObject) obj.get("listinstancesresponse");

        // 조회된 DB 인스턴스가 존재할 경우에만 검색
        if(parse_listinstancesresponse.size() > 0 ) {

            JSONObject parse_listinstancelist= (JSONObject) parse_listinstancesresponse.get("instancelist");

            JSONArray parse_instance = (JSONArray) parse_listinstancelist.get("instance");

            JSONObject instance =  (JSONObject) parse_instance.get(0);

            displayname = String.valueOf(instance.get("instancename"));


        }


        return displayname;

    }
    /**
     * @throws ParseException
     * @return Database의 이름, 상태, DB상태(보류), 용량, 생성일, 종속장치, 위치를 가지는 arraylist
     * @brief Database 기능에 해당하는, 생성된 mysql DB 정보 출력을 위한 함수
     **/
    public static ArrayList<String[]> listMysqlDB() throws IOException, InvalidKeyException, NoSuchAlgorithmException, ParseException {

        int button = 20;

        TreeMap<String, String> request = new TreeMap<String, String>();
        request = generateRequire(button, request);

        request.put("response", "json");
        request.put("apiKey", getApikey());

        String req_message = generateReq(request);

//        System.out.println("Request Message is...");
//        System.out.println(req_message);

        JSONObject obj =  readJsonFromUrl(req_message);

        JSONObject parse_listinstancesresponse = (JSONObject) obj.get("listinstancesresponse");

        JSONObject parse_listinstancelist= (JSONObject) parse_listinstancesresponse.get("instancelist");

        JSONArray parse_instance = (JSONArray) parse_listinstancelist.get("instance");

        JSONObject instance;
        ArrayList<String[]> list = new ArrayList<String[]>();

        for(int i = 0 ; i < parse_instance.size(); i++) {
            instance = (JSONObject) parse_instance.get(i);

            String dbstatus =null;
            if(((String)instance.get("instancestatus")).equals("Running"))dbstatus="정상";
            else dbstatus = "알수없음";

            //이름, 상태, DB상태(보류), 용량, 생성일, 종속장치, 위치
            list.add(new String[]{(String) instance.get("instancename"),(String)instance.get("instancestatus"),
                    dbstatus, (String)instance.get("storagesize") + " GB", (String)instance.get("instancecreationtime")
                    , (String) instance.get("instancetype"), (String)instance.get("zone")});
        }

        return list;
    }
    /**
     * @throws ParseException
     * @brief Database 기능에 해당하는, DB의 HA 그룹 정보 출력을 위한 함수
     **/
    public static void listHaGroups() throws IOException, InvalidKeyException, NoSuchAlgorithmException, ParseException {

        int button = 21;


        TreeMap<String, String> request = new TreeMap<String, String>();

        request = generateRequire(button, request);

        request.put("response", "json");
        request.put("apiKey", getApikey());


        String req_message = generateReq(request);

        System.out.println("Request Message is...");
        System.out.println(req_message);

        JSONObject obj =  readJsonFromUrl(req_message);

        JSONObject parse_listhagroupsresponse = (JSONObject) obj.get("listhagroupsresponse");

        JSONObject parse_hagrouplist= (JSONObject) parse_listhagroupsresponse.get("hagrouplist");

        JSONArray parse_hagroup = (JSONArray) parse_hagrouplist.get("hagroup");

        JSONObject hagroup;
        JSONObject master;

        for(int i = 0 ; i < parse_hagroup.size(); i++) {
            hagroup = (JSONObject) parse_hagroup.get(i);

            System.out.println("HA 그룹명: "+ hagroup.get("hagroupname"));
            System.out.println("HA 그룹 id: "+ hagroup.get("hagroupid"));
            System.out.println("slave 갯수: "+ hagroup.get("slavecount"));

            //위치, master이름/상태, slave이름/상태
            master = (JSONObject) hagroup.get("master");

            String HAzone = String.valueOf(master.get("zone"));
            System.out.println("위치: "+ HAzone);

            String mastername = getDisplaynameById(HAzone, String.valueOf(master.get("instanceid")));

            System.out.println("마스터명: "+ mastername);
            System.out.println("마스터 상태: "+ master.get("instancestatus"));

            JSONObject parse_slavelist = (JSONObject) hagroup.get("slavelist");
            JSONArray parse_slave = (JSONArray) parse_slavelist.get("slave");
            JSONObject slave;

            for(int j = 0 ; j < parse_slavelist.size(); j++) {
                slave = (JSONObject) parse_slave.get(j);

                String slavename =  getDisplaynameById(HAzone, String.valueOf(slave.get("instanceid")));

                System.out.println((j+1) + "번 째 슬레이브명: "+ slavename);
                System.out.println((j+1) + "번 째 슬레이브 상태: "+ slave.get("instancestatus"));

            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * @throws ParseException
     * @brief Database 기능에 해당하는, 복제 그룹의 슬레이브의 수를 변경하기 위한 함수
     **/

    public static void updateHaGroupSlaveCount() throws IOException, InvalidKeyException, NoSuchAlgorithmException, ParseException {

        int button = 22;

        Scanner sc = new Scanner(System.in);

        System.out.print("슬레이브 수를 변경할 HA 그룹 id 입력: ");
        String hagroupid = sc.next();

        System.out.print("슬레이브 수 입력(1~2): ");
        String slavecount = sc.next();


        TreeMap<String, String> request = new TreeMap<String, String>();
        request = generateRequire(button, request);

        request.put("response", "json");
        request.put("apiKey", getApikey());
        request.put("hagroupid", hagroupid);
        request.put("slavecount", slavecount);

        String req_message = generateReq(request);

        System.out.println("Request Message is...");
        System.out.println(req_message);

        JSONObject obj =  readJsonFromUrl(req_message);

        JSONObject parse_listinstancesresponse = (JSONObject) obj.get("listinstancesresponse");

        JSONObject parse_listinstancelist= (JSONObject) parse_listinstancesresponse.get("instancelist");

        JSONArray parse_instance = (JSONArray) parse_listinstancelist.get("instance");

        JSONObject instance;

        for(int i = 0 ; i < parse_instance.size(); i++) {
            instance = (JSONObject) parse_instance.get(i);


            System.out.println();
        }

    }
}
