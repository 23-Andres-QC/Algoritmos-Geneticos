import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class MainPrincipal {
    private static int N; 
    private static int Ncromosomas;

    private static void ejecutarSerial(Integer[][] matriz, int Ncromosomas) {
        long tiempoInicioSerial = System.currentTimeMillis();

        Poblacion generadorSerial = new Poblacion();
        Integer[][] poblacionSerial = generadorSerial.CrearPoblacion(matriz, N, Ncromosomas);
        Integer[][] puntajesSerial = SacarScore.evaluarSoluciones(matriz, poblacionSerial);
        final int MAX_GENERACIONES = 500000;
        int generacionSerial = 0;
        boolean solucionSerial = false;
        try (PrintWriter log = new PrintWriter(new FileWriter("log_generaciones_serial.txt"))) {
            while (generacionSerial < MAX_GENERACIONES && !solucionSerial) {
                log.println("\n===== GENERACIÓN " + generacionSerial + " =====");
                log.println("==== Población ====");
                escribirMatriz(log, poblacionSerial);
                log.println("==== Puntajes ====");
                escribirMatriz(log, puntajesSerial);
                int[] scores = Mutacion.calcularScoresPorFila(puntajesSerial);
                for (int i = 0; i < scores.length; i++) {
                    if (scores[i] == puntajesSerial[0].length) {
                        log.println("\n ¡Solución encontrada en la generación " + generacionSerial + "!");
                        log.print("Genoma solución: ");
                        for (int gen : poblacionSerial[i]) log.print("[" + gen + "] ");
                        log.println();
                        solucionSerial = true;
                        break;
                    }
                }
                if (!solucionSerial) {
                    poblacionSerial = Mutacion.reproducirPoblacionFija(poblacionSerial, puntajesSerial, N);
                    puntajesSerial = SacarScore.evaluarSoluciones(matriz, poblacionSerial);
                    generacionSerial++;
                }
            }
            if (!solucionSerial) {
                log.println("\n No se encontró solución perfecta en " + MAX_GENERACIONES + " generaciones.");
            }
            long tiempoFinSerial = System.currentTimeMillis();
            double segundosSerial = (tiempoFinSerial - tiempoInicioSerial) / 1000.0;
            System.out.printf("\n[Serial] Tiempo total de ejecución: %.3f segundos\n", segundosSerial);
        } catch (IOException e) {
            System.err.println("Error al escribir archivo de log serial: " + e.getMessage());
        }
    }

    private static void ejecutarParalelo(Integer[][] matriz, int Ncromosomas) {
        PoolDeHilos poolDeHilos = PoolDeHilos.obtenerInstancia();
        long tiempoInicioParalelo = System.currentTimeMillis();
        
        PoblacionParalelo generadorParalelo = new PoblacionParalelo();
        Integer[][] poblacionParalelo = generadorParalelo.CrearPoblacion(matriz, N, Ncromosomas);
        Integer[][] puntajesParalelo = SacarScoreParalelo.evaluarSoluciones(matriz, poblacionParalelo);
        final int MAX_GENERACIONES = 500000;
        int generacionParalelo = 0;
        boolean solucionParalelo = false;
        try (PrintWriter log = new PrintWriter(new FileWriter("log_generaciones_paralelo.txt"))) {
            while (generacionParalelo < MAX_GENERACIONES && !solucionParalelo) {
                log.println("\n===== GENERACIÓN " + generacionParalelo + " =====");
                log.println("==== Población ====");
                escribirMatriz(log, poblacionParalelo);
                log.println("==== Puntajes ====");
                escribirMatriz(log, puntajesParalelo);
                int[] scores = MutacionParalelo.calcularScoresPorFila(puntajesParalelo);
                for (int i = 0; i < scores.length; i++) {
                    if (scores[i] == puntajesParalelo[0].length) {
                        log.println("\n ¡Solución encontrada en la generación " + generacionParalelo + "!");
                        log.print("Genoma solución: ");
                        for (int gen : poblacionParalelo[i]) log.print("[" + gen + "] ");
                        log.println();
                        solucionParalelo = true;
                        break;
                    }
                }
                if (!solucionParalelo) {
                    poblacionParalelo = MutacionParalelo.reproducirPoblacionFija(poblacionParalelo, puntajesParalelo, N);
                    puntajesParalelo = SacarScoreParalelo.evaluarSoluciones(matriz, poblacionParalelo);
                    generacionParalelo++;
                }
            }
            if (!solucionParalelo) {
                log.println("\n No se encontró solución perfecta en " + MAX_GENERACIONES + " generaciones.");
            }
            long tiempoFinParalelo = System.currentTimeMillis();
            double segundosParalelo = (tiempoFinParalelo - tiempoInicioParalelo) / 1000.0;
            System.out.printf("\n[Paralelo] Tiempo total de ejecución: %.3f segundos\n", segundosParalelo);
        } catch (IOException e) {
            System.err.println("Error al escribir archivo de log paralelo: " + e.getMessage());
        } finally {
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

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            CrearSudokuParalelo sudokuParalelo = new CrearSudokuParalelo();
            System.out.print("Tamaño del Sudoku (ej: 4, 9, 16): ");
            N = scanner.nextInt();
            System.out.print("¿Ingrese el número de cromosomas que desea? ");
            Ncromosomas = scanner.nextInt();
            Integer[][] matrizSudoku = sudokuParalelo.generarSudoku(N);
            MatrizActual matrizActual = new MatrizActual(matrizSudoku);
            System.out.println("Sudoku generado en paralelo:");
            sudokuParalelo.imprimirTablero(matrizActual.getMatriz());

            ejecutarSerial(matrizActual.getMatriz(), Ncromosomas);
            ejecutarParalelo(matrizActual.getMatriz(), Ncromosomas);
        }
    }
}
