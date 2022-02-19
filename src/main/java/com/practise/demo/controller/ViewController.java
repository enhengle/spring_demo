package com.practise.demo.controller;

import com.practise.demo.common.LogPrint;
import com.practise.demo.response.Response;
import com.practise.demo.service.ViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lingwang
 * @date 2022/2/19 15:48
 */
@RestController
@RequestMapping("/manager")
public class ViewController {

    @Autowired
    private ViewService viewService;

    @LogPrint
    @RequestMapping(value = "/findList", method = RequestMethod.POST)
    public Response findList(@RequestParam(value = "state", required = false, defaultValue = "1") int state) {
        return Response.ok(viewService.findList(state));
    }

}
