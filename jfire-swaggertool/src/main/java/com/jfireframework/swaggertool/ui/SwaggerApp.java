package com.jfireframework.swaggertool.ui;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;

public class SwaggerApp
{
    private final String swaggerJson;
    private final int    port;
    
    public SwaggerApp(int port, String swaggerJson)
    {
        this.port = port;
        this.swaggerJson = swaggerJson;
    }
    
    public void start()
    {
        
        HttpHandler resHandler = Handlers.resource(new ClassPathResourceManager(SwaggerApp.class.getClassLoader(), "swaggerui"));
        PathHandler handler = Handlers.path()//
                .addPrefixPath("/", resHandler)//
                .addPrefixPath("/business", new HttpHandler() {
                    
                    @Override
                    public void handleRequest(HttpServerExchange exchange) throws Exception
                    {
                        exchange.getResponseSender().send(swaggerJson);
                    }
                });
        Undertow server = Undertow.builder() // Undertow builder
                .addHttpListener(port, "0.0.0.0") // Listener binding
                .setHandler(handler).build();
        server.start();
    }
    
}
