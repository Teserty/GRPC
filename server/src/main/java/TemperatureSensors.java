import com.mera.server.grpc.Messages;

import java.util.ArrayList;

public class TemperatureSensors extends ArrayList<Messages.TemperatureSensor> {
    private static Messages.TemperatureSensor temperatureSensor;
    public TemperatureSensors(){
        for (int i = 0; i < 1000; i++)
        this.add( Messages.TemperatureSensor
                .newBuilder()
                .setId(i)
                .setTemperature(42+i%3)
                .setType("C")
                .build());
    }
}
