package dk.dtu;

import io.quarkus.runtime.Shutdown;
import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LifecycleBean {

    @Startup
    public void start() {
        System.out.println("EventPublisher started");
    }

    @Shutdown
    public void stop() {
        System.out.println("EventPublisher stopped");
    }
}
