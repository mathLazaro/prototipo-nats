package com.github.mathlazaro.view;

import java.util.Scanner;

public interface View {

    static boolean readKeepRunning(Scanner scanner) {
        System.out.print("Continuar? (s/n): ");
        String option = scanner.nextLine().trim().toLowerCase();
        return option.equals("s") || option.equals("sim");
    }

}
