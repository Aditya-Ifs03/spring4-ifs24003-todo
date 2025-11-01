package org.delcom.starter.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Base64;

@SpringBootTest
class HomeControllerTest {

    private final HomeController controller = new HomeController();

    // ==============================
    // 0️⃣ Tes Halaman Utama
    // ==============================
    @Test
    @DisplayName("hello() harus menampilkan pesan sambutan default")
    void testHello() {
        String result = controller.hello();
        assertTrue(result.contains("Spring Boot"));
    }

    // ==============================
    // 1️⃣ Tes Informasi NIM
    // ==============================
    @Test
    @DisplayName("informasiNim() harus menampilkan program studi dan angkatan dengan benar")
    void testInformasiNim() {
        String result = controller.informasiNim("11S24xxx");
        assertTrue(result.contains("Sarjana Informatika"));
        assertTrue(result.contains("Angkatan: 2024"));
    }

    // ==============================
    // 2️⃣ Tes Perolehan Nilai
    // ==============================
    @Test
    @DisplayName("perolehanNilai() harus mendekode Base64 dan menghitung nilai akhir dengan benar")
    void testPerolehanNilai() {
        // format input:
        // bobot -> pa t k p uts uas
        // data nilai -> tipe|max|score
        String input = """
                10 20 10 20 20 20
                PA|10|8
                T|20|15
                K|10|10
                P|20|15
                UTS|20|10
                UAS|20|18
                ---
                """;
        String encoded = Base64.getEncoder().encodeToString(input.getBytes());
        String result = controller.perolehanNilai(encoded);

        assertTrue(result.contains("Perolehan Nilai:"));
        assertTrue(result.contains("Nilai Akhir"));
        assertTrue(result.contains("Grade"));
    }

    // ==============================
    // 3️⃣ Tes Perbedaan L dan Kebalikannya
    // ==============================
    @Test
    @DisplayName("perbedaanL() harus menghitung L dan kebalikannya dengan benar")
    void testPerbedaanL() {
        // Matriks 3x3
        String input = """
                3
                1 2 3
                4 5 6
                7 8 9
                """;
        String encoded = Base64.getEncoder().encodeToString(input.getBytes());
        String result = controller.perbedaanL(encoded);

        assertTrue(result.contains("Nilai L"));
        assertTrue(result.contains("Nilai Kebalikan L"));
        assertTrue(result.contains("Perbedaan"));
        assertTrue(result.contains("Dominan"));
    }

    // ==============================
    // 4️⃣ Tes Paling Ter
    // ==============================
    @Test
    @DisplayName("palingTer() harus menemukan nilai tertinggi, terendah, terbanyak, dan tersedikit")
    void testPalingTer() {
        String input = """
                3
                7
                3
                7
                8
                9
                3
                ---
                """;
        String encoded = Base64.getEncoder().encodeToString(input.getBytes());
        String result = controller.palingTer(encoded);

        assertTrue(result.contains("Tertinggi"));
        assertTrue(result.contains("Terendah"));
        assertTrue(result.contains("Terbanyak"));
        assertTrue(result.contains("Tersedikit"));
    }
}
