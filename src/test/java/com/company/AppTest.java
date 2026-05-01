package com.company;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class SalaryAnalyzerTest {

    @Test
    void underpaidManager_whenSalaryBelowMinimum() {
        List<Employee> employees = List.of(
                new Employee(1, "Alice", "Boss", 50000, null),
                new Employee(2, "Bob", "Worker", 45000, 1),
                new Employee(3, "Carol", "Worker", 47000, 1)
        );
        // Average subordinate salary = 46000, minimum = 46000 * 1.20 = 55200
        // Alice earns 50000 < 55200 → underpaid by 5200

        SalaryAnalyzer analyzer = new SalaryAnalyzer(employees);
        var result = analyzer.analyzeSalaryViolations();

        assertEquals(1, result.underpaid().size());
        assertTrue(result.underpaid().get(0).contains("Alice Boss"));
        assertTrue(result.underpaid().get(0).contains("5200"));
    }

    @Test
    void overpaidManager_whenSalaryAboveMaximum() {
        List<Employee> employees = List.of(
                new Employee(1, "Dave", "Rich", 80000, null),
                new Employee(2, "Eve", "Worker", 40000, 1),
                new Employee(3, "Frank", "Worker", 40000, 1)
        );
        // Average subordinate salary = 40000, maximum = 40000 * 1.50 = 60000
        // Dave earns 80000 > 60000 → overpaid by 20000

        SalaryAnalyzer analyzer = new SalaryAnalyzer(employees);
        var result = analyzer.analyzeSalaryViolations();

        assertEquals(1, result.overpaid().size());
        assertTrue(result.overpaid().get(0).contains("Dave Rich"));
        assertTrue(result.overpaid().get(0).contains("20000"));
    }

    @Test
    void managerWithinRange_notReported() {
        List<Employee> employees = List.of(
                new Employee(1, "Grace", "Fair", 55000, null),
                new Employee(2, "Heidi", "Worker", 40000, 1),
                new Employee(3, "Ivan", "Worker", 42000, 1)
        );
        // Average = 41000, minimum = 49200, maximum = 61500
        // Grace earns 55000 → within range

        SalaryAnalyzer analyzer = new SalaryAnalyzer(employees);
        var result = analyzer.analyzeSalaryViolations();

        assertTrue(result.underpaid().isEmpty());
        assertTrue(result.overpaid().isEmpty());
    }

    @Test
    void employeeWithNoSubordinates_notAManager() {
        List<Employee> employees = List.of(
                new Employee(1, "Judy", "Solo", 100000, null)
        );

        SalaryAnalyzer analyzer = new SalaryAnalyzer(employees);
        var result = analyzer.analyzeSalaryViolations();

        assertTrue(result.underpaid().isEmpty());
        assertTrue(result.overpaid().isEmpty());
    }

    @Test
    void reportingLineTooLong_whenMoreThan4Managers() {
        // Chain: CEO -> A -> B -> C -> D -> E (E has 5 managers above)
        List<Employee> employees = List.of(
                new Employee(1, "CEO", "Boss", 100000, null),
                new Employee(2, "A", "Mgr", 90000, 1),
                new Employee(3, "B", "Mgr", 80000, 2),
                new Employee(4, "C", "Mgr", 70000, 3),
                new Employee(5, "D", "Mgr", 60000, 4),
                new Employee(6, "E", "Worker", 50000, 5)
        );

        SalaryAnalyzer analyzer = new SalaryAnalyzer(employees);
        List<String> results = analyzer.findLongReportingLines();

        assertEquals(1, results.size());
        assertTrue(results.get(0).contains("E Worker"));
        assertTrue(results.get(0).contains("too long by 1"));
    }

    @Test
    void reportingLineWithin4_notReported() {
        // Chain: CEO -> A -> B -> C -> D (D has 4 managers — exactly the limit)
        List<Employee> employees = List.of(
                new Employee(1, "CEO", "Boss", 100000, null),
                new Employee(2, "A", "Mgr", 90000, 1),
                new Employee(3, "B", "Mgr", 80000, 2),
                new Employee(4, "C", "Mgr", 70000, 3),
                new Employee(5, "D", "Worker", 60000, 4)
        );

        SalaryAnalyzer analyzer = new SalaryAnalyzer(employees);
        List<String> results = analyzer.findLongReportingLines();

        assertTrue(results.isEmpty());
    }

    @Test
    void cycleInHierarchy_doesNotCrash() {
        // A -> B -> C -> A (cycle)
        List<Employee> employees = List.of(
                new Employee(1, "A", "One", 50000, 3),
                new Employee(2, "B", "Two", 40000, 1),
                new Employee(3, "C", "Three", 30000, 2)
        );

        SalaryAnalyzer analyzer = new SalaryAnalyzer(employees);
        List<String> results = analyzer.findLongReportingLines();

        // should not throw, and should log a warning
        assertFalse(analyzer.getWarnings().isEmpty());
        assertTrue(analyzer.getWarnings().stream().anyMatch(w -> w.contains("Cycle")));
    }

    @Test
    void unknownManagerId_producesWarning() {
        List<Employee> employees = List.of(
                new Employee(1, "Tom", "Lost", 50000, 999)
        );

        SalaryAnalyzer analyzer = new SalaryAnalyzer(employees);

        assertFalse(analyzer.getWarnings().isEmpty());
        assertTrue(analyzer.getWarnings().get(0).contains("unknown manager"));
    }
}
