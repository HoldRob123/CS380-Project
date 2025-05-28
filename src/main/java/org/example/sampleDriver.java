package org.example;

import java.util.Scanner;

public class sampleDriver {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String vin = null;
        String make = null;
        String model = null;
        String year = null;
        String nickname = null;
        boolean isSavedOnly = false;
        String userName = "SAMPLE";

        // VIN input
        while (true) {
            System.out.print("Enter VIN (17 characters or 'null'): ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("null")) {
                vin = null;
                break;
            } else if (input.length() == 17) {
                vin = input;
                break;
            } else {
                System.out.println(" VIN must be exactly 17 characters long or 'null'.");
            }
        }

        // Make input
        while (true) {
            System.out.print("Enter Make (no numbers or 'null'): ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("null")) {
                make = null;
                break;
            } else if (!input.matches(".*\\d.*")) {
                make = input;
                break;
            } else {
                System.out.println(" Make should not contain numbers.");
            }
        }

        // Model input (optional validation)
        System.out.print("Enter Model (any string or 'null'): ");
        String inputModel = scanner.nextLine().trim();
        model = inputModel.equalsIgnoreCase("null") ? null : inputModel;

        // Year input
        while (true) {
            System.out.print("Enter Year (4-digit year or 'null'): ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("null")) {
                year = null;
                break;
            } else if (input.matches("\\d{4}")) {
                year = input;
                break;
            } else {
                System.out.println(" Year must be a 4-digit number or 'null'.");
            }
        }

        // Nickname input
        System.out.print("Enter Nickname (any string or 'null'): ");
        String inputNick = scanner.nextLine().trim();
        nickname = inputNick.equalsIgnoreCase("null") ? null : inputNick;

        // Saved-only filter
        System.out.print("Show only saved vehicles? (true/false): ");
        String savedInput = scanner.nextLine().trim();
        isSavedOnly = savedInput.equalsIgnoreCase("true");

        System.out.println("\n Searching...\n");

        VehicleSearch searchService = new VehicleSearch();
        searchService.searchVehicles(vin, make, model, year, nickname, isSavedOnly, userName);
    }
}

