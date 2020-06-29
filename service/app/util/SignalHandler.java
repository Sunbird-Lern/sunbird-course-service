package util;

import akka.actor.ActorSystem;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import play.api.Application;
import play.api.Play;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;
import sun.misc.Signal;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;

@Singleton
public class SignalHandler {

    Logger logger = Logger.getLogger(SignalHandler.class);
    private String stopDelay = System.getenv("sigterm_stop_delay");
    private static FiniteDuration STOP_DELAY = null;

    private volatile boolean isShuttingDown = false;

    @Inject
    public SignalHandler(ActorSystem actorSystem, Provider<Application> applicationProvider) {
        long delay = 40;
        if (StringUtils.isNotBlank(stopDelay)) {
            delay = Long.parseLong(stopDelay);
        }
        STOP_DELAY = Duration.create(delay, TimeUnit.SECONDS);
        Signal.handle(
                new Signal("TERM"),
                signal -> {
                    isShuttingDown = true;
                    logger.info("Termination required, swallowing SIGTERM to allow current requests to finish");
                    actorSystem
                            .scheduler()
                            .scheduleOnce(
                                    STOP_DELAY,
                                    () -> {
                                        Play.stop(applicationProvider.get());
                                    },
                                    actorSystem.dispatcher());
                });
    }

    public boolean isShuttingDown() {
        return isShuttingDown;
    }
}