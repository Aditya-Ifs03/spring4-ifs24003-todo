package org.delcom.starter.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.Base64;

@RestController
public class HomeController {

    @GetMapping("/")
    public String hello() {
        return "Hay Abdullah, selamat datang di pengembangan aplikasi dengan Spring Boot!";
    }

    @GetMapping("/hello/{name}")
    public String sayHello(@PathVariable String name) {
        return "Hello, " + name + "!";
    }

// 1. Informasi NIM
@GetMapping("/informasiNim/{nim}")
public String informasiNim(@PathVariable String nim) {
    return "Informasi NIM Anda adalah: " + nim;
}

// 2. Perolehan Nilai
@GetMapping("/perolehanNilai/{strBase64}")
public String perolehanNilai(@PathVariable String strBase64) {
    byte[] decodedBytes = Base64.getDecoder().decode(strBase64);
    String nilai = new String(decodedBytes);
    return "Nilai hasil decode dari Base64: " + nilai;
}

// 3. Perbedaan L dan Kebalikannya
@GetMapping("/perbedaanL/{strBase64}")
public String perbedaanL(@PathVariable String strBase64) {
    byte[] decodedBytes = Base64.getDecoder().decode(strBase64);
    String teks = new String(decodedBytes);
    String reversed = new StringBuilder(teks).reverse().toString();
    return "Teks asli: " + teks + "<br>Teks kebalikannya: " + reversed;
}

// 4. Paling Ter
@GetMapping("/palingTer/{strBase64}")
public String palingTer(@PathVariable String strBase64) {
    byte[] decodedBytes = Base64.getDecoder().decode(strBase64);
    String teks = new String(decodedBytes);
    return "Data paling terdepan (decode dari Base64): " + teks.toUpperCase();
}
}