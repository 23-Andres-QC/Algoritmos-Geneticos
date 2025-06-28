import java.util.Scanner;

public class MainParalelo2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        PoolDeHilos poolDeHilos = PoolDeHilos.obtenerInstancia();
        long tiempoInicio = System.currentTimeMillis();
        try {
            CrearSudokuParalelo sudoku = new CrearSudokuParalelo();

            System.out.print("Tamaño del Sudoku (ej: 4, 9, 16): ");
            int N = scanner.nextInt();
            System.out.print("¿Ingrese el número de cromosomas que desea? ");
            int Ncromosomas = scanner.nextInt();

            // Generar Sudoku base usando paralelismo
            Integer[][] MSudoku = sudoku.generarSudoku(N);
            sudoku.imprimirTablero(MSudoku);

            // Crear población inicial usando paralelismo
            PoblacionParalelo generador = new PoblacionParalelo();
            Integer[][] poblacionActual = generador.CrearPoblacion(MSudoku, N, Ncromosomas);
            Integer[][] puntajes = SacarScoreParalelo.evaluarSoluciones(MSudoku, poblacionActual);

            final int MAX_GENERACIONES = 500000;
            int generacion = 0;
            boolean solucionEncontrada = false;

            while (generacion < MAX_GENERACIONES && !solucionEncontrada) {
                System.out.println("\n===== GENERACIÓN " + generacion + " =====");
                System.out.println("==== Población ====");
                imprimirMatriz(poblacionActual);
                System.out.println("==== Puntajes ====");
                imprimirMatriz(puntajes);

                int[] scores = MutacionParalelo.calcularScoresPorFila(puntajes);

                // Verificar si hay un cromosoma perfecto
                for (int i = 0; i < scores.length; i++) {
                    if (scores[i] == puntajes[0].length) {
                        System.out.println("\n¡Solución encontrada en la generación " + generacion + "!");
                        System.out.print("Genoma solución: ");
                        for (int gen : poblacionActual[i]) System.out.print("[" + gen + "] ");
                        System.out.println();
                        solucionEncontrada = true;
                        break;
                    }
                }

                if (!solucionEncontrada) {
                    poblacionActual = MutacionParalelo.reproducirPoblacionFija(poblacionActual, puntajes, N);
                    puntajes = SacarScoreParalelo.evaluarSoluciones(MSudoku, poblacionActual);
                    generacion++;
                }
            }

            if (!solucionEncontrada) {
                System.out.println("\nNo se encontró solución perfecta en " + MAX_GENERACIONES + " generaciones.");
            }
            long tiempoFin = System.currentTimeMillis();
            double segundos = (tiempoFin - tiempoInicio) / 1000.0;
            System.out.printf("\nTiempo total de ejecución: %.3f segundos\n", segundos);
        } finally {
            scanner.close();
            poolDeHilos.apagar();
            System.out.println("PoolDeHilos cerrado correctamente.");
        }
    }

    private static void imprimirMatriz(Integer[][] matriz) {
        for (Integer[] fila : matriz) {
            for (Integer val : fila) {
                System.out.printf("[%3d] ", val);
            }
            System.out.println();
        }
    }
}
