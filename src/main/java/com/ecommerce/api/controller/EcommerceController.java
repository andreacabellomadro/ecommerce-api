package com.ecommerce.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController 
@RequestMapping ("/api")
public class EcommerceController {

    @GetMapping("/hello")
    public String getHello() {
        return "Hola, bienvenido a la API de E-commerce!";
    }
    
}
