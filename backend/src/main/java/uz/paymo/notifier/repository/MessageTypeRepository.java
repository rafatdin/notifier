package uz.paymo.notifier.repository;


import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import uz.paymo.notifier.domain.MessageType;
import uz.paymo.notifier.domain.User;

import java.util.List;

public interface MessageTypeRepository extends CrudRepository<MessageType, Integer> {
    MessageType findFirstByName(String name);
}
