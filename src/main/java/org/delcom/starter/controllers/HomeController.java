package org.delcom.starter.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class HomeController {

    // -------------------------
    // Utility: decode Base64
    // -------------------------
    private String decodeBase64(String strBase64) {
        if (strBase64 == null) return "";
        try {
            byte[] base64Bytes = strBase64.getBytes();
            byte[] decodedBytes = java.util.Base64.getDecoder().decode(new String(base64Bytes));
            return new String(decodedBytes);
        } catch (IllegalArgumentException e) {
            // jika bukan Base64 yang valid, kembalikan input apa adanya
            return strBase64;
        }
    }

    // =========================
    // 1) Informasi NIM
    // =========================
    @GetMapping("/informasiNim/{nim}")
    public String informasiNim(@PathVariable String nim) {
        if (nim == null || nim.isBlank()) return "NIM kosong.";

        // contoh mapping prefix seperti pada materi praktikum
        Map<String, String> map = new HashMap<>();
        map.put("11S", "Sarjana Informatika");
        map.put("12S", "Sarjana Sistem Informasi");
        map.put("14S", "Sarjana Teknik Elektro");
        map.put("21S", "Sarjana Manajemen Rekayasa");
        map.put("22S", "Sarjana Teknik Metalurgi");
        map.put("31S", "Sarjana Teknik Bioproses");
        map.put("113", "Diploma 3 Teknologi Informasi");
        map.put("114", "Diploma 4 Teknologi Rekayasa Perangkat Lunak");
        // (tambahkan mapping lain jika perlu)

        // asumsi format: PREFIX(3) + angkatan(2) + nomorUrut(rest)
        String prefix = nim.length() >= 3 ? nim.substring(0, 3) : nim;
        String rest = nim.length() > 3 ? nim.substring(3) : "";

        String program = map.get(prefix);
        StringBuilder sb = new StringBuilder();
        if (program == null) {
            sb.append("Prefix NIM '").append(prefix).append("' tidak ditemukan.");
            return sb.toString();
        }

        // ambil 2 digit angkatan jika tersedia
        String angkatanStr = rest.length() >= 2 ? rest.substring(0, 2) : rest;
        String nomorUrut = rest.length() > 2 ? rest.substring(2) : "";

        int tahun = -1;
        try {
            if (!angkatanStr.isEmpty()) {
                tahun = 2000 + Integer.parseInt(angkatanStr);
            }
        } catch (NumberFormatException ignored) {}

        sb.append("Informasi NIM ").append(nim).append(":\n");
        sb.append(">> Program Studi: ").append(program).append("\n");
        sb.append(">> Angkatan: ").append(tahun == -1 ? "tidak diketahui" : tahun).append("\n");
        sb.append(">> Urutan: ").append(nomorUrut.isBlank() ? "tidak diketahui" : Integer.parseInt(nomorUrut)).append("\n");

        return sb.toString();
    }

    // =========================
    // 2) Perolehan Nilai
    //    - menerima strBase64 yang berisi beberapa baris test case
    //    - contoh baris: "T|90|21" atau "UAS|92|82" dan ada beberapa angka/metadata di atasnya
    // =========================
    @GetMapping("/perolehanNilai/{strBase64}")
    public String perolehanNilai(@PathVariable String strBase64) {
        String plain = decodeBase64(strBase64);
        if (plain.isBlank()) return "Input kosong atau tidak valid Base64.";

        // parsing baris
        String[] lines = plain.split("\\r?\\n");
        // kumpulkan entries berbentuk TYPE|scoreA|scoreB
        List<String> gradeLines = new ArrayList<>();
        for (String l : lines) {
            String t = l.trim();
            if (t.isEmpty() || t.equals("---")) continue;
            if (t.contains("|")) gradeLines.add(t);
        }

        if (gradeLines.isEmpty()) return "Tidak ditemukan data nilai pada input.";

        // untuk tiap entry, kita ambil skor (dua angka jika ada) lalu kumpulkan statistik sederhana
        int count = 0;
        double totalAll = 0.0;
        Map<String, List<Double>> byType = new LinkedHashMap<>();

        for (String g : gradeLines) {
            String[] parts = g.split("\\|");
            String type = parts.length > 0 ? parts[0] : "UNKNOWN";
            List<Double> scores = new ArrayList<>();
            for (int i = 1; i < parts.length; i++) {
                try {
                    scores.add(Double.parseDouble(parts[i]));
                } catch (NumberFormatException ignored) {}
            }
            if (scores.isEmpty()) continue;

            double avg = scores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            byType.computeIfAbsent(type, k -> new ArrayList<>()).add(avg);
            totalAll += avg;
            count++;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Ringkasan Perolehan Nilai:\n");
        sb.append("Jumlah entry nilai: ").append(count).append("\n");
        sb.append(String.format("Rata-rata keseluruhan (tiap entry dihitung rata-rata skornya): %.2f\n", (count==0?0:totalAll/count)));

        sb.append("\nRata-rata per jenis:\n");
        for (Map.Entry<String, List<Double>> e : byType.entrySet()) {
            double avgOfType = e.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            sb.append(String.format(" - %s : rata-rata %.2f (dari %d entry)\n", e.getKey(), avgOfType, e.getValue().size()));
        }

        // contoh threshold lulus sederhana: rata-rata >= 60
        double overall = (count==0?0:totalAll/count);
        sb.append("\nStatus kelulusan (threshold 60): ").append(overall >= 60.0 ? "Lulus" : "Tidak Lulus").append("\n");

        return sb.toString();
    }

    // =========================
    // 3) Perbedaan L dan Kebalikan L
    //    - input: Base64 yang berisi matriks NxN, format baris per baris, first line = N
    //    - L: kolom pertama + baris terakhir (hindari double count pojok)
    //    - L-terbalik: kolom terakhir + baris pertama (hindari double count pojok)
    // =========================
    @GetMapping("/perbedaanL/{strBase64}")
    public String perbedaanL(@PathVariable String strBase64) {
        String plain = decodeBase64(strBase64);
        if (plain.isBlank()) return "Input kosong atau tidak valid Base64.";

        String[] lines = plain.split("\\r?\\n");
        Queue<String> q = new LinkedList<>();
        for (String l : lines) {
            String t = l.trim();
            if (!t.isEmpty()) q.add(t);
        }
        if (q.isEmpty()) return "Tidak ada data matriks.";

        int n;
        try {
            n = Integer.parseInt(q.poll());
        } catch (Exception ex) {
            return "Baris pertama harus ukuran matriks (integer).";
        }

        if (n <= 0) return "Ukuran matriks harus > 0.";
        // jika baris kurang, kembalikan error
        int[][] m = new int[n][n];
        for (int i = 0; i < n; i++) {
            if (q.isEmpty()) return "Data matriks tidak lengkap.";
            String rowLine = q.poll();
            String[] toks = rowLine.trim().split("\\s+");
            if (toks.length < n) return "Baris matriks " + i + " kurang kolom.";
            for (int j = 0; j < n; j++) {
                try {
                    m[i][j] = Integer.parseInt(toks[j]);
                } catch (NumberFormatException ex) {
                    return "Nilai matriks bukan integer di baris " + i + " kolom " + j;
                }
            }
        }

        // kasus kecil
        if (n == 1) {
            return "Nilai L: Tidak Ada\nNilai Kebalikan L: Tidak Ada\nNilai Tengah: " + m[0][0] + "\nPerbedaan: Tidak Ada\nDominan: " + m[0][0];
        }
        if (n == 2) {
            int total = 0;
            for (int i=0;i<n;i++) for (int j=0;j<n;j++) total += m[i][j];
            return "Nilai L: Tidak Ada\nNilai Kebalikan L: Tidak Ada\nNilai Tengah: " + total + "\nPerbedaan: Tidak Ada\nDominan: " + total;
        }

        // hitung L = kolom pertama + baris terakhir (hindari double count pojok)
        int sumL = 0;
        for (int i = 0; i < n; i++) sumL += m[i][0]; // kolom pertama
        for (int j = 1; j < n; j++) sumL += m[n-1][j]; // baris terakhir (skip bottom-left karena sudah dihitung)

        // hitung L-terbalik = kolom terakhir + baris pertama (hindari double count pojok)
        int sumLInv = 0;
        for (int i = 0; i < n; i++) sumLInv += m[i][n-1]; // kolom terakhir
        for (int j = 0; j < n-1; j++) sumLInv += m[0][j]; // baris pertama (skip top-right karena sudah dihitung)

        int diff = Math.abs(sumL - sumLInv);
        String dominan = sumL > sumLInv ? "L" : (sumLInv > sumL ? "L-terbalik" : "Seimbang");

        StringBuilder sb = new StringBuilder();
        sb.append("Nilai L: ").append(sumL).append("\n");
        sb.append("Nilai Kebalikan L: ").append(sumLInv).append("\n");
        // nilai tengah (diambil definisi: elemen tengah jika n odd, atau total jika even) — gunakan rata2 atau total
        if (n % 2 == 1) {
            int mid = m[n/2][n/2];
            sb.append("Nilai Tengah: ").append(mid).append("\n");
        } else {
            sb.append("Nilai Tengah: ").append("Tidak tunggal (n genap)").append("\n");
        }
        sb.append("Perbedaan: ").append(diff).append("\n");
        sb.append("Dominan: ").append(dominan).append("\n");

        return sb.toString();
    }

    // =========================
    // 4) Paling Ter (statistik frekuensi dari sekumpulan angka)
    //    - input: Base64 berupa daftar angka satu per baris (atau pemisah spasi)
    // =========================
    @GetMapping("/palingTer/{strBase64}")
    public String palingTer(@PathVariable String strBase64) {
        String plain = decodeBase64(strBase64);
        if (plain.isBlank()) return "Input kosong atau tidak valid Base64.";

        // ambil semua token angka
        String[] tokens = plain.split("[\\s,;]+");
        List<Integer> numbers = new ArrayList<>();
        for (String t : tokens) {
            if (t == null || t.trim().isEmpty()) continue;
            try {
                numbers.add(Integer.parseInt(t.trim()));
            } catch (NumberFormatException ignored) {}
        }
        if (numbers.isEmpty()) return "Tidak ada angka ditemukan pada input.";

        int max = numbers.stream().mapToInt(Integer::intValue).max().orElse(0);
        int min = numbers.stream().mapToInt(Integer::intValue).min().orElse(0);

        // frekuensi
        Map<Integer, Integer> freq = new HashMap<>();
        for (int v : numbers) freq.put(v, freq.getOrDefault(v, 0) + 1);

        // modus (terbanyak)
        int mode = numbers.get(0);
        int modeFreq = 0;
        for (Map.Entry<Integer, Integer> e : freq.entrySet()) {
            if (e.getValue() > modeFreq || (e.getValue() == modeFreq && e.getKey() < mode)) {
                mode = e.getKey();
                modeFreq = e.getValue();
            }
        }

        // paling jarang
        int minFreq = Integer.MAX_VALUE;
        int least = numbers.get(0);
        for (Map.Entry<Integer, Integer> e : freq.entrySet()) {
            if (e.getValue() < minFreq || (e.getValue() == minFreq && e.getKey() < least)) {
                minFreq = e.getValue();
                least = e.getKey();
            }
        }

        // jumlah tertinggi dan terendah (angka * frekuensi)
        int angkaTotalMaks = Collections.max(freq.entrySet(), Map.Entry.comparingByKey()).getKey();
        int frekuensiTotalMaks = freq.get(angkaTotalMaks);
        int totalMaks = angkaTotalMaks * frekuensiTotalMaks;

        int angkaJumlahMin = Collections.min(freq.entrySet(), Map.Entry.comparingByKey()).getKey();
        int frekuensiTotalMin = freq.get(angkaJumlahMin);
        int totalJumlahMin = angkaJumlahMin * frekuensiTotalMin;

        StringBuilder sb = new StringBuilder();
        sb.append("Statistik Paling Ter:\n");
        sb.append("Jumlah data: ").append(numbers.size()).append("\n");
        sb.append("Tertinggi (nilai): ").append(max).append("\n");
        sb.append("Terendah (nilai): ").append(min).append("\n");
        sb.append("Terbanyak (modus): ").append(mode).append(" (" + modeFreq + "x)\n");
        sb.append("Tersedikit: ").append(least).append(" (" + minFreq + "x)\n");
        sb.append("Jumlah Tertinggi: ").append(angkaTotalMaks).append(" * ").append(frekuensiTotalMaks).append(" = ").append(totalMaks).append("\n");
        sb.append("Jumlah Terendah: ").append(angkaJumlahMin).append(" * ").append(frekuensiTotalMin).append(" = ").append(totalJumlahMin).append("\n");

        // tampilkan frekuensi ringkasan top 5
        List<Map.Entry<Integer,Integer>> sorted = freq.entrySet().stream()
                .sorted((a,b)->b.getValue().equals(a.getValue()) ? Integer.compare(a.getKey(), b.getKey()) : Integer.compare(b.getValue(), a.getValue()))
                .collect(Collectors.toList());
        sb.append("\nFrekuensi (top entries):\n");
        int shown = Math.min(10, sorted.size());
        for (int i = 0; i < shown; i++) {
            Map.Entry<Integer,Integer> e = sorted.get(i);
            sb.append(String.format(" - %d : %dx\n", e.getKey(), e.getValue()));
        }

        return sb.toString();
    }

    // contoh endpoint root
    @GetMapping("/")
    public String hello() {
        return "Hello, welcome to the combined Studi Kasus API. Available endpoints: "
                + "/informasiNim/{nim} , /perolehanNilai/{strBase64} , /perbedaanL/{strBase64} , /palingTer/{strBase64}";
    }
}
