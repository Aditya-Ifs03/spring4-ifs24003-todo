package org.delcom.starter.controllers;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Base64;

public class HomeControllerTest {

    HomeController controller = new HomeController();

    @Test
    void testHello() {
        String result = controller.hello();
        assertTrue(result.contains("Spring Boot"));
    }

    @Test
    void testSayHello() {
        String result = controller.sayHello("Karina");
        assertEquals("Hello, Karina!", result);
    }

    @Test
    void testInformasiNim() {
        String result = controller.informasiNim("IF24003");
        assertTrue(result.contains("IF24003"));
    }

    @Test
    void testPerolehanNilai() {
        String encoded = Base64.getEncoder().encodeToString("95".getBytes());
        String result = controller.perolehanNilai(encoded);
        assertTrue(result.contains("95"));
    }

    @Test
    void testPerbedaanL() {
        String encoded = Base64.getEncoder().encodeToString("Java".getBytes());
        String result = controller.perbedaanL(encoded);
        assertTrue(result.contains("avaJ"));
    }

    @Test
    void testPalingTer() {
        String encoded = Base64.getEncoder().encodeToString("hello".getBytes());
        String result = controller.palingTer(encoded);
        assertTrue(result.contains("HELLO"));
    }
}
