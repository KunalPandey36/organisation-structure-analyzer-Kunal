package com.company;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AppTest {

    @Test
    void greetReturnsExpectedMessage() {
        App app = new App();
        assertEquals("Hello, World!", app.greet("World"));
    }

    @Test
    void greetWithEmptyName() {
        App app = new App();
        assertEquals("Hello, !", app.greet(""));
    }
}
