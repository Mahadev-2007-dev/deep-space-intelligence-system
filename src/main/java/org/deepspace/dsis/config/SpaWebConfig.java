package org.deepspace.dsis.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

/**
 * SPA (Single Page Application) Static Resource Handler.
 *
 * Forwards all non-API and non-static requests to index.html for client-side
 * React Router routing. This replaces complex regex mappings that fail under
 * Spring Boot 3's PathPatternParser.
 */
@Configuration
public class SpaWebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        Resource requestedResource = location.createRelative(resourcePath);
                        if (requestedResource.exists() && requestedResource.isReadable()) {
                            return requestedResource;
                        }
                        // Don't forward API requests to index.html
                        if (resourcePath.startsWith("api") || resourcePath.startsWith("/api")) {
                            return null;
                        }
                        // Fallback to index.html for SPA routes
                        return new ClassPathResource("/static/index.html");
                    }
                });
    }
}
