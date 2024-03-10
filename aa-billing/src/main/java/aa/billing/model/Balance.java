package aa.billing.model;


import aa.common.model.Born;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "billing_balances")
public class Balance extends Born {

    @ManyToOne
    @JoinColumn(name = "account_id", unique = true, nullable = false)
    private Account account;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

}
