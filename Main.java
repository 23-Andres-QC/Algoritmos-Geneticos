import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CrearSudoku sudoku = new CrearSudoku();

        System.out.print("Tamaño del Sudoku (ej: 4, 9, 16): ");
        int N = scanner.nextInt();

        // Generar Sudoku base con espacios vacíos
        Integer[][] MSudoku = sudoku.generarSudoku(N);
        sudoku.imprimirTablero(MSudoku);

        // Crear población inicial
        Poblacion generador = new Poblacion();
        Integer[][] poblacionActual = generador.CrearPoblacion(MSudoku, N);

        // Evaluar puntajes
        Integer[][] puntajes = SacarScore.evaluarSoluciones(MSudoku, poblacionActual);

        final int MAX_GENERACIONES = 100;
        int generacion = 0;
        boolean solucionEncontrada = false;

        while (generacion < MAX_GENERACIONES && !solucionEncontrada) {
            System.out.println("\n===== GENERACIÓN " + generacion + " =====");
            System.out.println("==== Población ====");
            generador.imprimirMatriz(poblacionActual);

            System.out.println("==== Puntajes ====");
            generador.imprimirMatriz(puntajes);

            // Calcular score por fila (número total de 1's)
            int[] scores = Mutacion.calcularScoresPorFila(puntajes);

            for (int i = 0; i < scores.length; i++) {
                if (scores[i] == puntajes[0].length) {
                    System.out.println("\n✅ ¡Solución encontrada en la generación " + generacion + "!");
                    System.out.print("Genoma solución: ");
                    for (int gen : poblacionActual[i]) {
                        System.out.print("[" + gen + "] ");
                    }
                    solucionEncontrada = true;
                    break;
                }
            }

            if (!solucionEncontrada) {
                // Generar nueva población con hijos y padres aptos
                int cantidadMaxima = poblacionActual.length * poblacionActual.length;
                int[][] relacionesPadres = new int[cantidadMaxima][2];

                Integer[][] nuevaGeneracion = Mutacion.NuevaGeneracionExtendidaMatriz(poblacionActual, puntajes, N, relacionesPadres);

                // Evaluar nueva generación
                poblacionActual = nuevaGeneracion;
                puntajes = SacarScore.evaluarSoluciones(MSudoku, poblacionActual);
                generacion++;
            }
        }

        if (!solucionEncontrada) {
            System.out.println("\n⚠️ No se encontró solución perfecta en " + MAX_GENERACIONES + " generaciones.");
        }

        scanner.close();
    }
}
