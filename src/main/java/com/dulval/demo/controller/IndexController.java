package com.dulval.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @GetMapping(value = "/", headers = "Connection!=Upgrade")
    public String status() {
        return "OK";
    }
}
