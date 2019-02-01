package uz.paymo.notifier.repository;


import org.springframework.data.repository.CrudRepository;
import uz.paymo.notifier.domain.MessageType;

public interface MessageTypeRepository extends CrudRepository<MessageType, Integer> {
    MessageType findFirstByName(String name);
}
