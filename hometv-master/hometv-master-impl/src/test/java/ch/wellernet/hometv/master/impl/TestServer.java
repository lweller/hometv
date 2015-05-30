package ch.wellernet.hometv.master.impl;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestServer {
    @SuppressWarnings("resource")
    public static void main(String... args) throws Exception {
        new ClassPathXmlApplicationContext("application-context.xml");
    }
}
