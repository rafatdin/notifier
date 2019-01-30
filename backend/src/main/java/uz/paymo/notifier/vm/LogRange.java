package uz.paymo.notifier.vm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by rafatdin on 1/24/19.
 */
@JsonIgnoreProperties
public class LogRange {
    @JsonProperty
    private Integer from;
    @JsonProperty
    private Integer to;

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public Integer getTo() {
        return to;
    }

    public void setTo(Integer to) {
        this.to = to;
    }
}
