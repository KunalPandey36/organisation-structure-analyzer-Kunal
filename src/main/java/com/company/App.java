package com.company;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Organisation Structure Analyzer
 *
 * Assumptions:
 * 1) A manager should earn at least 20% more than the average salary of their direct reports.
 * 2) A manager should earn at most 50% more than the average salary of their direct reports.
 * 3) Only direct reports are considered (not the entire subtree).
 * 4) Reporting line depth counts the number of managers between an employee and the CEO.
 * 5) A reporting line longer than 4 is considered too long.
 * 6) The CSV file has a header row: Id,firstName,lastName,salary,managerId
 */
public class App {

    public static void main(String[] args) throws IOException {
        InputStream input = App.class.getResourceAsStream("/data.csv");
        if (input == null) {
            System.err.println("Error: data.csv not found in resources.");
            return;
        }

        CsvReader csvReader = new CsvReader();
        // Load employees from CSV in resources
        List<Employee> employees = csvReader.read(input);

        SalaryAnalyzer analyzer = new SalaryAnalyzer(employees);
        SalaryAnalyzer.SalaryResult salaryResult = analyzer.analyzeSalaryViolations();

        System.out.println("=== Managers earning below expected range ===");
        if (salaryResult.underpaid().isEmpty()) {
            System.out.println("None");
        } else {
            salaryResult.underpaid().forEach(System.out::println);
        }
        System.out.println("Total underpaid managers: " + salaryResult.underpaid().size());
        System.out.println();

        System.out.println("=== Managers earning above expected range ===");
        if (salaryResult.overpaid().isEmpty()) {
            System.out.println("None");
        } else {
            salaryResult.overpaid().forEach(System.out::println);
        }
        System.out.println("Total overpaid managers: " + salaryResult.overpaid().size());
        System.out.println();

        System.out.println("=== Employees with too long reporting lines ===");
        List<String> longLines = analyzer.findLongReportingLines();
        if (longLines.isEmpty()) {
            System.out.println("None");
        } else {
            longLines.forEach(System.out::println);
        }

        // print any data quality issues
        if (!analyzer.getWarnings().isEmpty()) {
            System.out.println();
            System.out.println("=== Warnings ===");
            analyzer.getWarnings().forEach(System.err::println);
        }
    }
}
