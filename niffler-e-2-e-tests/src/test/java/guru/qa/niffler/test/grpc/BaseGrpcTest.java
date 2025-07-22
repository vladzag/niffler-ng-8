package guru.qa.niffler.test.grpc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.grpc.NifflerCurrencyServiceGrpc;
import guru.qa.niffler.grpc.NifflerUserdataServiceGrpc;
import guru.qa.niffler.jupiter.annotation.meta.GrpcTest;
import guru.qa.niffler.utils.GrpcConsoleInterceptor;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;

@GrpcTest
public class BaseGrpcTest {

    protected static final Config CFG = Config.getInstance();

    protected static final Channel currencyChannel = ManagedChannelBuilder
            .forAddress(CFG.currencyGrpcAddress(), CFG.currencyGrpcPort())
            .intercept(new GrpcConsoleInterceptor())
            .usePlaintext()
            .build();

    protected static final Channel udChannel = ManagedChannelBuilder
            .forAddress(CFG.userdataGrpcAddress(), CFG.userdataGrpcPort())
            .intercept(new GrpcConsoleInterceptor())
            .usePlaintext()
            .build();

    protected static final NifflerCurrencyServiceGrpc.NifflerCurrencyServiceBlockingStub blockingStub
            = NifflerCurrencyServiceGrpc.newBlockingStub(currencyChannel);
    protected static final NifflerUserdataServiceGrpc.NifflerUserdataServiceBlockingStub userdataBlockingStub
            = NifflerUserdataServiceGrpc.newBlockingStub(udChannel);
}