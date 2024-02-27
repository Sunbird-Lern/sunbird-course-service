package controllers.cache;

import akka.actor.ActorRef;
import controllers.BaseController;
import org.sunbird.common.models.util.ActorOperations;
import org.sunbird.common.models.util.JsonKey;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.concurrent.CompletionStage;

public class CacheController extends BaseController {

  @Inject
  @Named("cache-management-actor")
  private ActorRef actorRef;

  public CompletionStage<Result> clearCache(String mapName, Http.Request httpRequest) {
    return handleRequest(
        actorRef, ActorOperations.CLEAR_CACHE.getValue(), mapName, JsonKey.MAP_NAME, httpRequest);
  }
}
