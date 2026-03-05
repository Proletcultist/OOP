package ru.nsu.zenin;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import ru.nsu.zenin.logging.Logger;
import ru.nsu.zenin.pizzeria.dto.PizzeriaDTO;
import ru.nsu.zenin.pizzeria.mapper.PizzeriaMapper;
import ru.nsu.zenin.pizzeria.model.Pizza;
import ru.nsu.zenin.pizzeria.model.Pizzeria;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Wrong amount of arguments, expected: 1, got: " + args.length);
            return;
        }

        PizzeriaDTO pizzeriaConfig;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            pizzeriaConfig = objectMapper.readValue(new File(args[0]), PizzeriaDTO.class);
        } catch (IOException e) {
            System.out.println("Failed to read config:\n\t" + e.getMessage());
            return;
        }

        Logger.init(new BufferedWriter(new OutputStreamWriter(System.err)));

        Pizzeria pizzeria = PizzeriaMapper.INSTANCE.fromPizzeriaDTO(pizzeriaConfig);
        pizzeria.start();

        Thread runner =
                new Thread(
                        () -> {
                            try (Scanner scanner = new Scanner(System.in)) {
                                while (!Thread.currentThread().isInterrupted()) {
                                    String readen = scanner.next();
                                    switch (readen) {
                                        case "peperoni":
                                            pizzeria.makeOrder(Pizza.PEPERONI);
                                            break;
                                        case "margarita":
                                            pizzeria.makeOrder(Pizza.MARGARITA);
                                            break;
                                        default:
                                            Logger.log(
                                                    Logger.LogLevel.ERROR,
                                                    "Unknown pizza: \"" + readen + "\"");
                                    }
                                }
                            } catch (NoSuchElementException e) {
                                Logger.log(
                                        Logger.LogLevel.INFO, "No more input from user available");
                            }
                        });

        runner.start();
        TimeUnit.MILLISECONDS.sleep(pizzeriaConfig.virtualHourValue() * 12);
        runner.interrupt();
        runner.join();

        pizzeria.stop();

        Logger.close();
    }
}
