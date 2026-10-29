/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.borala.borala.controller;
//Faz a importação dos pacotes incluídos dentro das dependencias
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
/**
 *
 * @author naype
 */
@Controller
public class HomeController {
    // /-> significa que ele é a raiz da nossa aplicação
    @RequestMapping("/") //direciona uma requisição http de acordo com o padrão "/" para o método index 
    public String indexModel(Model model){
        model.addAttribute("msnBemVindo", "Bem-Vindo à Bora Lá!!");
        return "index";
    }
    
}
