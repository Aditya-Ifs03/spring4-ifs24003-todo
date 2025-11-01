package org.delcom.starter.controllers;

import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.Base64;

@RestController
public class HomeController {

    // ==============================
    // 0️⃣ HALAMAN UTAMA
    // ==============================
    @GetMapping("/")
    public String hello() {
        return "Selamat datang di pengembangan aplikasi dengan Spring Boot!";
    }

    // ==============================
    // 1️⃣ INFORMASI NIM
    // ==============================
    @GetMapping("/informasiNim/{nim}")
    public String informasiNim(@PathVariable String nim) {
        Map<String, String> prodi = new HashMap<>();
        prodi.put("11S", "Sarjana Informatika");
        prodi.put("12S", "Sarjana Sistem Informasi");
        prodi.put("14S", "Sarjana Teknik Elektro");
        prodi.put("21S", "Sarjana Manajemen Rekayasa");
        prodi.put("22S", "Sarjana Teknik Metalurgi");
        prodi.put("31S", "Sarjana Teknik Bioproses");
        prodi.put("114", "Diploma 4 Teknologi Rekayasa Perangkat Lunak");
        prodi.put("113", "Diploma 3 Teknologi Informasi");
        prodi.put("133", "Diploma 3 Teknologi Komputer");

        String kode = nim.substring(0, 3);
        String angkatan = "20" + nim.substring(3, 5);
        int urutan = Integer.parseInt(nim.substring(nim.length() - 3));

        return String.format("""
                Informasi NIM %s:
                >> Program Studi: %s
                >> Angkatan: %s
                >> Urutan: %d
                """, nim, prodi.getOrDefault(kode, "Tidak Dikenal"), angkatan, urutan);
    }

    // ==============================
    // 2️⃣ PEROLEHAN NILAI
    // ==============================
    @GetMapping("/perolehanNilai/{strBase64}")
    public String perolehanNilai(@PathVariable String strBase64) {
        String decoded = new String(Base64.getDecoder().decode(strBase64));
        Scanner sc = new Scanner(decoded);

        double pa = sc.nextDouble(), t = sc.nextDouble(), k = sc.nextDouble(),
               p = sc.nextDouble(), uts = sc.nextDouble(), uas = sc.nextDouble();
        sc.nextLine();

        int sumPA = 0, maxPA = 0;
        int sumT = 0, maxT = 0;
        int sumK = 0, maxK = 0;
        int sumP = 0, maxP = 0;
        int sumUTS = 0, maxUTS = 0;
        int sumUAS = 0, maxUAS = 0;

        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.equals("---")) break;
            String[] parts = line.split("\\|");
            String tipe = parts[0];
            int max = Integer.parseInt(parts[1]);
            int score = Integer.parseInt(parts[2]);
            switch (tipe) {
                case "PA" -> { maxPA += max; sumPA += score; }
                case "T" -> { maxT += max; sumT += score; }
                case "K" -> { maxK += max; sumK += score; }
                case "P" -> { maxP += max; sumP += score; }
                case "UTS" -> { maxUTS += max; sumUTS += score; }
                case "UAS" -> { maxUAS += max; sumUAS += score; }
            }
        }

        int paPct = maxPA == 0 ? 0 : (int) Math.floor((double) sumPA * 100 / maxPA);
        int tPct = maxT == 0 ? 0 : (int) Math.floor((double) sumT * 100 / maxT);
        int kPct = maxK == 0 ? 0 : (int) Math.floor((double) sumK * 100 / maxK);
        int pPct = maxP == 0 ? 0 : (int) Math.floor((double) sumP * 100 / maxP);
        int utsPct = maxUTS == 0 ? 0 : (int) Math.floor((double) sumUTS * 100 / maxUTS);
        int uasPct = maxUAS == 0 ? 0 : (int) Math.floor((double) sumUAS * 100 / maxUAS);

        double total = paPct / 100.0 * pa +
                       tPct / 100.0 * t +
                       kPct / 100.0 * k +
                       pPct / 100.0 * p +
                       utsPct / 100.0 * uts +
                       uasPct / 100.0 * uas;

        sc.close();

        return String.format("""
                Perolehan Nilai:
                >> Partisipatif: %d/100 (%.2f/%.0f)
                >> Tugas: %d/100 (%.2f/%.0f)
                >> Kuis: %d/100 (%.2f/%.0f)
                >> Proyek: %d/100 (%.2f/%.0f)
                >> UTS: %d/100 (%.2f/%.0f)
                >> UAS: %d/100 (%.2f/%.0f)
                                
                >> Nilai Akhir: %.2f
                >> Grade: %s
                """,
                paPct, paPct / 100.0 * pa, pa,
                tPct, tPct / 100.0 * t, t,
                kPct, kPct / 100.0 * k, k,
                pPct, pPct / 100.0 * p, p,
                utsPct, utsPct / 100.0 * uts, uts,
                uasPct, uasPct / 100.0 * uas, uas,
                total, getGrade(total));
    }

    private String getGrade(double n) {
        if (n >= 79.5) return "A";
        else if (n >= 72.0) return "AB";
        else if (n >= 64.5) return "B";
        else if (n >= 57.0) return "BC";
        else if (n >= 49.5) return "C";
        else if (n >= 34.0) return "D";
        else return "E";
    }

    // ==============================
    // 3️⃣ PERBEDAAN L DAN KEBALIKANNYA
    // ==============================
    @GetMapping("/perbedaanL/{strBase64}")
    public String perbedaanL(@PathVariable String strBase64) {
        String decoded = new String(Base64.getDecoder().decode(strBase64));
        Scanner sc = new Scanner(decoded);
        int n = sc.nextInt();
        int[][] m = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                m[i][j] = sc.nextInt();

        if (n == 1) {
            int val = m[0][0];
            return String.format("""
                    Nilai L: Tidak Ada
                    Nilai Kebalikan L: Tidak Ada
                    Nilai Tengah: %d
                    Perbedaan: Tidak Ada
                    Dominan: %d
                    """, val, val);
        }

        int l = 0, k = 0;
        for (int i = 0; i < n; i++) l += m[i][0];
        for (int j = 1; j <= n - 2; j++) l += m[n - 1][j];

        for (int i = 0; i < n; i++) k += m[i][n - 1];
        for (int j = 1; j <= n - 2; j++) k += m[0][j];

        int tengah;
        if (n % 2 == 1) tengah = m[n / 2][n / 2];
        else {
            int a = n / 2 - 1, b = n / 2;
            tengah = m[a][a] + m[a][b] + m[b][a] + m[b][b];
        }

        int diff = Math.abs(l - k);
        int dominan = diff == 0 ? tengah : Math.max(l, k);

        sc.close();
        return String.format("""
                Nilai L: %d
                Nilai Kebalikan L: %d
                Nilai Tengah: %d
                Perbedaan: %d
                Dominan: %d
                """, l, k, tengah, diff, dominan);
    }

    // ==============================
    // 4️⃣ PALING TER
    // ==============================
    @GetMapping("/palingTer/{strBase64}")
    public String palingTer(@PathVariable String strBase64) {
        String decoded = new String(Base64.getDecoder().decode(strBase64));
        Scanner input = new Scanner(decoded);
        List<Integer> data = new ArrayList<>();

        while (input.hasNextLine()) {
            String line = input.nextLine().trim();
            if (line.equals("---")) break;
            if (!line.isEmpty()) data.add(Integer.parseInt(line));
        }

        if (data.isEmpty()) return "Tidak ada input";

        Map<Integer, Integer> counter = new LinkedHashMap<>();
        int maxNum = Integer.MIN_VALUE, minNum = Integer.MAX_VALUE;
        int mostCommon = 0, maxCount = 0;

        for (int num : data) {
            counter.put(num, counter.getOrDefault(num, 0) + 1);
            int occ = counter.get(num);
            if (occ > maxCount) {
                maxCount = occ;
                mostCommon = num;
            }
            if (num > maxNum) maxNum = num;
            if (num < minNum) minNum = num;
        }

        int rarest = counter.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .get().getKey();

        input.close();
        return String.format("""
                Tertinggi: %d
                Terendah: %d
                Terbanyak: %d (%dx)
                Tersedikit: %d (%dx)
                """,
                maxNum, minNum,
                mostCommon, maxCount,
                rarest, counter.get(rarest));
    }
}
