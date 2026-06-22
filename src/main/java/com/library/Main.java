package com.library;

import com.library.menu.MainMenu;

public class Main {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("        Library Management System       ");
        System.out.println("========================================\n");

        MainMenu menu = new MainMenu();
        menu.start();
    }
}