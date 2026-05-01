package com.company;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

class CsvReaderTest {

    @Test
    void parsesValidCsv() throws IOException {
        String csv = """
                Id,firstName,lastName,salary,managerId
                1,Joe,Doe,60000,
                2,Martin,Chekov,45000,1
                """;
        InputStream input = new ByteArrayInputStream(csv.getBytes());

        CsvReader reader = new CsvReader();
        List<Employee> employees = reader.read(input);

        assertEquals(2, employees.size());
        assertEquals("Joe", employees.get(0).firstName());
        assertNull(employees.get(0).managerId());
        assertEquals(1, employees.get(1).managerId());
    }

    @Test
    void handlesEmptyFile() throws IOException {
        String csv = "Id,firstName,lastName,salary,managerId\n";
        InputStream input = new ByteArrayInputStream(csv.getBytes());

        CsvReader reader = new CsvReader();
        List<Employee> employees = reader.read(input);

        assertTrue(employees.isEmpty());
    }
}
