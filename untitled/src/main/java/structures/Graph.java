package structures;

import utils.Util;

import java.util.*;

/**
 * Representa un grafo utilizando una lista de adyacencia y una matriz de adyacencia.
 * Permite determinar si el grafo es dirigido o no basado en la matriz proporcionada.
 * Además, proporciona métodos para agregar aristas y obtener información sobre el grafo.
 * Los métodos de recorrido (BFS y DFS) ahora manejan grafos desconectados visitando
 * todos los componentes conectados.
 * @author Jaime Landázuri
 */

public class Graph {
    private final int nVertices;
    private final boolean isDirected;
    private final List<List<Integer>> adjList;
    private final int[][] matrix;

    private int[] distances;
    private int[] parents;

    public Graph(int[][] matrix) {
        this.matrix = matrix;
        // Número de vértices basado en el tamaño de la matriz
        this.nVertices = matrix.length;

        // Determinación de si el grafo es dirigido utilizando la función utilitaria
        this.isDirected = Util.isDirected(matrix, nVertices);

        // Inicialización de la lista de adyacencia
        this.adjList = new ArrayList<>(nVertices);

        // Creación de listas vacías para cada vértice
        for (int i = 0; i<nVertices; i++) {
            adjList.add(new ArrayList<>());
        }

        // Población de la lista de adyacencia basada en la matriz
        for (int i = 0; i < nVertices; i++) {
            for (int j = 0; j < nVertices; j++) {
                // Si hay conexión
                if (matrix[i][j] != 0) {
                    // si es dirigido agregamos todo
                    // si no, solo agregamos si j >= i (triangulo superior)
                    if (isDirected() || j >= i) {
                        addEdge(i, j);
                    }
                }
            }
        }
    }

    /**
     * Agrega una arista desde el vértice 'from' al vértice 'to'.
     * Si el grafo no es dirigido, también agrega la arista inversa.
     *
     * @param from Vértice de origen.
     * @param to   Vértice de destino.
     */
    public void addEdge(int from, int to) {
        if(from<0 || from>=nVertices || to<0 || to>=nVertices) return;
        adjList.get(from).add(to);
        if(!isDirected) {
            adjList.get(to).add(from);
        }
    }

    /**
     * Implementación de BFS que maneja grafos desconectados.
     * Visita todos los nodos del grafo, iniciando desde startNode y
     * continuando con nodos no visitados si el grafo tiene componentes desconectados.
     *
     * @param startNode Nodo desde el cual iniciar el recorrido
     * @return Lista con el orden de visita de todos los nodos
     */
    public List<Integer> bfs(int startNode) {
        List<Integer> traversalOrder = new ArrayList<>();

        // Validar nodo de inicio
        if (startNode < 0 || startNode >= nVertices) return traversalOrder;

        // Inicializar estructuras
        distances = new int[nVertices];
        parents = new int[nVertices];
        boolean[] visited = new boolean[nVertices];
        Arrays.fill(distances, -1);
        Arrays.fill(parents, -1);

        // Primer recorrido desde el nodo inicial
        bfsSingleComponent(startNode, visited, traversalOrder);

        // Buscar nodos no visitados y realizar BFS desde ellos
        for (int i = 0; i < nVertices; i++) {
            if (!visited[i]) {
                bfsSingleComponent(i, visited, traversalOrder);
            }
        }

        return traversalOrder;
    }

    /**
     * Realiza BFS desde un nodo específico visitando solo su componente conectado.
     *
     * @param startNode Nodo desde el cual iniciar
     * @param visited Array de nodos visitados (compartido entre componentes)
     * @param traversalOrder Lista para agregar el orden de visita
     */
    private void bfsSingleComponent(int startNode, boolean[] visited, List<Integer> traversalOrder) {
        Queue<Integer> queue = new LinkedList<>();

        // Configurar nodo inicial
        visited[startNode] = true;
        distances[startNode] = 0;
        queue.add(startNode);

        while (!queue.isEmpty()) {
            int u = queue.poll();
            traversalOrder.add(u);

            for (int v : adjList.get(u)) {
                if (!visited[v]) {
                    visited[v] = true;
                    distances[v] = distances[u] + 1;
                    parents[v] = u;
                    queue.add(v);
                }
            }
        }
    }

    /**
     * Implementación de DFS que maneja grafos desconectados.
     * Visita todos los nodos del grafo, iniciando desde startNode y
     * continuando con nodos no visitados si el grafo tiene componentes desconectados.
     *
     * @param startNode Nodo desde el cual iniciar el recorrido
     * @return Lista con el orden de visita de todos los nodos
     */
    public List<Integer> dfs(int startNode) {
        List<Integer> traversalOrder = new ArrayList<>();

        // Validar nodo de inicio
        if (startNode < 0 || startNode >= nVertices) return traversalOrder;

        // Inicializar estructuras
        distances = new int[nVertices];
        parents = new int[nVertices];
        boolean[] visited = new boolean[nVertices];

        Arrays.fill(distances, -1);
        Arrays.fill(parents, -1);

        // Primer recorrido desde el nodo inicial
        dfsVisit(startNode, visited, traversalOrder, 0);

        // Buscar nodos no visitados y realizar DFS desde ellos
        for (int i = 0; i < nVertices; i++) {
            if (!visited[i]) {
                dfsVisit(i, visited, traversalOrder, 0);
            }
        }

        return traversalOrder;
    }

    /**
     * Método auxiliar recursivo para DFS.
     *
     * @param u Nodo actual
     * @param visited Array de nodos visitados
     * @param traversalOrder Lista para agregar el orden de visita
     * @param depth Profundidad actual en el árbol DFS
     */
    private void dfsVisit(int u, boolean[] visited, List<Integer> traversalOrder, int depth) {
        visited[u] = true;
        distances[u] = depth;
        traversalOrder.add(u);

        for (int v : adjList.get(u)) {
            if (!visited[v]) {
                parents[v] = u;
                dfsVisit(v, visited, traversalOrder, depth + 1);
            }
        }
    }

    /**
     * Cuenta el número de componentes conectados en el grafo.
     * Útil para verificar si el grafo está desconectado.
     *
     * @return Número de componentes conectados
     */
    public int countConnectedComponents() {
        boolean[] visited = new boolean[nVertices];
        int components = 0;

        for (int i = 0; i < nVertices; i++) {
            if (!visited[i]) {
                components++;
                if (isDirected) {
                    // Para grafos dirigidos, usar DFS simple
                    dfsComponentCount(i, visited);
                } else {
                    // Para grafos no dirigidos, usar BFS simple
                    bfsComponentCount(i, visited);
                }
            }
        }
        return components;
    }

    /**
     * auxiliar para contar componentes usando DFS.
     */
    private void dfsComponentCount(int node, boolean[] visited) {
        visited[node] = true;
        for (int neighbor : adjList.get(node)) {
            if (!visited[neighbor]) {
                dfsComponentCount(neighbor, visited);
            }
        }
    }

    /**
     * auxiliar para contar componentes usando BFS.
     */
    private void bfsComponentCount(int startNode, boolean[] visited) {
        Queue<Integer> queue = new LinkedList<>();
        visited[startNode] = true;
        queue.add(startNode);

        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (int v : adjList.get(u)) {
                if (!visited[v]) {
                    visited[v] = true;
                    queue.add(v);
                }
            }
        }
    }

    public int getnVertices() { return nVertices; }
    public int[] getDistances() { return distances; }
    public int[] getParents() { return parents; }
    public boolean isDirected() { return isDirected; }
    public List<List<Integer>> getAdjList() { return adjList; }
    public int[][] getMatrix() { return matrix; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Graph (").append(isDirected ? "Directed" : "Undirected").append(") with ").append(nVertices).append(" vertices:\n");
        for (int i = 0; i < nVertices; i++) {
            sb.append(i).append(": ");
            for (Integer neighbor : adjList.get(i)) {
                sb.append(neighbor).append("->");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
<<<<<<< HEAD
}
=======
}
>>>>>>> 23bceec768f6904483538f5f4c940dd89485c463
