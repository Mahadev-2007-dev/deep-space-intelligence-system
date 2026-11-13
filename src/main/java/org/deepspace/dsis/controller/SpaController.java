package org.deepspace.dsis.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * SPA (Single Page Application) Controller.
 * Handles the root URL; nested client-side routes are handled
 * by SpaWebConfig and PathResourceResolver.
 */
@Controller
public class SpaController {

    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }
}
