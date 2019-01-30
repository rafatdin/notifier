package uz.paymo.notifier.controller;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jcraft.jsch.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.codehaus.jackson.map.util.JSONPObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import uz.paymo.notifier.domain.PartnerSystem;
import uz.paymo.notifier.domain.User;
import uz.paymo.notifier.dto.SlackRequest;
import uz.paymo.notifier.dto.SystemsDto;
import uz.paymo.notifier.repository.PartnerSystemRepository;
import uz.paymo.notifier.util.ApiHelper;
import uz.paymo.notifier.util.Encrypt;
import uz.paymo.notifier.util.JwtHelper;
import uz.paymo.notifier.util.SlackHelper;
import uz.paymo.notifier.vm.LogRange;

import java.io.*;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
public class LogController {
    private static final Logger LOG = LoggerFactory.getLogger(LogController.class);


    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private PartnerSystemRepository partnerRepo;

    @MessageMapping("log/update/{id}")
    public void updateLogForSystem(@DestinationVariable("id") Integer id){
        PartnerSystem partner = partnerRepo.findById(id).orElse(null);
        LOG.info("System id " + id + " from database.");

        Session session = null;
        try {
            //1. prepare session
            session = prepareSession(partner.getUser(), partner.getHost(), Encrypt.decrypt(partner));
            session.connect();

            //2. open SFTP channel
            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftp = (ChannelSftp) channel;

            //3. read lines and send to subscribers of broadcas
            updateLog(sftp, partner);
        } catch (JSchException | GeneralSecurityException | IOException e) {
            e.printStackTrace();
        } finally {
            if (session != null) session.disconnect();
        }
    }

    @MessageMapping("log/{id}")
    public void getLogBySystemId(@DestinationVariable("id") Integer id, @RequestBody LogRange range) {
        PartnerSystem partner = partnerRepo.findById(id).orElse(null);

        //send params to subscribers of web socket
        send(partner, range);
    }

    private void sendLinesToTopic(String line, String topic) {
        this.simpMessagingTemplate.convertAndSend(topic, line);
    }

    private Session prepareSession(String username, String host, String password) throws JSchException {
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");

        JSch jsch = new JSch();
        Session session = jsch.getSession(username, host, 22);
        session.setPassword(password);
        session.setConfig(config);

        return session;
    }

    private void send(PartnerSystem partner, LogRange range){
        File targetFile = new File("src/main/resources/" + partner.getName() + ".tmp");
        ReversedLinesFileReader fr = null;
        try {
            fr = new ReversedLinesFileReader(targetFile);

            Integer lineCount = -1;
            String line;
            while ((line = fr.readLine()) != null) {
                ++lineCount;
                if(lineCount > range.getTo())
                    break;

                if(lineCount >= range.getFrom())
                    sendLinesToTopic(line, "/broadcast/log/"+partner.getId());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateLog(ChannelSftp sftp, PartnerSystem partner){
        try (InputStream stream = sftp.get(partner.getLogPath())) {
            File targetFile = new File("src/main/resources/" + partner.getName() + ".tmp");
            FileUtils.copyInputStreamToFile(stream, targetFile);
        } catch (IOException | SftpException e) {
            e.printStackTrace();
        } finally {
            sftp.exit();
        }
    }

}
