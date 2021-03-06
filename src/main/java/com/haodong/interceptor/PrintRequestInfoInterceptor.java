package com.haodong.interceptor;

import com.google.gson.Gson;
import com.haodong.util.JsonSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class PrintRequestInfoInterceptor implements HandlerInterceptor {

    @Autowired
    Gson gson;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        StringBuffer sb=new StringBuffer();
        sb.append("request {");
        sb.append(JsonSerializer.serialize(httpServletRequest.getParameterMap()));
        sb.append("} " + httpServletRequest.getRequestURI() + "\n");
        if(modelAndView != null) {
            sb.append("response " + gson.toJson(modelAndView));
        }
        System.out.println(sb.toString());
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
