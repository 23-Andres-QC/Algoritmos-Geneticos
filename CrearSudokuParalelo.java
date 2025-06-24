import java.util.*;

public class CrearSudokuParalelo {

    private int N;
    private int SQRT_N;
    private Integer[][] tablero;
    private Random random;

    public Integer[][] generarSudoku(int tamano) {
        double raiz = Math.sqrt(tamano);
        if (raiz != (int) raiz)
            throw new IllegalArgumentException("El tamaño debe tener raíz cuadrada entera (por ejemplo: 4, 9, 16).");

        this.N = tamano;
        this.SQRT_N = (int) raiz;
        this.tablero = new Integer[N][N];
        this.random = new Random();

        resolverSudoku();
        removerCeldas((int) (N * N * 0.6));

        return tablero;
    }

    public void imprimirTablero(Integer[][] tablero) {
        for (Integer[] fila : tablero) {
            for (Integer val : fila) {
                System.out.printf("%3s", val == null ? "." : val);
            }
            System.out.println();
        }
    }

    private boolean esValido(int fila, int col, int num) {
        final boolean[] esFilaValida = {true};
        final boolean[] esColValida = {true};
        final boolean[] esSubcuadroValido = {true};

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < N; i++) {
                if (tablero[fila][i] != null && tablero[fila][i] == num) {
                    esFilaValida[0] = false;
                    break;
                }
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < N; i++) {
                if (tablero[i][col] != null && tablero[i][col] == num) {
                    esColValida[0] = false;
                    break;
                }
            }
        });

        Thread t3 = new Thread(() -> {
            int startRow = fila - fila % SQRT_N;
            int startCol = col - col % SQRT_N;
            for (int i = 0; i < SQRT_N; i++) {
                for (int j = 0; j < SQRT_N; j++) {
                    Integer val = tablero[startRow + i][startCol + j];
                    if (val != null && val == num) {
                        esSubcuadroValido[0] = false;
                        return;
                    }
                }
            }
        });

        // Iniciar hilos
        t1.start();
        t2.start();
        t3.start();

        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return esFilaValida[0] && esColValida[0] && esSubcuadroValido[0];
    }

    private boolean resolverSudoku() {
        for (int fila = 0; fila < N; fila++) {
            for (int col = 0; col < N; col++) {
                if (tablero[fila][col] == null) {
                    List<Integer> numeros = generarNumerosAleatorios();
                    for (int num : numeros) {
                        if (esValido(fila, col, num)) {
                            tablero[fila][col] = num;
                            if (resolverSudoku()) return true;
                            tablero[fila][col] = null;
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private List<Integer> generarNumerosAleatorios() {
        List<Integer> numeros = new ArrayList<>();
        for (int i = 1; i <= N; i++) {
            numeros.add(i);
        }
        Collections.shuffle(numeros, random);
        return numeros;
    }

    private void removerCeldas(int cantidad) {
        int eliminadas = 0;
        while (eliminadas < cantidad) {
            int fila = random.nextInt(N);
            int col = random.nextInt(N);
            if (tablero[fila][col] != null) {
                tablero[fila][col] = null;
                eliminadas++;
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Tamaño del Sudoku (ej: 4, 9, 16): ");
        int n = scanner.nextInt();

        CrearSudoku generador = new CrearSudoku();
        Integer[][] sudoku = generador.generarSudoku(n);

        System.out.println("\nSudoku generado:");
        generador.imprimirTablero(sudoku);
    }
}
