package ru.nsu.zenin;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import ru.nsu.zenin.pizzeria.dto.PizzeriaDTO;
import ru.nsu.zenin.pizzeria.mapper.PizzeriaMapper;
import ru.nsu.zenin.pizzeria.model.Pizzeria;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Wrong amount of arguments, expected: 1, got: " + args.length);
            return;
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            PizzeriaDTO pizzeriaConfig =
                    objectMapper.readValue(new File(args[0]), PizzeriaDTO.class);
            Pizzeria pizzeria = PizzeriaMapper.INSTANCE.fromPizzeriaDTO(pizzeriaConfig);

            System.out.println(pizzeria.toString());
        } catch (IOException e) {
            System.out.println("Failed to read config:\n\t" + e.getMessage());
        }
    }
}
