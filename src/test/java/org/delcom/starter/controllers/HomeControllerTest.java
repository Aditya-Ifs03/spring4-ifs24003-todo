package org.delcom.starter.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HomeControllerTest {

    private final HomeController controller = new HomeController();

    // ✅ Tes untuk hello()
    @Test
    @DisplayName("hello() menampilkan pesan sambutan dinamis")
    void testHello() {
        String result = controller.hello();
        assertTrue(result.contains("Spring Boot"));
    }

    // ✅ Tes Informasi NIM
    @Test
    @DisplayName("informasiNim() menampilkan data NIM yang valid")
    void testInformasiNimValid() {
        String encodedNim = Base64.getEncoder().encodeToString("IFS24003".getBytes());
        String result = controller.informasiNim(encodedNim);
        assertTrue(result.contains("Informatika"));
        assertTrue(result.contains("Angkatan"));
        assertTrue(result.contains("Urutan"));
    }

    @Test
    @DisplayName("informasiNim() menangani input kosong")
    void testInformasiNimKosong() {
        String result = controller.informasiNim("");
        assertEquals("NIM tidak boleh kosong.", result);
    }

    @Test
    @DisplayName("informasiNim() menangani input null")
    void testInformasiNimNull() {
        String result = controller.informasiNim(null);
        assertEquals("NIM tidak boleh kosong.", result);
    }

    @Test
    @DisplayName("informasiNim() menampilkan Tidak diketahui untuk NIM non-IFS")
    void testInformasiNimTidakDiketahui() {
        String encodedNim = Base64.getEncoder().encodeToString("ABC24003".getBytes());
        String result = controller.informasiNim(encodedNim);
        assertTrue(result.contains("Tidak diketahui"));
    }

    @Test
    @DisplayName("informasiNim() menangani Base64 tidak valid")
    void testInformasiNimBase64Invalid() {
        String result = controller.informasiNim("###invalid###");
        assertEquals("Input Base64 tidak valid.", result);
    }

    // ✅ Tes Perolehan Nilai
    @Test
    @DisplayName("perolehanNilai() menghitung nilai dan grade dengan benar")
    void testPerolehanNilaiValid() {
        String encoded = Base64.getEncoder().encodeToString("80,75,90".getBytes());
        String result = controller.perolehanNilai(encoded);
        assertTrue(result.contains("Grade: A") || result.contains("Grade: B"));
        assertTrue(result.contains("Nilai Tertinggi"));
        assertTrue(result.contains("Rata-rata"));
    }

    @Test
    @DisplayName("perolehanNilai() menangani input kosong")
    void testPerolehanNilaiKosong() {
        String result = controller.perolehanNilai("");
        assertEquals("Data nilai tidak boleh kosong.", result);
    }

    @Test
    @DisplayName("perolehanNilai() menangani input tidak lengkap")
    void testPerolehanNilaiTidakLengkap() {
        String encoded = Base64.getEncoder().encodeToString("80,75".getBytes());
        String result = controller.perolehanNilai(encoded);
        assertEquals("Data nilai tidak lengkap. Harus berisi minimal 3 nilai.", result);
    }

    @Test
    @DisplayName("perolehanNilai() menangani format nilai tidak valid")
    void testPerolehanNilaiTidakValid() {
        String encoded = Base64.getEncoder().encodeToString("a,b,c".getBytes());
        String result = controller.perolehanNilai(encoded);
        assertTrue(result.contains("Format nilai tidak valid"));
    }

    @Test
    @DisplayName("perolehanNilai() menangani Base64 tidak valid")
    void testPerolehanNilaiBase64Invalid() {
        String result = controller.perolehanNilai("$$$invalid$$$");
        assertEquals("Input Base64 tidak valid.", result);
    }

    // ✅ Tes Perbedaan L
    @Test
    @DisplayName("perbedaanL() menghitung jumlah huruf L/l dan Non-L serta kebalikan teks")
    void testPerbedaanLValid() {
        String encoded = Base64.getEncoder().encodeToString("Lalala".getBytes());
        String result = controller.perbedaanL(encoded);
        assertTrue(result.contains("Jumlah huruf L/l"));
        assertTrue(result.contains("Kebalikan"));
        assertTrue(result.contains("Apakah sama?"));
    }

    @Test
    @DisplayName("perbedaanL() menangani input kosong")
    void testPerbedaanLKosong() {
        String result = controller.perbedaanL("");
        assertEquals("Input tidak boleh kosong.", result);
    }

    @Test
    @DisplayName("perbedaanL() menangani Base64 tidak valid")
    void testPerbedaanLBase64Invalid() {
        String result = controller.perbedaanL("##invalid##");
        assertEquals("Input Base64 tidak valid.", result);
    }

    // ✅ Tes Paling Ter
    @Test
    @DisplayName("palingTer() menemukan nilai tertinggi dan terendah dengan format key=value")
    void testPalingTerValid() {
        String encoded = Base64.getEncoder().encodeToString("Ali=90,Budi=80,Cici=70".getBytes());
        String result = controller.palingTer(encoded);
        assertTrue(result.contains("Nilai Tertinggi"));
        assertTrue(result.contains("Nilai Terendah"));
    }

    @Test
    @DisplayName("palingTer() menangani input kosong")
    void testPalingTerKosong() {
        String result = controller.palingTer("");
        assertEquals("Data tidak boleh kosong.", result);
    }

    @Test
    @DisplayName("palingTer() menangani Base64 tidak valid")
    void testPalingTerBase64Invalid() {
        String result = controller.palingTer("$$$invalid$$$");
        assertEquals("Input Base64 tidak valid.", result);
    }

    @Test
    @DisplayName("palingTer() menangani pasangan nilai yang tidak valid")
    void testPalingTerFormatSalah() {
        String encoded = Base64.getEncoder().encodeToString("Ali=90,Budi=X".getBytes());
        String result = controller.palingTer(encoded);
        assertTrue(result.contains("Format nilai salah"));
    }

    @Test
    @DisplayName("palingTer() menangani data tanpa pasangan valid")
    void testPalingTerDataKosongValid() {
        String encoded = Base64.getEncoder().encodeToString("".getBytes());
        String result = controller.palingTer(encoded);
        assertTrue(result.contains("Tidak ada data") || result.contains("Tidak ada pasangan"));
    }
}
