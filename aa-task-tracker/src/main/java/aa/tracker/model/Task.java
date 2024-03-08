package aa.tracker.model;

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
@Table(name = "tm_tasks")
public class Task extends Born {

    @ManyToOne
    @JoinColumn(name = "assignee_id", nullable = false)
    private Account assignee;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "reward", nullable = false)
    private BigDecimal reward;

    @Column(name = "assignment_fee", nullable = false)
    private BigDecimal assignmentFee;

    @Column(name = "is_done", nullable = false)
    private boolean done = false;

}
