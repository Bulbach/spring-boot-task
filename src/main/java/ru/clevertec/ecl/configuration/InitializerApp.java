package ru.clevertec.ecl.configuration;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class InitializerApp implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {

        AnnotationConfigWebApplicationContext context =new AnnotationConfigWebApplicationContext();
        context.register(AppConfig.class);
        context.register(WebMvcConfig.class);

        ServletRegistration.Dynamic servletRegistration
                = servletContext.addServlet("dispatcher", new DispatcherServlet(context));
        servletRegistration.setLoadOnStartup(1);
        servletRegistration.addMapping("/");
    }
}
