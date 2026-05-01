package com.company;

public record Employee(int id, String firstName, String lastName, int salary, Integer managerId) {

    public String fullName() {
        return firstName + " " + lastName;
    }
}
