package uz.paymo.notifier.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uz.paymo.notifier.domain.MessageType;
import uz.paymo.notifier.domain.PartnerSystem;
import uz.paymo.notifier.dto.SlackRequest;
import uz.paymo.notifier.repository.MessageTypeRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rafatdin on 1/14/19.
 */
@Component
public class SlackHelper {
    private static final Logger LOG = LoggerFactory.getLogger(SlackHelper.class);

    @Autowired
    private MessageTypeRepository typeRepository;

    @Value("${url.to.log}")
    private String link;

    public String attachment(SlackRequest request, PartnerSystem partner) throws Exception {
        MessageType type;
        try {
            type = typeRepository.findFirstByName(request.getType());
        }catch (Exception e){
            LOG.error(e.getMessage());
            type = typeRepository.findFirstByName("error");
        }

        SlackAttachment attachment = new SlackAttachment(
                partner.getName(),
                request.getFieldValue(),
                request.getPretext(),
                type.getColor(),
                request.getUser(),
                request.getFieldTitle(),
                link);

        List<SlackAttachment> attachments = new ArrayList<>();
        attachments.add(attachment);

        ObjectMapper mapper = new ObjectMapper();
        JSONObject object = new JSONObject();
        object.put("attachments", attachments);

        String result = mapper.writeValueAsString(object);
        LOG.debug("Generated json to send to hook: " + result);
        return result;
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    class SlackAttachment{
        SlackAttachment(String partner, String text, String pretext, String color, String user, String title, String link){
            this.pretext= pretext;
            this.color= color;
            this.author_name= user;
            this.title= title;
            this.title_link= link;
            this.footer = partner;
            this.text = text;
        }
        @JsonProperty("pretext")
        private String pretext;
        @JsonProperty("color")
        private String color;
        @JsonProperty("author_name")
        private String author_name;
        @JsonProperty("title")
        private String title;
        @JsonProperty("title_link")
        private String title_link;
        @JsonProperty("footer")
        private String footer;
        @JsonProperty("text")
        private String text;
    }
}
