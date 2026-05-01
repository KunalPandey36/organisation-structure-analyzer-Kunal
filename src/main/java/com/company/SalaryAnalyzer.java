package com.company;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SalaryAnalyzer {

    private static final double MIN_ABOVE_AVG = 1.20;
    private static final double MAX_ABOVE_AVG = 1.50;
    private static final int MAX_DEPTH = 4;

    private final Map<Integer, Employee> employeeById = new HashMap<>();
    private final Map<Integer, List<Employee>> reports = new HashMap<>();
    private final Map<Integer, Integer> depthCache = new HashMap<>();
    private final List<String> warnings = new ArrayList<>();

    public SalaryAnalyzer(List<Employee> employees) {
        // first pass: index everyone
        for (Employee e : employees) {
            employeeById.put(e.id(), e);
        }

        // second pass: build reports map + validate references
        for (Employee e : employees) {
            if (e.managerId() != null) {
                if (!employeeById.containsKey(e.managerId())) {
                    warnings.add(e.fullName() + " references unknown manager ID " + e.managerId());
                    continue;
                }
                reports.computeIfAbsent(e.managerId(), k -> new ArrayList<>()).add(e);
            }
        }
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public record SalaryResult(List<String> underpaid, List<String> overpaid) {}

    public SalaryResult analyzeSalaryViolations() {
        List<String> underpaid = new ArrayList<>();
        List<String> overpaid = new ArrayList<>();

        for (var entry : reports.entrySet()) {
            Employee mgr = employeeById.get(entry.getKey());
            if (mgr == null) {
                warnings.add("Manager ID " + entry.getKey() + " found in reports but missing from employee list");
                continue;
            }

            long avg = averageSalary(entry.getValue());
            long minExpected = Math.round(avg * MIN_ABOVE_AVG);
            long maxExpected = Math.round(avg * MAX_ABOVE_AVG);

            if (mgr.salary() < minExpected) {
                long diff = minExpected - mgr.salary();
                underpaid.add(mgr.fullName() + " earns " + diff + " less than expected");
            } else if (mgr.salary() > maxExpected) {
                long diff = mgr.salary() - maxExpected;
                overpaid.add(mgr.fullName() + " earns " + diff + " more than expected");
            }
        }
        return new SalaryResult(underpaid, overpaid);
    }

    public List<String> findLongReportingLines() {
        List<String> results = new ArrayList<>();
        for (Employee e : employeeById.values()) {
            int depth = getDepth(e.id(), new HashSet<>());
            if (depth < 0) {
                // cycle detected, already logged
                continue;
            }
            if (depth > MAX_DEPTH) {
                results.add(e.fullName() + " has reporting line too long by " + (depth - MAX_DEPTH));
            }
        }
        return results;
    }

    /**
     * Returns depth (number of managers above), or -1 if a cycle is detected.
     */
    private int getDepth(int employeeId, Set<Integer> visited) {
        if (depthCache.containsKey(employeeId)) {
            return depthCache.get(employeeId);
        }
        if (visited.contains(employeeId)) {
            warnings.add("Cycle detected involving employee ID " + employeeId);
            return -1;
        }
        visited.add(employeeId);

        Employee emp = employeeById.get(employeeId);
        if (emp == null || emp.managerId() == null) {
            depthCache.put(employeeId, 0);
            return 0;
        }
        if (!employeeById.containsKey(emp.managerId())) {
            warnings.add(emp.fullName() + " references unknown manager ID " + emp.managerId());
            // dangling reference, treat as root
            depthCache.put(employeeId, 0);
            return 0;
        }

        int parentDepth = getDepth(emp.managerId(), visited);
        if (parentDepth < 0) {
            return -1; // propagate cycle
        }
        int depth = 1 + parentDepth;
        depthCache.put(employeeId, depth);
        return depth;
    }

    private long averageSalary(List<Employee> employees) {
        long total = 0;
        for (Employee e : employees) {
            total += e.salary();
        }
        return total / employees.size();
    }
}
