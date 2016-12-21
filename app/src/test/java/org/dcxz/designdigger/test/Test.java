package org.dcxz.designdigger.test;

import org.dcxz.designdigger.util.API;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * <br/>
 * Created by OvO on 2016/12/21.<br/>
 * ChangeLog :
 * <pre>
 * </pre>
 */
public class Test {
    private static final String TAG = "Test";

    @org.junit.Test
    public void test() throws IOException {
        String locations[] =
                new String[]{
                        "https://api.dribbble.com/v1/simplebits",//get failed
                        API.END_POINT_SHOTS_PAGE + 1,//get success
                        "https://dribbble.com/oauth/authorize/?client_id=e8e27bfbaa6d35bfe58255c68957c70aecfb444b20fc8197bf1c5f9acc1181ce",//get success
                        "https://dribbble.com/oauth/authorize/?client_id=e8e27bfbaa6d35bfe58255c68957c70aecfb444b20fc8197bf1c5f9acc1181ce&redirect_uri=www.baidu.com",
                };
        post(locations[2]);
    }

    @SuppressWarnings("unused")
    private void get(String location) throws IOException {
        URL url = new URL(location);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setConnectTimeout(10000);
        getResponse(connection);
    }

    @SuppressWarnings("unused")
    private void post(String location) throws IOException {
        URL url = new URL(location);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setConnectTimeout(10000);
        connection.setRequestProperty(API.Oauth2.AUTHORIZATION, API.Oauth2.AUTHORIZATION_TYPE + API.Oauth2.ACCESS_TOKEN);
        PrintWriter writer = new PrintWriter(connection.getOutputStream());
        writer.print("login=XieEDeHeiShou&password=159357asD");
        writer.flush();

        getResponse(connection);
    }

    private void getResponse(HttpsURLConnection connection) throws IOException {
        if (connection.getResponseCode() == 200) {
            System.out.println("Connect success, now receiving response...");
            String response = "";
            byte cache[] = new byte[1024];
            int length;
            InputStream is = connection.getInputStream();
            while ((length = is.read(cache)) != -1) {
                response += new String(cache, 0, length);
            }
            System.out.println(response);
        } else {
            System.out.println("test: " + connection.getResponseCode() + "\r\n" + connection.getResponseMessage());
        }
    }
}