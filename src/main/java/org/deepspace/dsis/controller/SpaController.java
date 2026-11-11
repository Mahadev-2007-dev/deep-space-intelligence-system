package org.deepspace.dsis.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * SPA (Single Page Application) Fallback Controller.
 *
 * React Router handles all client-side routing. This controller ensures
 * that any direct navigation to a React route (e.g. /dashboard, /satellites)
 * returns index.html so React Router can take over, rather than a 404.
 *
 * API routes (/api/**) and static assets are excluded automatically
 * because Spring Boot serves those before reaching this controller.
 */
@Controller
public class SpaController {

    /**
     * Catch-all route for React SPA.
     * Excludes paths that contain a dot (static assets like .js, .css, .png)
     * and anything starting with /api.
     */
    @RequestMapping(value = {
        "/",
        "/{path:^(?!api|actuator)[^\\.]*}",
        "/{path:^(?!api|actuator)[^\\.]*}/**/{subpath:[^\\.]*}"
    })
    public String forward() {
        return "forward:/index.html";
    }
}
