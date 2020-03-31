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

public class APIcall_LB extends APIcall_main{
    /**
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws IOException
     * @throws ParseException
     * @brief 서버의 zoneid와 vm id로 Displayname을 받아오는 함수
     * @param zoneid displayname을 알기를 원하는 서버의 zoneid
     * @param id displayname을 알기를 원하는 서버의 id
     * @return zoneid와 id이 일치하는 서버의 displayname, 찾지못했을 경우 null 반환
     **/
    public static String getDisplaynameById(String zoneid, String id) throws InvalidKeyException, NoSuchAlgorithmException, ParseException, IOException {
        String displayName = "";
        int button = 2;

        TreeMap<String, String> request = new TreeMap<String, String>();
        request = generateRequire(button, request);

        request.put("response", "json");
        request.put("apiKey", getApikey());

        request.put("zoneid", zoneid);

        String req_message = generateReq(request);

        JSONObject obj =  readJsonFromUrl(req_message);

        JSONObject parse_listvirtualmachinesresponse = (JSONObject) obj.get("listvirtualmachinesresponse");

        // 조회된 서버가 하나 이상일 경우에만 출력, 메트릭 조회 가능
        if(parse_listvirtualmachinesresponse.size() > 0 ) {

            JSONArray parse_virtualmachine = (JSONArray) parse_listvirtualmachinesresponse.get("virtualmachine");

            JSONObject virtualmachine;

            for(int i = 0 ; i < parse_virtualmachine.size(); i++) {

                virtualmachine = (JSONObject) parse_virtualmachine.get(i);

                if(String.valueOf(virtualmachine.get("id")).equals(id)) {
                    displayName = String.valueOf(virtualmachine.get("displayname"));
                    return displayName;
                }
            }
        }
        return displayName;
    }

    /**
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws IOException
     * @throws ParseException
     * @brief Networking 기능에 해당하는, LB 리스트 출력을 위한 함수
     * @return 각 LoadBalancer의 LB이름, 옵션, 타입, 위치, IP, Port를 가진 리스트
     **/
    public static ArrayList<String[]> listLB() throws InvalidKeyException, NoSuchAlgorithmException, ParseException, IOException {
        int button = 16;

        TreeMap<String, String> request = new TreeMap<String, String>();
        request = generateRequire(button, request);

        request.put("response", "json");
        request.put("apiKey", getApikey());

        String req_message = generateReq(request);

        JSONObject obj =  readJsonFromUrl(req_message);

        JSONObject parse_listloadbalancersresponse = (JSONObject) obj.get("listloadbalancersresponse");

        JSONArray parse_loadbalancer = (JSONArray) parse_listloadbalancersresponse.get("loadbalancer");

        JSONObject loadbalancer;

        ArrayList<String[]> list = new ArrayList<String[]>();

        for(int i = 0 ; i < parse_loadbalancer.size(); i++) {
            loadbalancer = (JSONObject) parse_loadbalancer.get(i);

            //LB이름, 위치, 옵션, 타입, IP, Port, id
            list.add(new String[]{(String) loadbalancer.get("name"),
                    (String)loadbalancer.get("zonename"), (String)loadbalancer.get("loadbalanceroption"), (String) loadbalancer.get("servicetype"),
                    (String)loadbalancer.get("serviceip"), (String) loadbalancer.get("serviceport"),String.valueOf(loadbalancer.get("loadbalancerid"))});
        }
        return list;
    }

    /**
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws IOException
     * @throws ParseException
     * @param loadbalancerid 조회할 LB의 아이디
     * @param zone 조회할 LB가 위치한 zone의 이름
     * @brief 각 LB에 등록된 웹 서버의 정보 조희를 위한 함수
     **/
    public static ArrayList<String[]> listLBWebServers(String loadbalancerid, String zone) throws InvalidKeyException, NoSuchAlgorithmException, ParseException, IOException {
        int button = 17;
        String zoneid = Seoul_M_zoneid;

        if(zone.equals("KOR-Central A")) zoneid = CentralA_zoneid;
        else if (zone.equals("KOR-Central B")) zoneid = CentralB_zoneid;
        else if (zone.equals("KOR-Seoul M"))  zoneid = Seoul_M_zoneid;
        else if (zone.equals("KOR-Seoul M2")) zoneid = Seoul_M2_zoneid;
        else if (zone.equals("KOR-HA"))  zoneid = HA_zoneid;
        else if (zone.equals("US-West")) zoneid = West_zoneid;

        TreeMap<String, String> request = new TreeMap<String, String>();
        request = generateRequire(button, request);

        request.put("response", "json");
        request.put("apiKey", getApikey());
        request.put("loadbalancerid", loadbalancerid);

        String req_message = generateReq(request);

        JSONObject obj =  readJsonFromUrl(req_message);

        JSONObject parse_listLoadBalancerWebServersresponse = (JSONObject) obj.get("listloadbalancerwebserversresponse");

        JSONArray parse_loadbalancerwebserver = (JSONArray) parse_listLoadBalancerWebServersresponse.get("loadbalancerwebserver");

        JSONObject loadbalancerwebserver;
        ArrayList<String[]> list = new ArrayList<String[]>();

        for(int i = 0 ; i < parse_loadbalancerwebserver.size(); i++) {
            loadbalancerwebserver = (JSONObject) parse_loadbalancerwebserver.get(i);

            // response로 받은 virtualmachineid로부터 얻은 vm id와 zoneid를 사용하여 vm명 검색 및 출력
            String name = getDisplaynameById(zoneid, String.valueOf(loadbalancerwebserver.get("virtualmachineid")));

            //서버명, ip, port, throughput, server conn, ttfb, req, 상태
            list.add(new String[]{name,
                    (String)loadbalancerwebserver.get("ipaddress"), (String)loadbalancerwebserver.get("publicport"), (String) loadbalancerwebserver.get("throughputrate"),
                    (String)loadbalancerwebserver.get("cursrvrconnections"), (String) loadbalancerwebserver.get("avgsvrttfb"),
                    (String) loadbalancerwebserver.get("requestsrate"),(String)loadbalancerwebserver.get("state")});

        }
        return list;
    }
}
