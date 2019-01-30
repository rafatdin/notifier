package uz.paymo.notifier.domain;

import javax.persistence.*;

@Entity
@Table(name="message_type")
public class MessageType {

    @Id
    private Integer id;
    private String name;
    private String color;

    protected MessageType() {}

    @Override
    public String toString() {
        return String.format(
                "MessageType[id=%d, name='%s', color='%s']",
                id, name, color);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
