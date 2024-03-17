package aa.billing.repository;

import aa.billing.model.BillingCycleReport;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface BillingCycleReportRepository extends CrudRepository<BillingCycleReport, Long> {

    @Query("""
                    SELECT b FROM BillingCycleReport b ORDER BY b.cycleStart DESC
            """)
    List<BillingCycleReport> findAllOrdered();

}
