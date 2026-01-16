package utils;

import structures.Graph;
import java.util.List;

/**
 * Clase auxiliar para reportes y algoritmos extendidos no incluidos en Graph.
 */
public class GraphAnalizer {

    /**
     * Imprime una tabla formateada con las distancias calculadas previamente.
     * PRECONDICIÓN: Se debe haber ejecutado BFS previamente en el grafo.
     */
    public static void imprimirTablaDistancias(Graph graph) {
        int[] distances = graph.getDistances();
        int n = graph.getnVertices();

        if (distances == null) {
            System.out.println("No hay distancias calculadas. Ejecute BFS primero.");
            return;
        }

        System.out.println("\n   +---------+-------------+");
        System.out.println("   | Vértice |  Distancia  |");
        System.out.println("   +---------+-------------+");
        for (int i = 0; i < n; i++) {
            String dStr = (distances[i] == -1) ? "INF" : String.valueOf(distances[i]);
            System.out.printf("   |    %2d   |     %3s     |\n", i, dStr);
        }
        System.out.println("   +---------+-------------+");
    }

    /**
     * Detecta si existe al menos un ciclo en el grafo.
     * Utiliza DFS recursivo buscando back-edges
     */
    public static boolean detectarCiclo(Graph graph) {
        int n = graph.getnVertices();
        boolean[] visited = new boolean[n];
        boolean[] recStack = new boolean[n]; // Solo necesario para dirigidos
        List<List<Integer>> adj = graph.getAdjList();

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                if (graph.isDirected()) {
                    if (dfsCicloDirigido(i, visited, recStack, adj)) return true;
                } else {
                    if (dfsCicloNoDirigido(i, -1, visited, adj)) return true;
                }
            }
        }
        return false;
    }

    private static boolean dfsCicloDirigido(int u, boolean[] visited, boolean[] recStack, List<List<Integer>> adj) {
        visited[u] = true;
        recStack[u] = true;

        for (int v : adj.get(u)) {
            if (!visited[v]) {
                if (dfsCicloDirigido(v, visited, recStack, adj)) return true;
            } else if (recStack[v]) {
                // Si el vecino ya está en la pila de recursión actual, es un ciclo.
                return true;
            }
        }
        recStack[u] = false;
        return false;
    }

    private static boolean dfsCicloNoDirigido(int u, int parent, boolean[] visited, List<List<Integer>> adj) {
        visited[u] = true;
        for (int v : adj.get(u)) {
            if (!visited[v]) {
                if (dfsCicloNoDirigido(v, u, visited, adj)) return true;
            } else if (v != parent) {
                // Si visitamos un nodo ya visitado que NO es nuestro padre directo, es un ciclo
                return true;
            }
        }
        return false;
    }
}