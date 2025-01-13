package com.virtual.cloud.om.workspace.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author z13465 2022/4/15
 */
@RestController
@RequestMapping("/home")
public class NativeController {

    @GetMapping
    public String nativeController(){
        return "Home Page.";
    }
}
