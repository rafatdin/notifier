package uz.paymo.notifier.util;

import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.security.jwt.Jwt;

import java.util.Map;

/**
 * Created by rafatdin on 1/14/19.
 */
public class JwtHelper {
    private static final Logger LOG = LoggerFactory.getLogger(JwtHelper.class);

    final static String ENV_TYPE = "http://wso2.org/claims/keytype";
    final static String SANDBOX = "SANDBOX";
    final static String PROD = "PRODUCTION";
    final static String xJwtKey = "application_name";

    public static String getPartnerName(String xJwtHeader){
        Jwt jwt = org.springframework.security.jwt.JwtHelper.decode(xJwtHeader);
        JsonParser parser = JsonParserFactory.getJsonParser();
        Map<String, ?> tokenData = parser.parseMap(jwt.getClaims());
        String partnerName = String.valueOf(tokenData.get(String.valueOf(xJwtKey)));
        Boolean sandbox = String.valueOf(tokenData.get(ENV_TYPE)).equals(SANDBOX);
        if(sandbox)
            partnerName += "_"+SANDBOX;
        else
            partnerName += "_"+PROD;

        LOG.debug(partnerName);
        return partnerName;
    }
}
