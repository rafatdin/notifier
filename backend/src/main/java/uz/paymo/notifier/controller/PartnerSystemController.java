package uz.paymo.notifier.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import uz.paymo.notifier.domain.PartnerSystem;
import uz.paymo.notifier.dto.SystemsDto;
import uz.paymo.notifier.repository.PartnerSystemRepository;
import uz.paymo.notifier.util.Encrypt;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/api")
public class PartnerSystemController {
    private static final Logger LOG = LoggerFactory.getLogger(PartnerSystemController.class);

    @Autowired
    private PartnerSystemRepository partnerRepo;

    @GetMapping("/partners")
    public ResponseEntity<List<SystemsDto>> getPartnerSystems(){
        List<SystemsDto> result = new ArrayList<>();
        partnerRepo.findAll().forEach(partnerSystem -> result.add(new SystemsDto(partnerSystem)));
        return ResponseEntity.ok(result);
    }

    @PostMapping("/partner")
    public ResponseEntity<?> create(@RequestBody SystemsDto partnerDto){
        try {
            Pair<String, byte[]> passAndSalt = Encrypt.encrypt(partnerDto.getPassword());
            PartnerSystem partner = new PartnerSystem(partnerDto, passAndSalt.getFirst(), passAndSalt.getSecond());
            partner = partnerRepo.save(partner);

            return ResponseEntity.ok(new SystemsDto(partner));
        } catch (GeneralSecurityException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
