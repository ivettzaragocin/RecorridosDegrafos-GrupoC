import io.LectorMatriz;
import io.SelectorDeArchivo;
import io.ValidadorMatriz;
import structures.Graph;
import utils.GraphAnalizer;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static Graph currentGraph = null;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean running = true;

        Path rutaDatasets = Paths.get("src", "main", "resources");
        while (running) {
            printMenu();
            int option = getIntInput(ANSI.BRIGHT_YELLOW + ">> Seleccione una opción: " + ANSI.RESET);

            switch (option) {
                case 1:
                    cargarGrafoInteractivo(rutaDatasets);
                    break;
                case 2:
                    ejecutarBFS();
                    break;
                case 3:
                    ejecutarDFS();
                    break;
                case 4:
                    analizarExtensiones();
                    break;
                case 5:
                    mostrarInformacionGrafo();
                    break;
                case 6:
                    System.out.println(ANSI.MAGENTA + "\nFinnnnn" + ANSI.RESET);
                    running = false;
                    break;
                default:
                    System.out.println(ANSI.RED_BOLD + "Opción no reconocida. Por favor elija entre 1 y 6." + ANSI.RESET);
            }

            if (running) {
                System.out.println(ANSI.BRIGHT_BLACK + "\n[Presione Enter para volver al menú principal...]" + ANSI.RESET);
                scanner.nextLine();
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n" + ANSI.CYAN_BOLD + "============================================================" + ANSI.RESET);
        System.out.println(ANSI.CYAN_BOLD + "          ANÁLISIS DE GRAFOS - TALLER 8" + ANSI.RESET);
        System.out.println(ANSI.CYAN_BOLD + "============================================================" + ANSI.RESET);

        if (currentGraph != null) {
            System.out.println(" " + ANSI.GREEN_BOLD + "ESTADO: Grafo cargado con " + currentGraph.getnVertices() + " vértices." + ANSI.RESET);
            String tipo = currentGraph.isDirected() ? "Dirigido (Aristas con dirección)" : "No Dirigido (Bidireccional)";
            System.out.println("    " + ANSI.GREEN + "TIPO:   " + tipo + ANSI.RESET);
        } else {
            System.out.println(" " + ANSI.YELLOW_BOLD + "ESTADO: No hay ningún grafo cargado en memoria." + ANSI.RESET);
        }

        System.out.println(ANSI.CYAN_BOLD + "------------------------------------------------------------" + ANSI.RESET);
        System.out.println(ANSI.WHITE_BOLD + " 1. " + ANSI.RESET + "CARGAR GRAFO");
        System.out.println(ANSI.BRIGHT_BLACK + "    (Lee una matriz desde un archivo .txt)" + ANSI.RESET);
        System.out.println("");
        System.out.println(ANSI.WHITE_BOLD + " 2. " + ANSI.RESET + "EJECUTAR BFS (Búsqueda en Anchura)");
        System.out.println(ANSI.BRIGHT_BLACK + "    (Calcula el camino más corto en grafos no ponderados)" + ANSI.RESET);
        System.out.println("");
        System.out.println(ANSI.WHITE_BOLD + " 3. " + ANSI.RESET + "EJECUTAR DFS (Búsqueda en Profundidad)");
        System.out.println(ANSI.BRIGHT_BLACK + "    (Exploración exhaustiva de ramas)" + ANSI.RESET);
        System.out.println("");
        System.out.println(ANSI.WHITE_BOLD + " 4. " + ANSI.RESET + "ANÁLISIS AVANZADO");
        System.out.println(ANSI.BRIGHT_BLACK + "    (Detectar Ciclos y contar Componentes Conexas)" + ANSI.RESET);
        System.out.println("");
        System.out.println(ANSI.WHITE_BOLD + " 5. " + ANSI.RESET + "VER ESTRUCTURA");
        System.out.println(ANSI.BRIGHT_BLACK + "    (Imprime la Lista de Adyacencia actual)" + ANSI.RESET);
        System.out.println("");
        System.out.println(ANSI.WHITE_BOLD + " 6. " + ANSI.RESET + "SALIR");
        System.out.println(ANSI.CYAN_BOLD + "============================================================" + ANSI.RESET);
    }

    private static void cargarGrafoInteractivo(Path carpeta) {
        try {
            System.out.println("\n" + ANSI.CYAN + "--- CARGAR NUEVO GRAFO ---" + ANSI.RESET);

            if (!carpeta.toFile().exists()) {
                carpeta = Paths.get(".");
            }

            SelectorDeArchivo selector = new SelectorDeArchivo();
            Path archivoSeleccionado = selector.escogerArchivoDeCarpeta(carpeta);

            System.out.println("\nLeyendo archivo: " + ANSI.YELLOW + archivoSeleccionado.getFileName() + ANSI.RESET + "...");
            int[][] matriz = LectorMatriz.cargar(archivoSeleccionado.toString());

            System.out.print("Validando estructura de la matriz... ");
            ValidadorMatriz.validar(matriz);
            System.out.println(ANSI.GREEN + "OK." + ANSI.RESET);

            currentGraph = new Graph(matriz);
            System.out.println("\n" + ANSI.GREEN_BOLD + "¡Grafo cargado exitosamente!" + ANSI.RESET);
            System.out.println("   -> Vértices detectados: " + ANSI.CYAN + currentGraph.getnVertices() + ANSI.RESET);
            System.out.println("   -> Modo: " + ANSI.CYAN + (currentGraph.isDirected() ? "Dirigido" : "No Dirigido") + ANSI.RESET);

        } catch (IOException e) {
            System.err.println(ANSI.RED_BOLD + "\nError de lectura/archivo: " + e.getMessage() + ANSI.RESET);
        } catch (IllegalArgumentException e) {
            System.err.println(ANSI.RED_BOLD + "\nLa matriz del archivo no es válida: " + e.getMessage() + ANSI.RESET);
        }
    }

    private static void ejecutarBFS() {
        if (currentGraph == null) {
            System.out.println(ANSI.RED_BOLD + "\nError: Primero debe cargar un grafo (Opción 1)." + ANSI.RESET);
            return;
        }

        System.out.println("\n" + ANSI.CYAN_BOLD + "--- BFS (Búsqueda en Anchura) ---" + ANSI.RESET);
        System.out.println(ANSI.BRIGHT_BLACK + "Este algoritmo visitará los nodos por 'niveles'." + ANSI.RESET);

        int startNode = solicitarNodo("Ingrese el nodo de inicio (Raíz)");
        if (startNode == -1) return;

        List<Integer> orden = currentGraph.bfs(startNode);
        System.out.println("\n" + ANSI.GREEN + "Orden de visita (BFS): " + orden + ANSI.RESET);

        System.out.println("\n" + ANSI.YELLOW + "Tabla de Distancias Mínimas desde el nodo " + startNode + ":" + ANSI.RESET);
        GraphAnalizer.imprimirTablaDistancias(currentGraph);
    }

    private static void ejecutarDFS() {
        if (currentGraph == null) {
            System.out.println(ANSI.RED_BOLD + "\nError: Primero debe cargar un grafo (Opción 1)." + ANSI.RESET);
            return;
        }

        System.out.println("\n" + ANSI.CYAN_BOLD + "--- DFS (Búsqueda en Profundidad) ---" + ANSI.RESET);
        System.out.println(ANSI.BRIGHT_BLACK + "Este algoritmo explorará cada rama hasta el final antes de retroceder." + ANSI.RESET);

        int startNode = solicitarNodo("Ingrese el nodo de inicio");
        if (startNode == -1) return;

        List<Integer> orden = currentGraph.dfs(startNode);
        System.out.println("\n" + ANSI.GREEN + "Orden de visita (DFS): " + orden + ANSI.RESET);
    }

    private static void analizarExtensiones() {
        if (currentGraph == null) {
            System.out.println(ANSI.RED_BOLD + "\nError: Primero debe cargar un grafo." + ANSI.RESET);
            return;
        }

        System.out.println("\n" + ANSI.CYAN_BOLD + "--- Análisis Avanzado del Grafo ---" + ANSI.RESET);

        // 1. Ciclos
        boolean tieneCiclo = GraphAnalizer.detectarCiclo(currentGraph);
        System.out.print(ANSI.WHITE_BOLD + "1. Detección de Ciclos: " + ANSI.RESET);
        if (tieneCiclo) {
            // CAMBIO: Usamos MAGENTA para indicar información/propiedad, no error.
            System.out.println(ANSI.MAGENTA_BOLD + "SE DETECTARON CICLOS." + ANSI.RESET);
            System.out.println(ANSI.MAGENTA + "   (El grafo contiene al menos un camino cerrado)" + ANSI.RESET);
        } else {
            System.out.println(ANSI.GREEN_BOLD + "NO HAY CICLOS." + ANSI.RESET);
            System.out.println(ANSI.GREEN + "   (Es un Grafo Acíclico o Árbol)" + ANSI.RESET);
        }

        // 2. Componentes
        System.out.print(ANSI.WHITE_BOLD + "2. Conectividad: " + ANSI.RESET);
        if (!currentGraph.isDirected()) {
            int componentes = currentGraph.countConnectedComponents();
            System.out.println("El grafo tiene " + ANSI.YELLOW_BOLD + componentes + ANSI.RESET + " componente(s) conexa(s).");
            if (componentes == 1) {
                System.out.println(ANSI.GREEN + "   (Todos los vértices están conectados entre sí)" + ANSI.RESET);
            } else {
                System.out.println(ANSI.YELLOW + "   (El grafo está fragmentado en islas aisladas)" + ANSI.RESET);
            }
        } else {
            System.out.println(ANSI.BRIGHT_BLACK + "Análisis de componentes omitido (El grafo es Dirigido)." + ANSI.RESET);
        }
    }

    private static void mostrarInformacionGrafo() {
        if (currentGraph == null) {
            System.out.println(ANSI.RED_BOLD + "\nNo hay grafo cargado." + ANSI.RESET);
            return;
        }
        System.out.println("\n" + ANSI.CYAN_BOLD + "--- Estructura Interna del Grafo ---" + ANSI.RESET);
        System.out.println(currentGraph.toString());
    }

    // --- Helpers de Consola ---

    private static int solicitarNodo(String mensaje) {
        int n = currentGraph.getnVertices();
        int nodo = getIntInput(ANSI.YELLOW + mensaje + " (0 a " + (n - 1) + "): " + ANSI.RESET);
        if (nodo < 0 || nodo >= n) {
            System.out.println(ANSI.RED_BOLD + "Nodo inválido: Debe ser un número entre 0 y " + (n-1) + ANSI.RESET);
            return -1;
        }
        return nodo;
    }

    private static int getIntInput(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.println(ANSI.RED + "Entrada inválida. Por favor ingrese un número entero." + ANSI.RESET);
            scanner.next();
            System.out.print(prompt);
        }
        int res = scanner.nextInt();
        scanner.nextLine();
        return res;
    }
}
// Comentario de prueba para git