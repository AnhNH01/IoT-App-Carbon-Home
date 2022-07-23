package com.example.iotapp.models;

public class LoginResult {

    private User user;
    private String token;
    private String homeId;
    private String brokerPassword;
    private String brokerHost;


    public LoginResult(User user, String token, String homeId, String brokerPassword, String brokerHost) {
        this.user = user;
        this.token = token;
        this.homeId = homeId;
        this.brokerPassword = brokerPassword;
        this.brokerHost = brokerHost;
    }

    public void setBrokerHost(String brokerHost) {
        this.brokerHost = brokerHost;
    }

    public String getBrokerHost() {
        return brokerHost;
    }

    public String getBrokerPassword() {
        return brokerPassword;
    }

    public void setBrokerPassword(String brokerPassword) {
        this.brokerPassword = brokerPassword;
    }

    public String getHomeId() {
        return homeId;
    }

    public void setHomeId(String homeId) {
        this.homeId = homeId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public class User {
        private String name;

        public User(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Override
    public String toString() {
        return "LoginResult{" +
                "user=" + user.getName() +
                ", token='" + token + '\'' +
                ", homeId='" + homeId + '\'' +
                '}';
    }
}
