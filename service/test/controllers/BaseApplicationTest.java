package controllers;

import static play.inject.Bindings.bind;

import java.io.File;
import java.util.List;

import actors.TestModule;
import modules.ActorStartModule;
import modules.ApplicationStart;
import modules.StartModule;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import play.Application;
import play.Mode;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.Helpers;
import util.ACTOR_NAMES;
import util.RequestInterceptor;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.management.*", "javax.net.ssl.*"})
@PrepareForTest({RequestInterceptor.class})
public abstract class BaseApplicationTest {
  protected Application application;

  public <T> void setup() {
    application =
        new GuiceApplicationBuilder()
                .disable(StartModule.class, ActorStartModule.class).bindings(new TestModule())
            .build();
    Helpers.start(application);
    PowerMockito.mockStatic(RequestInterceptor.class);
    PowerMockito.when(RequestInterceptor.verifyRequestData(Mockito.any())).thenReturn("userId");
  }

  public <T> void setup(List<ACTOR_NAMES> actors, Class actorClass) {
    GuiceApplicationBuilder applicationBuilder =
        new GuiceApplicationBuilder()
            .in(new File("path/to/app"))
            .in(Mode.TEST)
            .disable(StartModule.class);
    for (ACTOR_NAMES actor : actors) {
      applicationBuilder = applicationBuilder.overrides(bind(actor.getActorClass()).to(actorClass));
    }
    application = applicationBuilder.build();
    Helpers.start(application);
    PowerMockito.mockStatic(RequestInterceptor.class);
    PowerMockito.when(RequestInterceptor.verifyRequestData(Mockito.any())).thenReturn("userId");
  }
}
