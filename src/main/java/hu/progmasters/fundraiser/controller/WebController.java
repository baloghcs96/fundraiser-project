package hu.progmasters.fundraiser.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/fundraiser/")
public class WebController {

    @GetMapping()
    public String index() {
        return "index";
    }

}