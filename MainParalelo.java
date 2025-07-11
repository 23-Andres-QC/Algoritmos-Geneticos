import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class MainParalelo {
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
            
            try (PrintWriter log = new PrintWriter(new FileWriter("log_generaciones_paralelo.txt"))) {
                while (generacion < MAX_GENERACIONES && !solucionEncontrada) {
                    System.out.println("Generación " + generacion + "...");

                    log.println("\n===== GENERACIÓN " + generacion + " =====");
                    log.println("==== Población ====");
                    escribirMatriz(log, poblacionActual);

                    log.println("==== Puntajes ====");
                    escribirMatriz(log, puntajes);
                    int[] scores = MutacionParalelo.calcularScoresPorFila(puntajes);

                    // Verificar si hay un cromosoma perfecto
                    for (int i = 0; i < scores.length; i++) {
                        if (scores[i] == puntajes[0].length) {
                            log.println("\n ¡Solución encontrada en la generación " + generacion + "!");
                            log.print("Genoma solución: ");
                            for (int gen : poblacionActual[i]) log.print("[" + gen + "] ");
                            log.println();

                            System.out.println(" Solución encontrada en generación " + generacion);
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
                    log.println("\n No se encontró solución perfecta en " + MAX_GENERACIONES + " generaciones.");
                    System.out.println(" No se encontró solución perfecta en " + MAX_GENERACIONES + " generaciones.");
                }
                long tiempoFin = System.currentTimeMillis();
                double segundos = (tiempoFin - tiempoInicio) / 1000.0;
                System.out.printf("\nTiempo total de ejecución: %.3f segundos\n", segundos);
            } catch (IOException e) {
                System.err.println("Error al escribir archivo de log: " + e.getMessage());
            }
        } finally {
            scanner.close();
            poolDeHilos.apagar();
            System.out.println("PoolDeHilos cerrado correctamente.");
        }
    }

    private static void escribirMatriz(PrintWriter log, Integer[][] matriz) {
        for (Integer[] fila : matriz) {
            for (Integer val : fila) {
                log.printf("[%3d] ", val);
            }
            log.println();
        }
    }
}
