package uz.paymo.notifier.domain;

import org.springframework.data.util.Pair;
import uz.paymo.notifier.dto.SystemsDto;
import uz.paymo.notifier.util.Encrypt;

import javax.persistence.*;

@Entity
@Table(name="partner")
public class PartnerSystem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String hook;
    private String logPath;
    private String user;
    private String host;
    private String password;
    private byte[] salt;

    protected PartnerSystem() {}

    public PartnerSystem(SystemsDto dto, String password, byte[] salt){
        this.id = dto.getId();
        this.name = dto.getName();
        this.hook = dto.getHook();
        this.logPath = dto.getPath();
        this.user = dto.getUser();
        this.host = dto.getHost();
        this.password = password;
        this.salt = salt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHook() {
        return hook;
    }

    public void setHook(String hook) {
        this.hook = hook;
    }

    public String getLogPath() {
        return logPath;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }
}
