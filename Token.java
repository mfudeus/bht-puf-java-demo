package de.bhtpaf.classes;

import com.google.gson.Gson;

public class Token {
    public String token;

    public static Token createFromJson(String json)
    {
        return new Gson().fromJson(json, Token.class);
    }
}
