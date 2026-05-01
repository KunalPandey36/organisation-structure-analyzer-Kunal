# Organisation Structure Analyzer

Reads employee data from a CSV file and reports:
- Managers who earn less than they should (below 20% above their direct reports' average)
- Managers who earn more than they should (above 50% above their direct reports' average)
- Employees with reporting lines longer than 4 managers to the CEO

## Assumptions

1. A manager's salary should be at least 20% more than the average salary of their direct reports.
2. A manager's salary should be at most 50% more than the average salary of their direct reports.
3. Only direct reports are considered, not the full subtree.
4. A reporting line is "too long" if there are more than 4 managers between the employee and the CEO.
5. The CSV has a header: `Id,firstName,lastName,salary,managerId`
6. An employee with no `managerId` is a CEO/root node.

## Requirements

- Java 21
- Maven 3.9+

## How to run

```bash
mvn clean compile exec:java
