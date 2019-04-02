package com.luyouchina.comm;

import javax.servlet.http.HttpServletRequest;

public class AjaxUtils {

    public static boolean isAjaxRequest(HttpServletRequest request) {
        String requestedWith = request.getHeader("X-Requested-With");
        return requestedWith != null ? "XMLHttpRequest".equals(requestedWith) : false;
    }

    public static boolean isAjaxUploadRequest(HttpServletRequest request) {
        return request.getParameter("ajaxUpload") != null;
    }

    private AjaxUtils() {
    }

}
