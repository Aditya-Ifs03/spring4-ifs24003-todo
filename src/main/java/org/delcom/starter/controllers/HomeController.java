package org.delcom.starter.controllers;

import org.springframework.web.bind.annotation.*;
import java.util.*;

/**
 * Versi Hybrid HomeController:
 * - Menggabungkan logika versi kode 1 (dinamis dan detail)
 * - Dengan efisiensi & format Base64 versi kode 2
 * - Siap untuk pengujian penuh (line & branch coverage)
 */

@RestController
@RequestMapping("/")
public class HomeController {

    // ✅ Endpoint Utama
    @GetMapping
    public String hello() {
        return "Selamat datang di Praktikum Spring Boot Versi Hybrid Dinamis!";
    }

    // 1️⃣ Informasi NIM (hybrid)
    @GetMapping("/informasiNim/{strBase64}")
    public String informasiNim(@PathVariable String strBase64) {
        if (strBase64 == null || strBase64.isEmpty()) {
            return "NIM tidak boleh kosong.";
        }

        byte[] decodedBytes;
        try {
            decodedBytes = Base64.getDecoder().decode(strBase64);
        } catch (IllegalArgumentException e) {
            return "Input Base64 tidak valid.";
        }

        String nim = new String(decodedBytes);
        if (nim.isEmpty()) {
            return "NIM tidak boleh kosong.";
        }

        String prodi = nim.startsWith("IFS") ? "Informatika" : "Tidak diketahui";
        String angkatan = nim.length() >= 5 ? "20" + nim.substring(3, 5) : "Tidak diketahui";
        String urutan = nim.length() >= 8 ? nim.substring(5) : "Tidak lengkap";

        return String.format(
                "NIM: %s\nProgram Studi: %s\nAngkatan: %s\nUrutan: %s",
                nim, prodi, angkatan, urutan
        );
    }

    // 2️⃣ Perolehan Nilai (hybrid)
    @GetMapping("/perolehanNilai/{strBase64}")
    public String perolehanNilai(@PathVariable String strBase64) {
        if (strBase64 == null || strBase64.isEmpty()) {
            return "Data nilai tidak boleh kosong.";
        }

        String decoded;
        try {
            decoded = new String(Base64.getDecoder().decode(strBase64));
        } catch (IllegalArgumentException e) {
            return "Input Base64 tidak valid.";
        }

        String[] parts = decoded.split(",");
        if (parts.length < 3) {
            return "Data nilai tidak lengkap. Harus berisi minimal 3 nilai.";
        }

        List<Integer> nilai = new ArrayList<>();
        for (String p : parts) {
            try {
                nilai.add(Integer.parseInt(p.trim()));
            } catch (NumberFormatException e) {
                return "Format nilai tidak valid: " + p;
            }
        }

        int total = nilai.stream().mapToInt(Integer::intValue).sum();
        double rata = (double) total / nilai.size();
        int max = Collections.max(nilai);
        int min = Collections.min(nilai);

        String grade;
        if (rata >= 85) grade = "A";
        else if (rata >= 70) grade = "B";
        else if (rata >= 55) grade = "C";
        else if (rata >= 40) grade = "D";
        else grade = "E";

        return String.format(
                "Nilai: %s\nTotal: %d\nRata-rata: %.2f\nNilai Tertinggi: %d\nNilai Terendah: %d\nGrade: %s",
                decoded, total, rata, max, min, grade
        );
    }

    // 3️⃣ Perbedaan L dan Kebalikannya (hybrid)
    @GetMapping("/perbedaanL/{strBase64}")
    public String perbedaanL(@PathVariable String strBase64) {
        if (strBase64 == null || strBase64.isEmpty()) {
            return "Input tidak boleh kosong.";
        }

        String decoded;
        try {
            decoded = new String(Base64.getDecoder().decode(strBase64));
        } catch (IllegalArgumentException e) {
            return "Input Base64 tidak valid.";
        }

        StringBuilder kebalikan = new StringBuilder(decoded).reverse();
        boolean sama = decoded.equalsIgnoreCase(kebalikan.toString());

        long jumlahL = decoded.chars()
                .filter(c -> c == 'L' || c == 'l')
                .count();
        long nonL = decoded.length() - jumlahL;

        return String.format(
                "Teks asli: %s\nKebalikan: %s\nApakah sama?: %s\nJumlah huruf L/l: %d\nNon-L: %d",
                decoded, kebalikan, sama ? "Ya" : "Tidak", jumlahL, nonL
        );
    }

    // 4️⃣ Paling Ter (hybrid)
    @GetMapping("/palingTer/{strBase64}")
    public String palingTer(@PathVariable String strBase64) {
        if (strBase64 == null || strBase64.isEmpty()) {
            return "Data tidak boleh kosong.";
        }

        String decoded;
        try {
            decoded = new String(Base64.getDecoder().decode(strBase64));
        } catch (IllegalArgumentException e) {
            return "Input Base64 tidak valid.";
        }

        String[] pairs = decoded.split(",");
        if (pairs.length == 0) return "Tidak ada data yang valid.";

        Map<String, Integer> map = new HashMap<>();
        for (String pair : pairs) {
            String[] kv = pair.split("=");
            if (kv.length == 2) {
                try {
                    map.put(kv[0].trim(), Integer.parseInt(kv[1].trim()));
                } catch (NumberFormatException e) {
                    return "Format nilai salah pada: " + pair;
                }
            }
        }

        if (map.isEmpty()) {
            return "Tidak ada pasangan data yang valid.";
        }

        Map.Entry<String, Integer> maxEntry = Collections.max(map.entrySet(), Map.Entry.comparingByValue());
        Map.Entry<String, Integer> minEntry = Collections.min(map.entrySet(), Map.Entry.comparingByValue());

        return String.format(
                "Nilai Tertinggi: %s (%d)\nNilai Terendah: %s (%d)",
                maxEntry.getKey(), maxEntry.getValue(),
                minEntry.getKey(), minEntry.getValue()
        );
    }
}
