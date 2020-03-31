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

public class APIcall_Messaging extends APIcall_main {
    /**
     * @throws ParseException
     * @return 토픽의 토픽 URN, 구독URN, 프로토콜, 설명, 수신처를 담은 arraylist
     * @brief Messaging 기능에 해당하는, 토픽 및 구독 정보 출력을 위한 함수
     **/
    public static ArrayList<String[]> listSubscriptions() throws IOException, InvalidKeyException, NoSuchAlgorithmException, ParseException {

        int button = 10;

        TreeMap<String, String> request = new TreeMap<String, String>();
        request = generateRequire(button, request);

        request.put("response", "json");
        request.put("apiKey", getApikey());


        String req_message = generateReq(request);

        JSONObject obj =  readJsonFromUrl(req_message);

        JSONObject parse_listsubscriptionsresponse = (JSONObject) obj.get("listsubscriptionsresponse");

        JSONObject parse_listsubscriptionsresult = (JSONObject) parse_listsubscriptionsresponse.get("listsubscriptionsresult");

        JSONArray parse_subscriptions = (JSONArray) parse_listsubscriptionsresult.get("subscriptions");

        JSONObject subscription;

        ArrayList<String[]> list = new ArrayList<String[]>();
        for(int i = 0 ; i < parse_subscriptions.size(); i++) {
            subscription = (JSONObject) parse_subscriptions.get(i);

            JSONObject member = (JSONObject) subscription.get("member");

            //토픽 URN, 구독URN, 프로토콜, 설명, 수신처
            list.add(new String[]{(String) member.get("topicurn"), (String)member.get("subscriptionurn"),
                    (String)member.get("protocol"), "토픽 설명", (String)member.get("endpoint")});
        }
        return list;

    }

    /**
     * @throws ParseException
     * @param message message 내용
     * @param subject message 제목
     * @param topicUrn message 발행 할 토픽 URN
     * @brief Messaging 기능에 해당하는, 토픽 및 구독 정보 출력을 위한 함수
     **/
    public static void publishMessage(String topicUrn, String subject, String message) throws IOException, InvalidKeyException, NoSuchAlgorithmException, ParseException, ParseException {

        int button = 12;

        TreeMap<String, String> request = new TreeMap<String, String>();
        request = generateRequire(button, request);

        request.put("response", "json");
        request.put("apiKey", getApikey());
        request.put("topicUrn", topicUrn);
        request.put("subject", subject);
        request.put("message", message);

        String req_message = generateReq(request);
//
//        System.out.println("Request Message is...");
//        System.out.println(req_message);

        readJsonFromUrl(req_message);

    }
}
