package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CsvReader {

    public List<Employee> read(Path filePath) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            return parseLines(reader);
        }
    }

    public List<Employee> read(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return parseLines(reader);
        }
    }

    private List<Employee> parseLines(BufferedReader reader) throws IOException {
        List<Employee> employees = new ArrayList<>();
        String line = reader.readLine(); // skip header
        while ((line = reader.readLine()) != null) {
            Employee employee = parseLine(line);
            if (employee != null) {
                employees.add(employee);
            }
        }
        return employees;
    }

    private Employee parseLine(String line) {
        String[] parts = line.split(",");
        if (parts.length < 4) {
            return null;
        }
        int id = Integer.parseInt(parts[0].trim());
        String firstName = parts[1].trim();
        String lastName = parts[2].trim();
        int salary = Integer.parseInt(parts[3].trim());
        Integer managerId = parts.length > 4 && !parts[4].trim().isEmpty()
                ? Integer.parseInt(parts[4].trim())
                : null;
        return new Employee(id, firstName, lastName, salary, managerId);
    }
}
