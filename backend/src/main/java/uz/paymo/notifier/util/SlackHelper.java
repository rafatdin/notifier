package uz.paymo.notifier.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import uz.paymo.notifier.dto.SlackRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uz.paymo.notifier.domain.MessageType;
import uz.paymo.notifier.domain.PartnerSystem;
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
        AttachmentField field = new AttachmentField(request.getFieldTitle(), request.getFieldValue());
        MessageType type;
        try {
            type = typeRepository.findFirstByName(request.getType());
        }catch (Exception e){
            LOG.error(e.getMessage());
            type = typeRepository.findFirstByName("error");
        }

        String link = this.link;
        if(link.charAt(link.length()-1) == '/')
            link+="/";

        SlackAttachment attachment = new SlackAttachment(field,
                request.getPretext(),
                type.getColor(),
                request.getUser(),
                request.getTitle(),
                link+""+partner.getId());

        ObjectMapper mapper = new ObjectMapper();
        String result = mapper.writeValueAsString(attachment);
        LOG.debug("Generated json to send to hook: " + result);
        return result;
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    class SlackAttachment{
        SlackAttachment(AttachmentField field, String pretext, String color, String user, String title, String link){
            List<AttachmentField> fields = new ArrayList<>();
            fields.add(field);

            this.fields=fields;
            this.pretext= pretext;
            this.color= color;
            this.author_name= user;
            this.title= title;
            this.title_link= link;
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
        @JsonProperty("fields")
        private List<AttachmentField> fields;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    class AttachmentField{
        AttachmentField(){}
        AttachmentField(String title, String value){
            this.title = title;
            this.value = value;
            this.is_short = false;
        }
        @JsonProperty("title")
        private String title;
        @JsonProperty("value")
        private String value;
        @JsonProperty("short")
        private Boolean is_short;
    }
}
