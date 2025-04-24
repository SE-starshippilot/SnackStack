package com.snackstack.server;
import com.snackstack.server.config.Bootstrap;
import spark.Spark;
import static spark.Spark.port;
public class Server
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        port(8080);
        Bootstrap.init();
        Spark.awaitInitialization();
        System.out.printf("✅  Server started on port %d%n", Spark.port());
        Runtime.getRuntime().addShutdownHook(new Thread(Spark::stop));
    }
}
