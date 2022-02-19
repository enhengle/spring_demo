package com.practise.demo.controller;

import com.practise.demo.common.LogPrint;
import com.practise.demo.response.Response;
import com.practise.demo.service.LiveViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lingwang
 * @date 2022/2/19 15:51
 */
@RestController
@RequestMapping("/stat")
public class StatController {

    @Autowired
    private LiveViewService liveViewService;

    @LogPrint
    @RequestMapping(value = "findAll", method = RequestMethod.POST)
    public Response findAll(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return Response.ok(liveViewService.findAll(page, size));
    }
}
