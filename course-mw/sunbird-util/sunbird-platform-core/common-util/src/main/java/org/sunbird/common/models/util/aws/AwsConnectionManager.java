package org.sunbird.common.models.util.aws;

import org.apache.commons.lang3.StringUtils;
import org.sunbird.cloud.storage.BaseStorageService;
import org.sunbird.cloud.storage.factory.StorageConfig;
import org.sunbird.cloud.storage.factory.StorageServiceFactory;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.models.util.PropertiesCache;
import static org.sunbird.common.models.util.JsonKey.AWS_STR;

public class AwsConnectionManager {
    private static String accountName = "";
    private static String accountKey = "";
    private static scala.Option<String> accountEndpoint = scala.Option.apply("");

    private static scala.Option<String> accountRegion = scala.Option.apply("");
    private static AwsConnectionManager connectionManager;
    private static BaseStorageService baseStorageService;

    static {
        String name = System.getenv(JsonKey.ACCOUNT_NAME);
        String key = System.getenv(JsonKey.ACCOUNT_KEY);
        scala.Option<String> endpoint = scala.Option.apply(System.getenv(JsonKey.ACCOUNT_ENDPOINT));
        scala.Option<String> region = scala.Option.apply("");
        if (StringUtils.isBlank(name) || StringUtils.isBlank(key)) {
            ProjectLogger.log(
                    "Aws account name and key is not provided by environment variable." + name + " " + key);
            accountName = PropertiesCache.getInstance().getProperty(JsonKey.ACCOUNT_NAME);
            accountKey = PropertiesCache.getInstance().getProperty(JsonKey.ACCOUNT_KEY);
            scala.Option<String> accountEndpoint = scala.Option.apply(System.getenv(JsonKey.ACCOUNT_ENDPOINT));
            scala.Option<String> accountRegion = scala.Option.apply("");
        } else {
            accountName = name;
            accountKey = key;
            accountEndpoint = endpoint;
            accountRegion= region;
            ProjectLogger.log(
                    "Aws account name and key is  provided by environment variable." + name + " " + key);
        }
    }

    private AwsConnectionManager() throws CloneNotSupportedException {
        if (connectionManager != null) throw new CloneNotSupportedException();
    }

    public static BaseStorageService getStorageService(){
        if(null == baseStorageService){
            baseStorageService = StorageServiceFactory.getStorageService(new StorageConfig(AWS_STR, accountKey, accountName,accountEndpoint,accountRegion));
            ProjectLogger.log(
                    "Aws account storage service with account name and key as " + accountName + " " + accountKey);
        }
        return  baseStorageService;
    }
}
