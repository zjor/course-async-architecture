# Async Architecture: UberPopug Inc. aTES

## Assumptions

Billing cycle is 5 minutes for the demonstration purposes

## Week 1

### Event Storming Diagram

![es](/docs/images/week-1-es.png)

### Domains

![domains](/docs/images/week-1-domains.png)

### Services

![services](/docs/images/week-1-services.png)

### Events

#### Sync commands
- BC for auth service
- BC for accounting service (e.g. get report)
- BC for task service (e.g. assign tasks, create task, change status)

#### Async commands
- CUD events for auth service (e.g. account created)
- BE task assigned (log balance reduction)
- BE report ready
- BE task done paid

### References

- [Kafka all in one](https://github.com/confluentinc/cp-all-in-one/)
