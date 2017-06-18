package io.anglehack.eso.tknkly.serial.send;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.anglehack.eso.tknkly.models.MotionData;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;


/**
 * Created by root on 6/17/17.
 */
public class RestSend implements SendInterface {

    public static ObjectMapper objectMapper = new ObjectMapper();

    public static void sendList(List<MotionData> data, String userId) throws IOException {

        String url = "http://localhost:8080/serial/"+userId;


        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);

        post.setHeader("Content-Type", MediaType.APPLICATION_JSON);
        post.setEntity(new StringEntity(objectMapper.writeValueAsString(data)));

        HttpResponse response = client.execute(post);
        if (response.getStatusLine().getStatusCode() != 200) {
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + post.getEntity());
            System.out.println("Response Code : " +
                    response.getStatusLine().getStatusCode());

            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
        }
    }

    @Override
    public void initialize() {

    }

    @Override
    public void sendModelData(MotionData object, String userId) throws IOException {

        String url = "http://localhost:8080/serial/single/"+userId;


        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);

        post.setHeader("Content-Type", MediaType.APPLICATION_JSON);
        post.setEntity(new StringEntity(objectMapper.writeValueAsString(object)));

        HttpResponse response = client.execute(post);
        if (response.getStatusLine().getStatusCode() != 200) {
            System.out.println("Response Code : " +
                    response.getStatusLine().getStatusCode());
        }
    }
}
