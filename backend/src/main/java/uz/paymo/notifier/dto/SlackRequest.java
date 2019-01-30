package uz.paymo.notifier.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Created by rafatdin on 1/14/19.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SlackRequest {
    @JsonProperty("title")
    private String pretext;

    @JsonProperty("type")
    private String type;

    @JsonProperty("user")
    private String user;

    @JsonProperty("module")
    private String title;

    @JsonProperty("class")
    private String fieldTitle;

    @JsonProperty("exception")
    private String fieldValue;

    public String getPretext() {
        return pretext;
    }

    public void setPretext(String pretext) {
        this.pretext = pretext;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFieldTitle() {
        return fieldTitle;
    }

    public void setFieldTitle(String fieldTitle) {
        this.fieldTitle = fieldTitle;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }
}
