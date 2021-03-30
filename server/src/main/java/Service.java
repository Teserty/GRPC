import com.mera.server.grpc.Messages;
import com.mera.server.grpc.TemperatureServiceGrpc;
import io.grpc.stub.StreamObserver;

public class Service extends TemperatureServiceGrpc.TemperatureServiceImplBase {
    private TemperatureSensors TemperatureSensors = new TemperatureSensors();

    @Override
    public void getRecordById(Messages.GetByIdRequest request, StreamObserver<Messages.TemperatureResponse> responseObserver) {
        for (Messages.TemperatureSensor temperatureRecordResponse : TemperatureSensors) {
            if (temperatureRecordResponse.getId() == request.getId()) {
                final Messages.TemperatureResponse temperatureResponse = Messages.TemperatureResponse
                        .newBuilder()
                        .setTemperatureSensor(TemperatureSensors.get(request.getId()))
                        .build();
                responseObserver.onNext(temperatureResponse);
                responseObserver.onCompleted();
                return;
            }
        }
        responseObserver.onError(new Exception("TemperatureSensor not found with id " + request.getId()));
    }

    @Override
    public void getAllByDeviceId(Messages.GetAllByDeviceIdRequest request, StreamObserver<Messages.TemperatureResponse> responseObserver) {
        for (Messages.TemperatureSensor sensor : TemperatureSensors) {
            final Messages.TemperatureResponse response = Messages.TemperatureResponse.newBuilder()
                    .setTemperatureSensor(sensor)
                    .build();
            responseObserver.onNext(response);
        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<Messages.TemperatureRequest> save(final StreamObserver<Messages.TemperatureResponse> responseObserver) {
        return getTemperatureRequestStreamObserver(responseObserver);
    }

    @Override
    public StreamObserver<Messages.TemperatureRequest> saveAll(final StreamObserver<Messages.TemperatureResponse> responseObserver) {
        return getTemperatureRequestStreamObserver(responseObserver);
    }

    private StreamObserver<Messages.TemperatureRequest> getTemperatureRequestStreamObserver(final StreamObserver<Messages.TemperatureResponse> responseObserver) {
        return new StreamObserver<Messages.TemperatureRequest>() {
            @Override
            public void onNext(Messages.TemperatureRequest value) {
                TemperatureSensors.add(value.getTemperatureSensor());
                responseObserver.onNext(
                        Messages.TemperatureResponse.newBuilder().setTemperatureSensor(value.getTemperatureSensor()).build()
                );
            }

            @Override
            public void onError(Throwable t) {
                System.out.println(t);
            }

            @Override
            public void onCompleted() {
                System.out.println("onCompleted");
                for (Messages.TemperatureSensor temperatureSensor : TemperatureSensors) {
                    System.out.println(temperatureSensor);
                }
                responseObserver.onCompleted();
            }
        };
    }

}
