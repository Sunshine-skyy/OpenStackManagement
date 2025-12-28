package com.openstack.management.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserSession implements Serializable {
    private String username;
    private String password;
    private String token;
    private String endpoint;
    private String domain;
    private String project;
}


