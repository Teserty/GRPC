import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import com.mera.client.grpc.*;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class ClientService {
    public static void main(String[] args) throws IOException, InterruptedException {
        final ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9000)
                .usePlaintext()
                .build();
//блокирующий клиент
        final TemperatureServiceGrpc.TemperatureServiceBlockingStub blockingClient
                = TemperatureServiceGrpc.newBlockingStub(channel);

//        неблокир. клиет
        final TemperatureServiceGrpc.TemperatureServiceStub nonBlockingClient =
                TemperatureServiceGrpc.newStub(channel);

        Scanner sc = new Scanner(System.in);
        switch (sc.nextInt()) {
            case 1:
                GetRecordById(blockingClient);
                break;
            case 2:
                GetAllByDeviceId(blockingClient);
                break;
            case 3:
                saveAll(nonBlockingClient);
                break;
            case 4:
                save(nonBlockingClient);
                break;
        }

        Thread.sleep(1000);
        channel.shutdown();
        channel.awaitTermination(1, TimeUnit.SECONDS);
    }

    private static void save(TemperatureServiceGrpc.TemperatureServiceStub nonBlockingClient) {
        final Messages.TemperatureSensor sensor = Messages.TemperatureSensor.newBuilder()
                .setId(10)
                .setType("C")
                .setTemperature(55)
                .setId(20)
                .build();
        final StreamObserver<Messages.TemperatureRequest> recordResponse = nonBlockingClient.save(new StreamObserver<Messages.TemperatureResponse>() {
            @Override
            public void onNext(Messages.TemperatureResponse value) {
                System.out.println();
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                System.out.println("onCompleted");
            }
        });
        final Messages.TemperatureRequest employeeRequest = Messages.TemperatureRequest.newBuilder()
                .setTemperatureSensor(sensor)
                .build();
        recordResponse.onNext(employeeRequest);
    }

    private static void saveAll(TemperatureServiceGrpc.TemperatureServiceStub nonBlockingClient) throws InterruptedException {
        List<Messages.TemperatureSensor> temperatureSensors = new ArrayList<Messages.TemperatureSensor>();
        temperatureSensors.add(Messages.TemperatureSensor.newBuilder()
                .setId(555)
                .setTemperature(40)
                .setType("C")
                .build());

        temperatureSensors.add(Messages.TemperatureSensor.newBuilder()
                .setId(6666)
                .setTemperature(5)
                .setType("C")
                .build());


        final StreamObserver<Messages.TemperatureRequest> stream = nonBlockingClient.saveAll(new StreamObserver<Messages.TemperatureResponse>()  {
            @Override
            public void onNext(Messages.TemperatureResponse value) {
                System.out.println();
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                System.out.println("onCompleted");
            }
        });

        for (Messages.TemperatureSensor temperatureSensor : temperatureSensors) {
            final Messages.TemperatureRequest employeeRequest = Messages.TemperatureRequest.newBuilder()
                    .setTemperatureSensor(temperatureSensor)
                    .build();
            stream.onNext(employeeRequest);
            Thread.sleep(1000);
        }

        stream.onCompleted();
    }


    private static void GetAllByDeviceId(TemperatureServiceGrpc.TemperatureServiceBlockingStub blockingClient) {
        final Iterator<Messages.TemperatureResponse> temperatureRecordResponseIterator =
                blockingClient.getAllByDeviceId(Messages.GetAllByDeviceIdRequest.newBuilder().build());

        while (temperatureRecordResponseIterator.hasNext()) {
            System.out.println(temperatureRecordResponseIterator.next().getTemperatureSensor());
        }
    }

    private static void GetRecordById(TemperatureServiceGrpc.TemperatureServiceBlockingStub blockingClient) {
        final Messages.TemperatureResponse temperatureRecordResponse = blockingClient.getRecordById(Messages.GetByIdRequest.newBuilder()
                .setId(500)
                .build());
        System.out.println(temperatureRecordResponse.getTemperatureSensor());
    }


}
