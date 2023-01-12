package de.bhtpaf.services;

import com.google.gson.*;
import de.bhtpaf.Token;
import de.bhtpaf.User;
import de.bhtpaf.StdResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class API {
    private String _apiUrl;
    private CloseableHttpClient _client;

    public API(String apiUrl)
    {
        _apiUrl = apiUrl;
        _client = HttpClients.createDefault();
    }

    public String getApiUrl()
    {
        return _apiUrl;
    }

    public User signupUser(User user)
    {
        String path = _apiUrl + "/auth/signup";
        StringEntity entity = new StringEntity(user.toJson(), ContentType.APPLICATION_JSON);

        HttpPost request = new HttpPost(path);
        request.setEntity(entity);

        User newUser = null;
        try
        {
            CloseableHttpResponse response = _client.execute(request);
            // System.out.println(path + ": " + response.getStatusLine());

            HttpEntity responseEntity = response.getEntity();
            if (response.getStatusLine().getStatusCode() == 200)
            {
                String result = _getStringFromInputStream(responseEntity.getContent());
                newUser = User.createFromJson(result);
            }

            EntityUtils.consume(responseEntity);

            response.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return newUser;
    }

    public User loginUser(User user)
    {
        String path = _apiUrl + "/auth/login";
        Token token = null;

        StringEntity entity = new StringEntity(user.toJson(), ContentType.APPLICATION_JSON);

        HttpPost request = new HttpPost(path);
        request.setEntity(entity);
        try
        {
            CloseableHttpResponse response = _client.execute(request);
            // System.out.println(path + ": " + response.getStatusLine());

            HttpEntity responseEntity = response.getEntity();
            if (response.getStatusLine().getStatusCode() == 200)
            {
                token = Token.createFromJson(_getStringFromInputStream(responseEntity.getContent()));
            }

            EntityUtils.consume(responseEntity);

            response.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        // Login failed
        if (token == null)
        {
            return null;
        }

        path = _apiUrl + "/api/v1/user";
        HttpGet getRequest = new HttpGet(path);
        getRequest.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        User loggedInUser = null;
        try
        {
            CloseableHttpResponse response = _client.execute(getRequest);
            // System.out.println(path + ": " + response.getStatusLine());

            HttpEntity responseEntity = response.getEntity();
            if (response.getStatusLine().getStatusCode() == 200)
            {
                String result = _getStringFromInputStream(responseEntity.getContent());
                loggedInUser = User.createFromJson(result);
                loggedInUser.token = token;
            }

            EntityUtils.consume(responseEntity);

            response.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return  loggedInUser;
    }

    public boolean logoutUser(User user)
    {
        String path = _apiUrl + "/auth/logout";

        HttpPost request = new HttpPost(path);

        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + user.token.token);

        StringEntity entity = new StringEntity(user.toJson(), ContentType.APPLICATION_JSON);
        request.setEntity(entity);

        boolean retValue = false;

        try
        {
            CloseableHttpResponse response = _client.execute(request);
            // System.out.println(path + ": " + response.getStatusLine());

            HttpEntity responseEntity = response.getEntity();

            if (response.getStatusLine().getStatusCode() == 200)
            {
                retValue = true;
            }

            EntityUtils.consume(responseEntity);

            response.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return retValue;
    }

    private String _getStringFromInputStream(InputStream inputStream)
    {
        try
        {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int length; (length = inputStream.read(buffer)) != -1; ) {
                result.write(buffer, 0, length);
            }
            // StandardCharsets.UTF_8.name() > JDK 7
            return result.toString("UTF-8");
        }
        catch (Exception e)
        {
            return "";
        }

    }

}
