package com.haodong.controller;

import com.haodong.service.WendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by haodong
 */
//@Controller
//public class SettingController {
//    @Autowired
//    WendaService wendaService;
//
//    @RequestMapping(path = {"/setting"}, method = {RequestMethod.GET})
//    @ResponseBody
//    public String setting() {
//        return "Setting OK. " + wendaService.getMessage(1);
//    }
//}