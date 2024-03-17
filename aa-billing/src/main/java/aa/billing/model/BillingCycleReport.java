package aa.billing.model;

import aa.common.model.Model;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "analytics_billing_cycle_report")
public class BillingCycleReport extends Model {

    @Column(name = "cycle_start", unique = true, nullable = false)
    private Instant cycleStart;

    /**
     * Stores balance (earnings of the top management) per billing cycle
     */
    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    /**
     * Stores the most expensive closed task cost per billing period
     */
    @Column(name = "most_expensive_task", nullable = false)
    private BigDecimal mostExpensiveTask;

}
