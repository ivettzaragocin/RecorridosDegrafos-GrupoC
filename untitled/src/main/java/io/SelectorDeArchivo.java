package io;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SelectorDeArchivo {

    public Path escogerArchivoDeCarpeta(Path carpeta) throws IOException {
        // Listar solo archivos .txt
        List<Path> archivos;
        try (Stream<Path> stream = Files.list(carpeta)) {
            archivos = stream
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".txt"))
                    .collect(Collectors.toList());
        }

        if (archivos.isEmpty()) {
            throw new IOException("No se encontraron archivos .txt en: " + carpeta.toAbsolutePath());
        }

        System.out.println("=========== DATASETS DISPONIBLES ===========");
        for (int i = 0; i < archivos.size(); i++) {
            System.out.println(" [" + i + "] -> " + archivos.get(i).getFileName());
        }
        System.out.println("============================================");

        // Usamos un Scanner local pero SIN cerrar System.in al final
        // para no romper el scanner del Main.
        Scanner sc = new Scanner(System.in);
        int opcion;

        while (true) {
            System.out.print("Seleccione el número del archivo: ");

            if (!sc.hasNextInt()) {
                System.out.println("❌ Error: Debe ingresar un número.");
                sc.next();
                continue;
            }

            opcion = sc.nextInt();

            if (opcion < 0 || opcion >= archivos.size()) {
                System.out.println("❌ Opción inválida. Intente de nuevo.");
            } else {
                break;
            }
        }
        // No cerramos sc porque cerraría System.in para todo el programa
        return archivos.get(opcion);
    }
}