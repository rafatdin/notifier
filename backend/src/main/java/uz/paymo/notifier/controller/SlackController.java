package uz.paymo.notifier.controller;


import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import uz.paymo.notifier.domain.PartnerSystem;
import uz.paymo.notifier.repository.PartnerSystemRepository;
import uz.paymo.notifier.dto.SlackRequest;
import uz.paymo.notifier.util.ApiHelper;
import uz.paymo.notifier.util.JwtHelper;
import uz.paymo.notifier.util.SlackHelper;

@Controller
@RequestMapping("/api/slack")
public class SlackController {
    private static final Logger LOG = LoggerFactory.getLogger(SlackController.class);

    @Autowired
    private PartnerSystemRepository partnerRepo;

    @Autowired
    private SlackHelper slackHelper;

    @PostMapping(path="")
    public ResponseEntity getUserById(@RequestBody SlackRequest request, @RequestHeader("X-jwt-assertion") String xJWTHeader) {
        String responseError = "";

        //1. Identify partner
        String partnerName = JwtHelper.getPartnerName(xJWTHeader);
        PartnerSystem partner = partnerRepo.findFirstByName(partnerName);
        if(partner == null)
            return ResponseEntity.badRequest().body("{\"result\":\"FAIL\",\"message\":\"Partner is not found\"}");


        try {
            //2. Generate json to send to slack
            String json = slackHelper.attachment(request, partner);

            //3. Send request to slack
            ResponseEntity<String> response = ApiHelper.sendRequest(partner.getHook(), json);

            //4. Process response
            if(response.getStatusCode().equals(HttpStatus.OK))
                return ResponseEntity.ok("{\"result\":\"OK\",\"message\":\"Message successfully sent\"}");
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error(e.getMessage());
            responseError = e.getMessage();
        }
        return ResponseEntity.badRequest().body("{\"result\":\"FAIL\",\"message\":\"Internal error: "+responseError+"\"}");
    }

}
