package fr.univ_lyon1.info.m1.Modele;

/**
 * Created by fanuel on 20/11/16.
 */
public class Message {

    private String message;
    private String user;

    public Message(String user, String message) {
        this.user = user;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
