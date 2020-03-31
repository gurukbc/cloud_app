package com.example.test1;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;

public class APIcall_server extends APIcall_main {

    /**
     * @return
     * @throws ParseException
     * @brief Server 기능에 해당하는, 생성된 VM 정보 출력을 위한 함수
     * @return 각 서버의 서버명, 스펙, 상태, 생성일시, 운영체제를 가지고 있는 list
     */
    public static ArrayList<String[]> listServers() throws IOException, InvalidKeyException, NoSuchAlgorithmException, ParseException {

        int button = 2;

        TreeMap<String, String> request = new TreeMap<String, String>();
        request = generateRequire(button, request);

        request.put("response", "json");
        request.put("apiKey", getApikey());

        String req_message = generateReq(request);

        JSONObject obj = readJsonFromUrl(req_message);

        JSONObject parse_listvirtualmachinesresponse = (JSONObject) obj.get("listvirtualmachinesresponse");

        JSONArray parse_virtualmachine = (JSONArray) parse_listvirtualmachinesresponse.get("virtualmachine");

        JSONObject virtualmachine;

        ArrayList<String[]> list = new ArrayList<String[]>();

        for (int i = 0; i < parse_virtualmachine.size(); i++) {
            virtualmachine = (JSONObject) parse_virtualmachine.get(i);

            String s = virtualmachine.get("cpunumber").toString() + " vCore";

            //서버명, 스펙, 상태, 생성일시, 운영체제
            list.add(new String[]{(String) virtualmachine.get("displayname"),
                    s, (String) virtualmachine.get("state"), (String) virtualmachine.get("created"),
                    (String) virtualmachine.get("templatename")});
        }

        return list;
    }

    /**
     * @throws ParseException
     * @brief Server 기능에 해당하는, 사용중인 VM을 정지시키기 위한 함수
     * @return 현재 정지시킨 서버의 상태
     * @param num 정지시키고자 하는 서버의 view내 상대적 위치
     **/
    public static String stopServer(int num) throws IOException, InvalidKeyException, NoSuchAlgorithmException, ParseException {

        int button = 3;

        List<Integer> vm = new ArrayList<Integer>();
        vm.add(num);


        TreeMap<String, String> request = new TreeMap<String, String>();

        // 먼저 서버 조회해서 입력된 순서에 해당하는 vm id값을 받아옴
        request = generateRequire(2, request);

        request.put("response", "json");
        request.put("apiKey", getApikey());

        String req_message = generateReq(request);
        System.out.println();

        JSONObject obj = readJsonFromUrl(req_message);
        JSONObject parse_listvirtualmachinesresponse = (JSONObject) obj.get("listvirtualmachinesresponse");
        JSONArray parse_virtualmachine = (JSONArray) parse_listvirtualmachinesresponse.get("virtualmachine");

        JSONObject virtualmachine = null;


        // stopVirtualMachine api url 생성 시작
        request.remove("command");
        request = generateRequire(button, request);

        // 각 id에 따라 stopVirtualMachine request url 생성 및 호출
        for (int i = 0; i < vm.size(); i++) {

            virtualmachine = (JSONObject) parse_virtualmachine.get(vm.get(i));

            request.put("id", (String) virtualmachine.get("id"));

//            System.out.println("###########################");
//            System.out.println("정지할 vm의 id: " + virtualmachine.get("id"));
//            System.out.println();

            req_message = generateReq(request);
//
//            System.out.println("Request Message is...");
//            System.out.println(req_message);
//            System.out.println();
//
            obj = readJsonFromUrl(req_message);

            //JSONObject parse_stopvirtualmachineresponse = (JSONObject) obj.get("stopvirtualmachineresponse");

            //System.out.println( "job id:" + parse_stopvirtualmachineresponse.get("jobid"));
//
//            System.out.println("SUCCESS to STOP " + virtualmachine.get("displayname"));
            request.remove("id");
        }

        return (String) virtualmachine.get("state");
    }

    /**
     * @throws ParseException
     * @brief Server 기능에 해당하는, 정지 상태인 VM을 시작하기 위한 함수
     * @param num 정지시키고자 하는 서버의 view내 상대적 위치
     **/
    public static void startServer(int num) throws IOException, InvalidKeyException, NoSuchAlgorithmException, ParseException {

        int button = 4;

        List<Integer> vm = new ArrayList<Integer>();

        vm.add(num);
        TreeMap<String, String> request = new TreeMap<String, String>();

        // 먼저 서버 조회해서 입력된 순서에 해당하는 vm id값을 받아옴
        request = generateRequire(2, request);

        request.put("response", "json");
        request.put("apiKey", getApikey());

        String req_message = generateReq(request);
        System.out.println();

        JSONObject obj = readJsonFromUrl(req_message);
        JSONObject parse_listvirtualmachinesresponse = (JSONObject) obj.get("listvirtualmachinesresponse");
        JSONArray parse_virtualmachine = (JSONArray) parse_listvirtualmachinesresponse.get("virtualmachine");

        JSONObject virtualmachine;


        // startVirtualMachine api url 생성 시작
        request.remove("command");
        request = generateRequire(button, request);

        // 각 id에 따라 startVirtualMachine request url 생성 및 호출
        for (int i = 0; i < vm.size(); i++) {

            virtualmachine = (JSONObject) parse_virtualmachine.get(vm.get(i));

            request.put("id", (String) virtualmachine.get("id"));
//
//            System.out.println("###########################");
//            System.out.println("시작할 vm의 id: " + virtualmachine.get("id"));
//            System.out.println();

            req_message = generateReq(request);
//
//            System.out.println("Request Message is...");
//            System.out.println(req_message);
//            System.out.println();

            readJsonFromUrl(req_message);
            System.out.println("SUCCESS to START " + virtualmachine.get("displayname"));
            request.remove("id");
        }
    }

    /**
     * @throws ParseException
     * @brief Server 기능에 해당하는, VM을 재시작하기 위한 함수
     * @param num 정지시키고자 하는 서버의 view내 상대적 위치
     **/
    public static void rebootServer(int num) throws IOException, InvalidKeyException, NoSuchAlgorithmException, ParseException {

        int button = 5;

        List<Integer> vm = new ArrayList<Integer>();
        vm.add(num);

        TreeMap<String, String> request = new TreeMap<String, String>();

        // 먼저 서버 조회해서 입력된 순서에 해당하는 vm id값을 받아옴
        request = generateRequire(2, request);

        request.put("response", "json");
        request.put("apiKey", getApikey());

        String req_message = generateReq(request);
        System.out.println();

        JSONObject obj = readJsonFromUrl(req_message);
        JSONObject parse_listvirtualmachinesresponse = (JSONObject) obj.get("listvirtualmachinesresponse");
        JSONArray parse_virtualmachine = (JSONArray) parse_listvirtualmachinesresponse.get("virtualmachine");

        JSONObject virtualmachine;


        // rebootVirtualMachine api url 생성 시작
        request.remove("command");
        request = generateRequire(button, request);

        // 각 id에 따라 rebootVirtualMachine request url 생성 및 호출
        for (int i = 0; i < vm.size(); i++) {

            virtualmachine = (JSONObject) parse_virtualmachine.get(vm.get(i));

            request.put("id", (String) virtualmachine.get("id"));

//            System.out.println("###########################");
//            System.out.println("재시작할 vm의 id: " + virtualmachine.get("id"));
//            System.out.println();

            req_message = generateReq(request);
//
//            System.out.println("Request Message is...");
//            System.out.println(req_message);
//            System.out.println();


            readJsonFromUrl(req_message);

            System.out.println("SUCCESS to REBOOT " + virtualmachine.get("displayname"));

            request.remove("id");
        }


    }
}

