package br.com.chatia.util;

import br.com.chatia.model.User;

public class SessionManager {

    private static User currentUser;

    private SessionManager(){
    }

    public static synchronized void login(User user){
        currentUser = user;
    }

    public static synchronized void logout(){
        currentUser = null;
    }

    public static synchronized User getCurrentUser(){
        return currentUser;
    }

    public static synchronized boolean isLoggedIn(){
        return currentUser != null;
    }
}
