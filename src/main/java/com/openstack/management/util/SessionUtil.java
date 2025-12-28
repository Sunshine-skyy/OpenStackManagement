package com.openstack.management.util;

import com.openstack.management.model.UserSession;

import javax.servlet.http.HttpSession;

public class SessionUtil {
    public static UserSession getUserSession(HttpSession session) {
        return (UserSession) session.getAttribute("userSession");
    }
}


