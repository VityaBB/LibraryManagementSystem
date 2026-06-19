package com.library;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import com.library.menu.MainMenu;

public class Main {
    public static void main(String[] args) {


        System.out.println("========================================");
        System.out.println("   БИБЛИОТЕКА - JDBC КОНСОЛЬНОЕ ПРИЛОЖЕНИЕ");
        System.out.println("========================================\n");
        
        MainMenu menu = new MainMenu();
        menu.start();
    }
}