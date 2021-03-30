import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;
import io.grpc.ServerServiceDefinition;

import java.io.IOException;

public class SensorServer {

    private Server server;

    public static void main(String[] args) throws IOException, InterruptedException {
        SensorServer employeeServer = new SensorServer();
        employeeServer.start();
    }

    private void start() throws IOException, InterruptedException {
        final int port = 9000;

        Service temperatureService = new Service();

        final ServerServiceDefinition serverServiceDefinition = ServerInterceptors.interceptForward(temperatureService, new HeaderServerInterceptor());

        server = ServerBuilder.forPort(port)
                .addService(serverServiceDefinition)
                .build()
                .start();

        System.out.println("Listening on port " + port);

        server.awaitTermination();
    }
}
