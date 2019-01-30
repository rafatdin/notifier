package uz.paymo.notifier.repository;


import org.springframework.data.repository.CrudRepository;
import uz.paymo.notifier.domain.PartnerSystem;

public interface PartnerSystemRepository extends CrudRepository<PartnerSystem, Integer> {
    PartnerSystem findFirstByName(String name);
}
