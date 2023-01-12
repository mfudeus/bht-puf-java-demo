package de.bhtpaf.classes;

import com.google.gson.Gson;

import java.util.Date;

public class User
{
    public String id;
    public String username;
    public String password;
    public boolean logged_in;

    public Token token;

    public User()
    { }

    public User(User user)
    {
        id = user._id;
        username = user.username;
        password = user.password;
        logged_in = user.logged_in;
    }

    public String toJson()
    {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static User createFromJson(String json)
    {
        return new Gson().fromJson(json, User.class);
    }
}
