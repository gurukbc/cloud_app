package com.example.test1;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;

public class APIcall_watch extends APIcall_main {
    private static String statistics = "Maximum";
    private static String starttime = "";
    private static String endtime = "";
    private static String namespace = "ucloud/server";
    private static String period = "60";
    private static String unit = "Bytes";
//    private static String statevalue = "ALL"; // 출력할 알람 상태 저장 변수

    private static HashMap<String, HashMap<String, String>> metricList;
    // metricList의 key에는 "CPUUtilization", "NetworkIn" 등의 Metric명이 들어가고, value에는  각 Metric별 points<timestamp, value>가 들어간다.

    // 알람 발생 시, 해당 알람에 대한 메트릭을 저장하기 위한 map
    // alarmMetricList의 key에는 발생한 알람명이 들어가고, value에는  알람 발생 전후의 points<timestamp, value>가 들어간다.
    private static HashMap<String, HashMap<String, String>> alarmMetricList;

    private static ArrayList<String> alarmList;

    // 알람 발생 시, 해당 알람에 대한 threshold을 저장하기 위한 map
    // key에는 발생한 알람명이 들어가고, value에는  threshold가 들어간다.
    private static HashMap<String, String> alarmThreshold;


    public static HashMap<String, String> getInfo(String metricname) {
        HashMap<String, String> tmp = new HashMap<String, String>();
        tmp = metricList.get(metricname);
        return tmp;
    }


    /**
     * @brief 알람 이름에 따른 매트릭을 가진 hashmap을 리턴, UX과정에서 사용
     * @param alarmname 조회를 원하는 알람이름
     * @return
     */
    public static HashMap<String, String> getAlarmMetricInfo(String alarmname) {
        HashMap<String, String> tmp = new HashMap<String, String>();
        tmp = alarmMetricList.get(alarmname);
//        System.out.println(alarmname);
//        System.out.println(tmp);
        System.out.println(alarmMetricList);
        return tmp;
    }

    /**
     * @brief 알람 이름에 따른 Threshold를 리턴
     * @param alarmname
     * @return
     */
    public static String getAlarmThresholdInfo(String alarmname) {
        String tmp;
        tmp = alarmThreshold.get(alarmname);
        return tmp;
    }

    /**
     * @brief 최근 알람3개의 이름을 가진 list 리턴
     * @return
     */
    public static ArrayList<String> getAlarmTitle() {
        return alarmList;
    }


    /**
     * @brief section 값을 구간으로, 현재 시간 및 통계 시작 시간을 설정하기 위한 함수
     **/
    private static void setTime(int section) {
        ZonedDateTime currDateTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        endtime = currDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
        starttime = currDateTime.minusHours(section).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
    }


    /**
     * @brief 각 상세 그래프에서, 통계, 구간, 주기에 대한 사용자 지정 값을 입력받기 위한 함수
     **/
    private static void setValue() {
        Scanner sc = new Scanner(System.in);

        System.out.print("통계(Average, Sum, SampleCount, Maximum, Minimum): ");
        statistics = sc.next();

        System.out.print("구간 설정(시간단위- 1, 3, 6, 12, 24, 168): ");
        int input = sc.nextInt();
        setTime(input);

        System.out.print("주기(분단위- 5, 15, 30, 60, 360, 720, 1440): ");
        period = sc.next();

    }

    /**
     * @brief CPU Utilization 및 네트워크 관련 값을 제외한 각 상세 그래프에서, 단위에 대한 사용자 지정 값을 입력받기 위한 함수
     **/
    public static void setUnit() {
        Scanner sc = new Scanner(System.in);

        System.out.print("단위(Bytes, Kilobytes, Megabytes, Gigabytes): ");
        unit = sc.next();
    }


    /**
     * @brief 네트워크 관련 상세 그래프에서, 단위에 대한 사용자 지정 값을 입력받기 위한 함수
     **/
    public static void setUnitForNetwork() {
        Scanner sc = new Scanner(System.in);

        System.out.print("단위(Bits/Second, Kilobits/Second, Megabits/Second, Gigabits/Second, Bytes/Second, Kilobytes/Second, Megabytes/Second, Gigabytes/Second): ");
        unit = sc.next();
    }


    /**
     * @param displayname 위치한 zoneid를 알기를 원하는 서버명
     * @return 서버명이 displayname인 서버의 zoneid, 찾지못했을 경우 "" 반환
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws IOException
     * @throws ParseException
     * @brief 서버의 displayname으로 zoneid를 받아오는 함수
     **/
    public static String getZoneByDisplayname(String displayname) throws InvalidKeyException, NoSuchAlgorithmException, ParseException, IOException {
        String zoneid = "";
        int button = 2;


        // 1. 서버가 Seoul-M2존이 아닐 경우 탐색
        setZone("");

        TreeMap<String, String> request = new TreeMap<String, String>();
        request = generateRequire(button, request);

        request.put("response", "json");
        request.put("apiKey", getApikey());


        String req_message = generateReq(request);

        JSONObject obj = readJsonFromUrl(req_message);

        JSONObject parse_listvirtualmachinesresponse = (JSONObject) obj.get("listvirtualmachinesresponse");

        // 조회된 서버가 하나 이상일 경우에만 출력, 메트릭 조회 가능
        if (parse_listvirtualmachinesresponse.size() > 0) {

            JSONArray parse_virtualmachine = (JSONArray) parse_listvirtualmachinesresponse.get("virtualmachine");

            JSONObject virtualmachine;

            for (int i = 0; i < parse_virtualmachine.size(); i++) {

                virtualmachine = (JSONObject) parse_virtualmachine.get(i);

                if (String.valueOf(virtualmachine.get("displayname")).equals(displayname)) {
                    zoneid = String.valueOf(virtualmachine.get("zoneid"));
//                    System.out.println("Request Message is...");
//                    System.out.println(req_message);
//                    System.out.println("zoneid= " + zoneid);
//                    System.out.println();
                    return zoneid;
                }

            }
        }


        // 2. 서버가 Seoul-M2존일 경우 탐색
        setZone("Seoul-M2");

        req_message = generateReq(request);

        obj = readJsonFromUrl(req_message);

        parse_listvirtualmachinesresponse = (JSONObject) obj.get("listvirtualmachinesresponse");

        // 조회된 서버가 하나 이상일 경우에만 출력, 메트릭 조회 가능
        if (parse_listvirtualmachinesresponse.size() > 0) {

            JSONArray parse_virtualmachine = (JSONArray) parse_listvirtualmachinesresponse.get("virtualmachine");

            JSONObject virtualmachine;

            for (int i = 0; i < parse_virtualmachine.size(); i++) {

                virtualmachine = (JSONObject) parse_virtualmachine.get(i);

                if (String.valueOf(virtualmachine.get("displayname")).equals(displayname)) {
                    zoneid = String.valueOf(virtualmachine.get("zone"));
                    return zoneid;
                }

            }
        }

        // 해당 displayname의 서버를 찾지 못할 경우 "" 반환

        return zoneid;
    }

    /**
     * @param zoneid      hostname을 알기를 원하는 서버의 zoneid
     * @param displayname hostname을 알기를 원하는 서버명
     * @return zoneid와 displayname이 일치하는 서버의 hostname, 찾지못했을 경우 "" 반환
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws IOException
     * @throws ParseException
     * @brief 서버의 zondid와 displayname으로 name(hostname)을 받아오는 함수
     **/
    public static String getNameByDisplayname(String zoneid, String displayname) throws InvalidKeyException, NoSuchAlgorithmException, ParseException, IOException {
        String name = "";
        int button = 2;


        TreeMap<String, String> request = new TreeMap<String, String>();
        request = generateRequire(button, request);

        request.put("response", "json");
        request.put("apiKey", getApikey());

        request.remove("zoneid");
        request.put("zoneid", zoneid);


        String req_message = generateReq(request);

        JSONObject obj = readJsonFromUrl(req_message);

        JSONObject parse_listvirtualmachinesresponse = (JSONObject) obj.get("listvirtualmachinesresponse");

        // 조회된 서버가 하나 이상일 경우에만 출력, 메트릭 조회 가능
        if (parse_listvirtualmachinesresponse.size() > 0) {

            JSONArray parse_virtualmachine = (JSONArray) parse_listvirtualmachinesresponse.get("virtualmachine");

            JSONObject virtualmachine;

            for (int i = 0; i < parse_virtualmachine.size(); i++) {

                virtualmachine = (JSONObject) parse_virtualmachine.get(i);

                if (String.valueOf(virtualmachine.get("displayname")).equals(displayname)) {
                    name = String.valueOf(virtualmachine.get("name"));
//                    System.out.println("(For Name)Request Message is...");
//                    System.out.println(req_message);
//                    System.out.println("name= " + name);
//                    System.out.println();
                    return name;
                }

            }
        }

        return name;
    }


    /**
     * @param zoneid      id을 알기를 원하는 서버의 zoneid
     * @param displayname id을 알기를 원하는 서버명
     * @return zoneid와 displayname이 일치하는 서버의 id, 찾지못했을 경우 "" 반환
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws IOException
     * @throws ParseException
     * @brief 서버의 zoneid와 displayname으로 id을 받아오는 함수
     **/
    public static String getIdByDisplayname(String zoneid, String displayname) throws InvalidKeyException, NoSuchAlgorithmException, ParseException, IOException {
        String id = "";
        int button = 2;

        TreeMap<String, String> request = new TreeMap<String, String>();
        request = generateRequire(button, request);

        request.put("response", "json");
        request.put("apiKey", getApikey());

        request.remove("zoneid");
        request.put("zoneid", zoneid);


        String req_message = generateReq(request);

        JSONObject obj = readJsonFromUrl(req_message);

        JSONObject parse_listvirtualmachinesresponse = (JSONObject) obj.get("listvirtualmachinesresponse");

        // 조회된 서버가 하나 이상일 경우에만 출력, 메트릭 조회 가능
        if (parse_listvirtualmachinesresponse.size() > 0) {

            JSONArray parse_virtualmachine = (JSONArray) parse_listvirtualmachinesresponse.get("virtualmachine");

            JSONObject virtualmachine;

            for (int i = 0; i < parse_virtualmachine.size(); i++) {

                virtualmachine = (JSONObject) parse_virtualmachine.get(i);

                if (String.valueOf(virtualmachine.get("displayname")).equals(displayname)) {
                    id = String.valueOf(virtualmachine.get("id"));

//                    System.out.println("(For ID)Request Message is...");
//                    System.out.println(req_message);
//                    System.out.println("id= " + id);
//                    System.out.println();
                    return id;
                }

            }
        }

        return id;

    }

    /**
     * @param z 사용중인 모든 서버 리스트 출력을 원하는 zone
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws IOException
     * @throws ParseException
     * @brief 특정 서버 메트릭 조회 시 필요한, 각 zone별 사용중인 서버 리스트 출력 함수
     **/
    private static void listServerAt(String z) throws InvalidKeyException, NoSuchAlgorithmException, ParseException, IOException {

        int button = 2;


        setState("running");

        setZone(z);

        TreeMap<String, String> request = new TreeMap<String, String>();
        request = generateRequire(button, request);

        request.put("response", "json");
        request.put("apiKey", getApikey());


        String req_message = generateReq(request);

        System.out.println("Request Message is...");
        System.out.println(req_message);

        JSONObject obj = readJsonFromUrl(req_message);

        JSONObject parse_listvirtualmachinesresponse = (JSONObject) obj.get("listvirtualmachinesresponse");

        // 조회된 서버가 하나 이상일 경우에만 출력, 메트릭 조회 가능
        if (parse_listvirtualmachinesresponse.size() > 0) {

            JSONArray parse_virtualmachine = (JSONArray) parse_listvirtualmachinesresponse.get("virtualmachine");

            JSONObject virtualmachine;

            for (int i = 0; i < parse_virtualmachine.size(); i++) {
                virtualmachine = (JSONObject) parse_virtualmachine.get(i);

                System.out.println("서버명 : " + virtualmachine.get("displayname"));
                System.out.println("서버 id : " + virtualmachine.get("id"));
                System.out.println("zone id : " + virtualmachine.get("zoneid"));
                System.out.println();
            }
        }

    }


    /**
     * @throws IOException
     * @throws ParseException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @brief 특정 서버 메트릭 조회 시 선택 가능한, 모든 위치의 서버 리스트 출력 함수
     **/
    public static void listAllServer() throws InvalidKeyException, NoSuchAlgorithmException, ParseException, IOException {

        listServerAt("Central-A");
        listServerAt("Central-B");
        listServerAt("Seoul-M");
        listServerAt("Seoul-M2");
        listServerAt("HA");
        listServerAt("US-West");
    }


    /**
     * @throws ParseException
     * @brief Watch 기능에 해당하는, 조회가능한 metricname 출력을 위한 함수
     **/
    public static void listMetrics() throws IOException, InvalidKeyException, NoSuchAlgorithmException, ParseException {

        int button = 1;

        TreeMap<String, String> request = new TreeMap<String, String>();
        request = generateRequire(button, request);

        request.put("response", "json");
        request.put("apiKey", getApikey());

        String req_message = generateReq(request);

        System.out.println("Request Message is...");
        System.out.println(req_message);

        JSONObject obj = readJsonFromUrl(req_message);

        JSONObject parse_listmetricsresponse = (JSONObject) obj.get("listmetricsresponse");

        JSONArray parse_metric = (JSONArray) parse_listmetricsresponse.get("metric");
        JSONObject metric;

        for (int i = 0; i < parse_metric.size(); i++) {
            metric = (JSONObject) parse_metric.get(i);

            System.out.println("metricname : " + metric.get("metricname"));

        }

    }

    /**
     * @param metricname 전체서버에 대해 조회하길 원하는 metricname
     * @throws ParseException
     * @brief Watch 기능에 해당하는, 전체 서버에 대한 지정 메트릭 통계를 위한 함수
     **/
    public static void showMetric(String metricname) throws IOException, InvalidKeyException, NoSuchAlgorithmException, ParseException {

        int button = 6;

//        setValue();
        setTime(6);

        metricList = new HashMap<String, HashMap<String, String>>();
        HashMap<String, String> points = new HashMap<String, String>();

        TreeMap<String, String> request = new TreeMap<String, String>();

        request = generateRequire(button, request);

        request.put("response", "json");
        request.put("apiKey", getApikey());
        request.put("metricname", metricname);
        request.put("namespace", namespace);

        request.put("statistics.member.1", statistics);
        request.put("endtime", endtime);
        request.put("starttime", starttime);
        request.put("period", period);
        request.put("unit", unit);

        if (metricname.equals("CPUUtilization")) request.put("unit", "Percent");
        else request.put("unit", "Bytes");

        String req_message = generateReq(request);

        System.out.println("Request Message is...");
        System.out.println(req_message);
        System.out.println();

        JSONObject obj = readJsonFromUrl(req_message);

        JSONObject parse_getmetricstatisticsresponse = (JSONObject) obj.get("getmetricstatisticsresponse");

        JSONArray parse_metricstatistics = (JSONArray) parse_getmetricstatisticsresponse.get("metricstatistics");
        JSONObject metric;

        System.out.println("총 point 수: " + parse_getmetricstatisticsresponse.get("count"));

        for (int i = 0; i < parse_metricstatistics.size(); i++) {

            metric = (JSONObject) parse_metricstatistics.get(i);


            points.put(String.valueOf(metric.get("timestamp")).substring(5, 16).replaceAll("T", " "), String.valueOf(metric.get(statistics.toLowerCase())));

        }

        metricList.put(metricname, points);

    }

    /**
     * @param metricname  조회하길 원하는 metricname
     * @param displayname 조회하길 원하는 특정 서버의 displayname
     * @throws ParseException
     * @brief 특정 서버의 지정 메트릭 통계를 위한 함수
     **/
    public static void showMetric(String metricname, String displayname) throws IOException, InvalidKeyException, NoSuchAlgorithmException, ParseException {

        int button = 6;


        // 먼저 displayname에 해당하는 name과 value값을 받아옴

        setValue();

        TreeMap<String, String> request = new TreeMap<String, String>();
        String zoneid = getZoneByDisplayname(displayname);
        String value = displayname + "(" + getIdByDisplayname(zoneid, displayname) + ")";

        request.clear();

        // displayname에 해당하는 getMetricStatistics api url 생성 시작

        metricList = new HashMap<String, HashMap<String, String>>();
        HashMap<String, String> points = new HashMap<String, String>();


        request = generateRequire(button, request);

        request.put("response", "json");
        request.put("apiKey", getApikey());
        request.put("metricname", metricname);
        request.put("namespace", namespace);

        request.put("statistics.member.1", statistics);
        request.put("endtime", endtime);
        request.put("starttime", starttime);
        request.put("period", period);
        request.put("unit", "Percent");
        if (metricname.equals("CPUUtilization")) request.put("unit", "Percent");
        else request.put("unit", "Bytes");

        request.put("dimensions.member.1.name", "name");
        request.put("dimensions.member.1.value", value);


        String req_message = generateReq(request);

        System.out.println("Request Message is...");
        System.out.println(req_message);
        System.out.println();

        JSONObject obj = readJsonFromUrl(req_message);

        JSONObject parse_getmetricstatisticsresponse = (JSONObject) obj.get("getmetricstatisticsresponse");

        JSONArray parse_metricstatistics = (JSONArray) parse_getmetricstatisticsresponse.get("metricstatistics");
        JSONObject metric;

        System.out.println("총 point 수: " + parse_getmetricstatisticsresponse.get("count"));

        for (int i = 0; i < parse_metricstatistics.size(); i++) {
            metric = (JSONObject) parse_metricstatistics.get(i);

            points.put(String.valueOf(metric.get("timestamp")).substring(5, 16).replaceAll("T", " "), String.valueOf(metric.get(statistics.toLowerCase())));
        }

        metricList.put(metricname, points);

    }

    /**
     * @throws IOException
     * @throws ParseException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @brief 개별 서버 메트릭 조회 시, 선택된 서버별 메트릭 조회를 위한 함수
     **/
    public static void showSpecificServerMetric() throws InvalidKeyException, NoSuchAlgorithmException, ParseException, IOException {

        Scanner sc = new Scanner(System.in);

        System.out.print("메트릭 조회할 vm 이름 입력: ");

        String displayname = sc.next();

        while (displayname != "9999") { // 9999 입력 시 종료


            System.out.print(displayname + " 조회할 메트릭(CPUUtilization, NetworkIn, NetworkOut, DiskReadBytes, DiskWriteBytes, MemoryInternalFree, MemoryTarget): ");
            String metric = sc.next();

            switch (metric) {
                case "CPUUtilization":
                    showMetric("CPUUtilization", displayname);
                    break;
                case "NetworkIn":
                    showMetric("NetworkIn", displayname);
                    break;
                case "NetworkOut":
                    showMetric("NetworkOut", displayname);
                    break;
                case "DiskReadBytes":
                    showMetric("DiskReadBytes", displayname);
                    break;
                case "DiskWriteBytes":
                    showMetric("DiskWriteBytes", displayname);
                    break;
                case "MemoryInternalFree":
                    showMetric("MemoryInternalFree", displayname);
                    break;
                case "MemoryTarget":
                    showMetric("MemoryTarget", displayname);
                    break;
            }

            displayname = sc.next();
        }

    }

    /**
     * @throws ParseException
     * @brief Watch 기능에 해당하는, 6가지 항목에 대한 통계 요약을 위한 함수
     **/
    public static void showMonitoring() throws IOException, InvalidKeyException, NoSuchAlgorithmException, ParseException {

        setTime(3);

        showMetric("CPUUtilization");
        showMetric("NetworkIn");
        showMetric("NetworkOut");
        showMetric("DiskReadBytes");
        showMetric("DiskWriteBytes");
        showMetric("MemoryInternalFree");

    }

    /**
     * @param statevalue 사용자가 보기 원하는 알람의 상태 - ALL, INSUFFICIENT_DATA, OK, ALARM
     * @return 알람의 이름, 상태, 알람 발생 조건, 액션 수행 여부, 수행 액션, 구분을 담은 arraylist
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws IOException
     * @throws ParseException
     * @brief 메트릭에 대해 사용자 정의 알람 리스트 출력을 위한 함수
     **/
    public static ArrayList<String[]> listAlarms(String statevalue) throws InvalidKeyException, NoSuchAlgorithmException, ParseException, IOException {
        int button = 7;

        //ALL, INSUFFICIENT_DATA, OK, ALARM

        TreeMap<String, String> request = new TreeMap<String, String>();

        request = generateRequire(button, request);

        request.put("response", "json");
        request.put("apiKey", getApikey());

        if (!statevalue.equals("ALL")) request.put("statevalue", statevalue);

        String req_message = generateReq(request);

        JSONObject obj = readJsonFromUrl(req_message);

        JSONObject parse_listalarmsresponse = (JSONObject) obj.get("listalarmsresponse");

        JSONArray parse_alarm = (JSONArray) parse_listalarmsresponse.get("alarm");
        JSONObject alarm;
        JSONObject dimensions;
        JSONArray dimension;
        JSONObject alarmactions;
        JSONArray alarmaction;

        System.out.println("총 alarm 수: " + parse_listalarmsresponse.get("count"));

        ArrayList<String[]> list = new ArrayList<String[]>();
        for (int i = 0; i < parse_alarm.size(); i++) {
            alarm = (JSONObject) parse_alarm.get(i);

//            System.out.println("#########################################");
//            System.out.println("알람명: "+  alarm.get("alarmname"));

            String state = String.valueOf(alarm.get("statevalue"));
            if (state.equals("OK")) state = "안정";
            else if (state.equals("ALARM")) state = "알람 발생";
            else if (state.equals("INSUFFICIENT_DATA")) state = "데이터 부족";

            String comparisonoperator = String.valueOf(alarm.get("comparisonoperator"));
            if (comparisonoperator.equals("GreaterThanThreshold")) comparisonoperator = "보다 클 때 ";
            else if (comparisonoperator.equals("GreaterThanOrEqualToThreshold"))
                comparisonoperator = "보다 크거나 같을 때 ";
            else if (comparisonoperator.equals("LessThanThreshold"))
                comparisonoperator = "보다 작을 때 ";
            else if (comparisonoperator.equals("LessThanOrEqualToThreshold"))
                comparisonoperator = "보다 작거나 같을 때 ";

            dimensions = (JSONObject) alarm.get("dimensions");
            dimension = (JSONArray) dimensions.get("dimension");

            String dimensionCount = String.valueOf(dimensions.get("count"));
            String namespace = String.valueOf(alarm.get("namespace"));

            if (dimensionCount.equals("0")) {
                if (namespace.equals("ucloud/server")) namespace = "서버:모든서버통합";
                else if (namespace.equals("ucloud/vr")) namespace = "ucloud/vr";
            } else {
                JSONObject dimensionOb = (JSONObject) dimension.get(0);
                String dimensionName = String.valueOf(dimensionOb.get("name"));
                if (dimensionName.equals("templatename")) namespace = "서버:운영체제별통합";
                else if (dimensionName.equals("name")) {
                    if (namespace.equals("ucloud/vr")) namespace = "ucloud/vr:name";
                    else if (namespace.equals("ucloud/server")) namespace = "서버:개별서버메트릭";
                } else if (dimensionName.equals("serviceofferingname")) namespace = "서버:스펙별통합";
                else if (dimensionName.equals("LB")) namespace = "CloudLB:LB";
            }

//            System.out.println("상태: "+  state );
//            System.out.println("알람 발생조건: "+  alarm.get("metricname") + " " + alarm.get("statistic") + "이(가) " + alarm.get("threshold") + alarm.get("unit") + comparisonoperator + "알람이 발생한다.");
            String sr = alarm.get("metricname") + " " + alarm.get("statistic") + "이(가) " + alarm.get("threshold") + alarm.get("unit") + comparisonoperator + "알람이 발생한다.";
//            System.out.println("액션 수행 여부: "+  (String.valueOf(alarm.get("actionsenabled")).equals("true")? "활성화" : "비활성화" ));
            String sy = (String.valueOf(alarm.get("actionsenabled")).equals("true") ? "활성화" : "비활성화");
//            System.out.println("구분: "+  namespace );

            alarmactions = (JSONObject) alarm.get("alarmactions");
            alarmaction = (JSONArray) alarmactions.get("alarmaction");
            JSONObject alarmactionObj;

            List<String> actionList = new ArrayList<String>();

            String s = "";
//            System.out.print("액션 종류: " );
            for (int k = 0; k < alarmaction.size(); k++) {
                s += (alarmaction.get(k) + " ");
            }

            //이름, 상태, 알람 발생 조건, 액션 수행 여부, 수행 액션, 구분
            list.add(new String[]{(String) alarm.get("alarmname"), state, sr, sy, s, namespace});
        }
        return list;
    }

    /**
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws IOException
     * @throws ParseException
     * @brief 메트릭에 대한 알람 현황(갯수) 출력
     **/
    public static int[] listAlarmStatus() throws InvalidKeyException, NoSuchAlgorithmException, ParseException, IOException {
        int button = 7;

        TreeMap<String, String> request = new TreeMap<String, String>();
        request = generateRequire(button, request);

        request.put("response", "json");
        request.put("apiKey", getApikey());


        String req_message = generateReq(request);
//
//        System.out.println("Request Message is...");
//        System.out.println(req_message);
//        System.out.println();

        JSONObject obj = readJsonFromUrl(req_message);

        JSONObject parse_listalarmsresponse = (JSONObject) obj.get("listalarmsresponse");

        JSONArray parse_alarm = (JSONArray) parse_listalarmsresponse.get("alarm");
        JSONObject alarm;


        System.out.println("총 alarm 수: " + parse_listalarmsresponse.get("count"));


        int cnt[] = new int[3];

        for (int i = 0; i < parse_alarm.size(); i++) {
            alarm = (JSONObject) parse_alarm.get(i);

            String state = String.valueOf(alarm.get("statevalue"));
            if (state.equals("OK")) cnt[2]++;
            else if (state.equals("ALARM")) cnt[0]++;
            else if (state.equals("INSUFFICIENT_DATA")) cnt[1]++;
        }
        return cnt;
    }


    /**
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws IOException
     * @throws ParseException
     * @brief (대시보드용) 발생 알람 출력을 위한 함수
     **/
    public static void listAlarmsForDashboard() throws InvalidKeyException, NoSuchAlgorithmException, ParseException, IOException {
        int button = 7;

        TreeMap<String, String> request = new TreeMap<String, String>();

        request = generateRequire(button, request);

        request.put("response", "json");
        request.put("apiKey", getApikey());

        String req_message = generateReq(request);
//
//        System.out.println("Request Message is...");
//        System.out.println(req_message);
//        System.out.println();

        JSONObject obj = readJsonFromUrl(req_message);

        JSONObject parse_listalarmsresponse = (JSONObject) obj.get("listalarmsresponse");

        JSONArray parse_alarm = (JSONArray) parse_listalarmsresponse.get("alarm");
        JSONObject alarm;

        for (int i = 0; i < parse_alarm.size(); i++) {

            alarm = (JSONObject) parse_alarm.get(i);
            String s = "";

            String state = String.valueOf(alarm.get("statevalue"));
            if (state.equals("ALARM")) {
                s = "[Watch]" + String.valueOf(alarm.get("metricname")) + " " + String.valueOf(alarm.get("alarmname") + "이 발생했습니다.");
            }
        }


    }

    /**
     * @throws ParseException
     * @brief Watch 기능에 해당하는, 전체 서버에 대한 지정 메트릭 통계를 위한 함수
     * @param alarmname 발생한 alarmname
     * @param metricname 전체서버에 대해 조회하길 원하는 metricname
     * @param timestamp 알람이 발생한 timestamp
     **/
    public static void showAlarmMetric(String alarmname, String metricname, LocalDateTime timestamp, HashMap<String, HashMap<String, String>> alarmMetricList) throws IOException, InvalidKeyException, NoSuchAlgorithmException, ParseException {

        int button = 6;

        HashMap<String, String> points = new HashMap<String, String> ();

        TreeMap<String, String> request = new TreeMap<String, String>();

        request = generateRequire(button, request);

        request.put("response", "json");
        request.put("apiKey", getApikey());
        request.put("metricname", metricname);
        request.put("namespace", namespace);

        request.put("statistics.member.1", statistics);

        ZonedDateTime currDateTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));

        endtime = currDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));

        int timestampInt = Integer.parseInt(String.valueOf(timestamp).substring(0,4) + String.valueOf(timestamp).substring(5,7) + String.valueOf(timestamp).substring(8,10) + String.valueOf(timestamp).substring(11,13));
        int endtimeInt = Integer.parseInt(endtime.substring(0,4) + endtime.substring(5,7) + endtime.substring(8,10) + endtime.substring(11,13));


        // timestamp보다 현재 시간이 3시간 이상 지나지 않았을 경우
        if(  (timestampInt + 3) > endtimeInt ) {
            request.put("endtime", endtime);

            starttime = currDateTime.minusHours(6).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
            request.put("starttime", starttime);
        }
        // timestamp보다 3시간 이상 지났을 경우
        else {

            endtime = timestamp.plusHours(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
            starttime = timestamp.minusHours(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));

            request.put("endtime", endtime);
            request.put("starttime", starttime);
        }

        request.put("period", "60");

        if(metricname.equals("CPUUtilization")) request.put("unit", "Percent");
        else request.put("unit", "Bytes");

        String req_message = generateReq(request);

        System.out.println("Request Message is...");
        System.out.println(req_message);
        System.out.println();

        JSONObject obj =  readJsonFromUrl(req_message);

        JSONObject parse_getmetricstatisticsresponse = (JSONObject) obj.get("getmetricstatisticsresponse");

        JSONArray parse_metricstatistics = (JSONArray) parse_getmetricstatisticsresponse.get("metricstatistics");
        JSONObject metric;

        System.out.println("총 point 수: "+ parse_getmetricstatisticsresponse.get("count"));

        for(int i = 0 ; i <  parse_metricstatistics.size(); i++) {

            metric = (JSONObject) parse_metricstatistics.get(i);


            points.put(String.valueOf(metric.get("timestamp")).substring(5,16).replaceAll("T", " "), String.valueOf(metric.get(statistics.toLowerCase())));


            System.out.println("##############################");

            System.out.println("timestamp: "+  String.valueOf(metric.get("timestamp")).substring(5,16).replaceAll("T", " "));
            System.out.println(statistics+ ": "+  metric.get(statistics.toLowerCase()));
            System.out.println("unit: "+  metric.get("unit"));


        }


        alarmMetricList.put(alarmname, points);

    }


    /**
     * @throws ParseException
     * @brief 발생 알람에 대한 특정 서버의 지정 메트릭 통계를 위한 함수
     * @param alarmname 발생한 알람 이름
     * @param metricname 특성 서버에 대해 조회하길 원하는 metricname
     * @param value 조회하길 원하는 특정 서버의 displayname(id) 값
     * @param timestamp 알람이 발생한 timestamp
     **/
    public static void showAlarmMetric(String alarmname, String metricname, String value, LocalDateTime timestamp, HashMap<String, HashMap<String, String>> alarmMetricList) throws IOException, InvalidKeyException, NoSuchAlgorithmException, ParseException {
        int button = 6;

        HashMap<String, String> points = new HashMap<String, String> ();

        TreeMap<String, String> request = new TreeMap<String, String>();

        request = generateRequire(button, request);

        request.put("response", "json");
        request.put("apiKey", getApikey());
        request.put("metricname", metricname);
        request.put("namespace", namespace);

        request.put("statistics.member.1", statistics);

        request.put("dimensions.member.1.name", "name");
        request.put("dimensions.member.1.value", value);

        ZonedDateTime currDateTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));

        endtime = currDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));


        int timestampInt = Integer.parseInt(String.valueOf(timestamp).substring(0,4) + String.valueOf(timestamp).substring(5,7) + String.valueOf(timestamp).substring(8,10) + String.valueOf(timestamp).substring(11,13));
        int endtimeInt = Integer.parseInt(endtime.substring(0,4) + endtime.substring(5,7) + endtime.substring(8,10) + endtime.substring(11,13));

        // timestamp보다 현재 시간이 3시간 이상 지나지 않았을 경우
        if(  (timestampInt + 3) > endtimeInt ) {
            request.put("endtime", endtime);

            starttime = currDateTime.minusHours(6).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
            request.put("starttime", starttime);
        }
        // timestamp보다 3시간 이상 지났을 경우
        else {

            endtime = timestamp.plusHours(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
            starttime = timestamp.minusHours(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));

            request.put("endtime", endtime);
            request.put("starttime", starttime);
        }

        request.put("period", "60");

        if(metricname.equals("CPUUtilization")) request.put("unit", "Percent");
        else request.put("unit", "Bytes");

        String req_message = generateReq(request);

        System.out.println("Request Message is...");
        System.out.println(req_message);
        System.out.println();

        JSONObject obj =  readJsonFromUrl(req_message);

        JSONObject parse_getmetricstatisticsresponse = (JSONObject) obj.get("getmetricstatisticsresponse");

        JSONArray parse_metricstatistics = (JSONArray) parse_getmetricstatisticsresponse.get("metricstatistics");
        JSONObject metric;

        System.out.println("총 point 수: "+ parse_getmetricstatisticsresponse.get("count"));

        for(int i = 0 ; i <  parse_metricstatistics.size(); i++) {

            metric = (JSONObject) parse_metricstatistics.get(i);


            points.put(String.valueOf(metric.get("timestamp")).substring(5,16).replaceAll("T", " "), String.valueOf(metric.get(statistics.toLowerCase())));


            System.out.println("##############################");

            System.out.println("timestamp: "+  String.valueOf(metric.get("timestamp")).substring(5,16).replaceAll("T", " "));
            System.out.println(statistics+ ": "+  metric.get(statistics.toLowerCase()));
            System.out.println("unit: "+  metric.get("unit"));


        }


        alarmMetricList.put(alarmname, points);



    }
    /**
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws IOException
     * @throws ParseException
     * @brief 알람 발생 시, 알람이 발생한 메트릭 출력. StateUpdate 중에서도 from OK/INSUFFICIENT_DATA to ALARM 인 경우에만 알람 출력.
     **/
    public static void listAlarmHistory() throws InvalidKeyException, NoSuchAlgorithmException, ParseException, IOException {
        int button = 8;

        alarmMetricList = new HashMap<String, HashMap<String, String>> ();
        alarmList = new ArrayList<String>();
        alarmThreshold = new HashMap<String, String> ();

        TreeMap<String, String> request = new TreeMap<String, String>();


        request = generateRequire(button, request);

        request.put("response", "json");
        request.put("apiKey", getApikey());



        String req_message = generateReq(request);

        System.out.println("Request Message is...");
        System.out.println(req_message);
        System.out.println();

        JSONObject obj =  readJsonFromUrl(req_message);

        JSONObject parse_listalarmhistoryresponse = (JSONObject) obj.get("listalarmhistoryresponse");

        JSONArray pasrse_alarmhistory = (JSONArray) parse_listalarmhistoryresponse.get("alarmhistory");
        JSONObject alarmhistory;

        int history_num = 0;
        String alarmname = "";


        for(int i = 0 ; i <  pasrse_alarmhistory.size(); i++) {
            alarmhistory = (JSONObject) pasrse_alarmhistory.get(i);

            if(i > 0) {
                // 중복되는 알람명이면 pass
                if(alarmname.equals(String.valueOf(alarmhistory.get("alarmname")))) continue;
            }

            // StateUpdate 중 from OK/INSUFFICIENT_DATA to ALARM 인 경우 알람 발생 메트릭 출력
            if(String.valueOf(alarmhistory.get("historysummary")).substring(String.valueOf(alarmhistory.get("historysummary")).length()-5, String.valueOf(alarmhistory.get("historysummary")).length()).equals("ALARM")) {
                history_num++;

                alarmname = String.valueOf(alarmhistory.get("alarmname"));
                System.out.println("알람명: " + alarmname);
                alarmList.add(alarmname);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
                LocalDateTime timestamp = LocalDateTime.parse(String.valueOf(alarmhistory.get("timestamp")), formatter);
                System.out.println("알람 발생시간: " + timestamp);

                // 해당 알람이름에 대한 알람 조회 - namespace 및 임계치를 얻음
                request.clear();
                request = generateRequire(7, request);

                request.put("response", "json");
                request.put("apiKey", getApikey());
                request.put("alarmnames.member.1", alarmname);

                req_message = generateReq(request);

                obj =  readJsonFromUrl(req_message);

                System.out.println("Request Message is...");
                System.out.println(req_message);
                System.out.println();


                obj =  readJsonFromUrl(req_message);

                JSONObject parse_listalarmsresponse = (JSONObject) obj.get("listalarmsresponse");

                JSONArray pasrse_alarm = (JSONArray) parse_listalarmsresponse.get("alarm");
                JSONObject alarm = (JSONObject) pasrse_alarm.get(0);
                JSONObject dimensions = (JSONObject) alarm.get("dimensions");
                JSONArray dimension = (JSONArray) dimensions.get("dimension");

                String dimensionCount = String.valueOf(dimensions.get("count"));
                String namespace = String.valueOf(alarm.get("namespace"));
                String metricname = String.valueOf(alarm.get("metricname"));

                String threshold = String.valueOf(alarm.get("threshold"));
                alarmThreshold.put(alarmname, threshold);


                if(dimensionCount.equals("0")) {
                    if(namespace.equals("ucloud/server")) {
                        namespace = "서버:모든서버통합";

                        showAlarmMetric(alarmname, metricname, timestamp, alarmMetricList);
                    }
                    else if (namespace.equals("ucloud/vr")) namespace = "ucloud/vr";
                }

                else {
                    JSONObject dimensionOb = (JSONObject) dimension.get(0);
                    String dimensionName = String.valueOf(dimensionOb.get("name"));
                    if(dimensionName.equals("templatename")) namespace = "서버:운영체제별통합";
                    else if(dimensionName.equals("name")) {
                        if (namespace.equals("ucloud/vr")) namespace = "ucloud/vr:name";
                        else if (namespace.equals("ucloud/server")) {
                            String value = String.valueOf(dimensionOb.get("value"));
                            namespace = "서버:개별서버메트릭";

                            showAlarmMetric(alarmname, metricname, value, timestamp, alarmMetricList);
                        }
                    }
                    else if(dimensionName.equals("serviceofferingname")) namespace = "서버:스펙별통합";
                    else if(dimensionName.equals("LB")) namespace = "CloudLB:LB";
                }



                System.out.println();
                if (history_num >= 3) break; // 최근 3개까지만 출력
            }

        }

    }
}
