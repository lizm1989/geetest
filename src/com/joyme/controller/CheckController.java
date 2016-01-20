package com.joyme.controller;

import com.joyme.geetest.GeetestConfig;
import com.joyme.geetest.GeetestLib;
import com.joyme.geetest.GeetestMsgLib;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * Created by zhimingli on 2016/1/13 0013.
 */

@Controller
@RequestMapping("/")
public class CheckController {

    @ResponseBody
    @RequestMapping("/get")
    public String get(HttpServletRequest request, HttpServletResponse response) throws Exception {
        GeetestMsgLib gtMsgSdk = new GeetestMsgLib();
        gtMsgSdk.setCaptchaId(GeetestConfig.getCaptcha_id());
        gtMsgSdk.setPrivateKey(GeetestConfig.getPrivate_key());
        gtMsgSdk.setDebugCode(true);

        gtMsgSdk.setGtMsgSession(request);


        String resStr = "{}";

        if (gtMsgSdk.preProcess() == 1) {
            // gt server is in use
            resStr = gtMsgSdk.getSuccessPreProcessRes();
            gtMsgSdk.setGtServerStatusSession(request, 1);

        } else {
            // gt server is down
            resStr = gtMsgSdk.getFailPreProcessRes();
            gtMsgSdk.setGtServerStatusSession(request, 0);
        }
        // PrintWriter out = response.getWriter();
        //   out.println(resStr);
        return resStr;
    }


    @ResponseBody
    @RequestMapping("/check")
    public String check(HttpServletRequest request, HttpServletResponse response) throws Exception {
        GeetestLib gtSdk = new GeetestLib();

        gtSdk.setPrivateKey(GeetestConfig.getPrivate_key());

        //从session中获取gt-server状态
        int gt_server_status_code = GeetestLib.getGtServerStatusSession(request);

        //从session中获取challenge
        gtSdk.getChallengeSession(request);

        String gtResult = "fail";

        if (gt_server_status_code == 1) {
            //gt-server正常，向gt-server进行二次验证
            gtResult = gtSdk.enhencedValidateRequest(request);
            System.out.println(gtResult);
        } else {
            // gt-server非正常情况下，进行failback模式验证
            System.out.println("failback:use your own server captcha validate");
            gtResult = "fail";

            gtResult = gtSdk.failbackValidateRequest(request);


        }


        if (gtResult.equals(GeetestLib.success_res)) {
            // 验证成功

            return GeetestLib.success_res + ":" + gtSdk.getVersionInfo();

        } else if (gtResult.equals(GeetestLib.forbidden_res)) {
            // 验证被判为机器人

            return GeetestLib.forbidden_res + ":" + gtSdk.getVersionInfo();
        } else {
            // 验证失败

            return GeetestLib.fail_res + ":" + gtSdk.getVersionInfo();
        }
    }


    @RequestMapping("/success")
    public ModelAndView success(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return new ModelAndView("/main");
    }
}
