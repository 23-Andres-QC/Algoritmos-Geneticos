import java.awt.*;
import java.util.List;
import javax.swing.*;

public class MainWindow extends JFrame {
    private PanelSudoku sudokuPanel;
    private final JButton btnIniciar, btnSiguiente, btnPausar, btnReanudar;
    private JComboBox<String> cmbTamaño;
    private List<Individuo> generaciones;
    private int currentGen = 0;
    private DefaultListModel<GeneracionInfo> evolucionModel;
    private JList<GeneracionInfo> evolucionList;
    private Timer animTimer;
    private boolean isPaused = false;

    public MainWindow(int tamano) {
        setTitle("Visualizador Genético Sudoku");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 750);
        setLayout(new BorderLayout());

        // Panel superior
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Tamaño:"));
        cmbTamaño = new JComboBox<>();
        for (int i = 2; i <= 100; i++) {
            cmbTamaño.addItem(String.valueOf(i));
        }
        topPanel.add(cmbTamaño);
        btnIniciar = new JButton("➡ Siguiente Gen");
        topPanel.add(btnIniciar);
        btnSiguiente = new JButton("▶ Ejecutar Automático");
        btnSiguiente.setEnabled(false);
        topPanel.add(btnSiguiente);
        btnPausar = new JButton("⏸ Pausar");
        btnPausar.setEnabled(false);
        topPanel.add(btnPausar);
        btnReanudar = new JButton("▶ Reanudar");
        btnReanudar.setEnabled(false);
        topPanel.add(btnReanudar);
        add(topPanel, BorderLayout.NORTH);

        // Sudoku central
        sudokuPanel = new PanelSudoku(tamano);
        add(sudokuPanel, BorderLayout.CENTER);

        // Panel derecho (historial de generaciones)
        evolucionModel = new DefaultListModel<>();
        evolucionList = new JList<>(evolucionModel);
        evolucionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        evolucionList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int idx = evolucionList.getSelectedIndex();
                if (generaciones != null && idx >= 0 && idx < generaciones.size()) {
                    currentGen = idx;
                    int[][] prev = (idx > 0) ? generaciones.get(idx-1).tablero : null;
                    sudokuPanel.establecerTablero(generaciones.get(idx).tablero, prev);
                    setTitle("Generación " + idx + " | Fitness: " + generaciones.get(idx).fitness);
                }
            }
        });
        JScrollPane evolucionScroll = new JScrollPane(evolucionList);
        evolucionScroll.setPreferredSize(new Dimension(220, 0));
        add(evolucionScroll, BorderLayout.EAST);

        // Cambio de tamaño de tablero
        cmbTamaño.addActionListener(e -> {
            String nuevoTamanoStr = (String) cmbTamaño.getSelectedItem();
            int nuevoTamano = Integer.parseInt(nuevoTamanoStr);

            // Actualizar el tamaño del Sudoku
            sudokuPanel = new PanelSudoku(nuevoTamano);
            getContentPane().removeAll(); // Eliminar todos los componentes actuales

            // Volver a agregar los componentes con el nuevo PanelSudoku
            add(topPanel, BorderLayout.NORTH);
            add(sudokuPanel, BorderLayout.CENTER);
            add(evolucionScroll, BorderLayout.EAST);

            // Reiniciar el estado del algoritmo
            generaciones = null;
            currentGen = 0;
            evolucionModel.clear();
            btnIniciar.setEnabled(true);
            btnSiguiente.setEnabled(false);
            btnPausar.setEnabled(false);
            btnReanudar.setEnabled(false);

            revalidate(); // Validar nuevamente el diseño
            repaint(); // Repintar la ventana para reflejar los cambios
        });

        // Botón siguiente generación (paso a paso desde el inicio)
        btnIniciar.addActionListener(e -> {
            if (generaciones == null) {
                int[][] board = sudokuPanel.obtenerTableroEntrada();
                SudokuGenetico solver = new SudokuGenetico(board, 100, 200, 0.2);
                generaciones = solver.resolverConEvolucion();
                currentGen = 0;
                sudokuPanel.setEditable(false);
                sudokuPanel.establecerTablero(generaciones.get(0).tablero, null);
                btnSiguiente.setEnabled(true);
                btnPausar.setEnabled(false);
                btnReanudar.setEnabled(false);
                evolucionModel.clear();
                for (int i = 0; i < generaciones.size(); i++) {
                    evolucionModel.addElement(new GeneracionInfo(i, generaciones.get(i).fitness));
                }
                evolucionList.setSelectedIndex(0);
            } else {
                if (currentGen < generaciones.size() - 1) {
                    currentGen++;
                    int[][] prev = generaciones.get(currentGen-1).tablero;
                    sudokuPanel.establecerTablero(generaciones.get(currentGen).tablero, prev);
                    setTitle("Generación " + currentGen + " | Fitness: " + generaciones.get(currentGen).fitness);
                    evolucionList.setSelectedIndex(currentGen);
                } else {
                    JOptionPane.showMessageDialog(this, "Última generación alcanzada");
                    btnIniciar.setEnabled(false);
                }
            }
        });

        // Botón ejecutar automático (animación más lenta)
        btnSiguiente.addActionListener(e -> {
            if (generaciones == null) return;
            if (animTimer != null && animTimer.isRunning()) animTimer.stop();
            animTimer = new Timer(1200, evt -> { // Animación más lenta
                if (!isPaused) {
                    currentGen++;
                    if (currentGen < generaciones.size()) {
                        int[][] prev = generaciones.get(currentGen-1).tablero;
                        sudokuPanel.establecerTablero(generaciones.get(currentGen).tablero, prev);
                        setTitle("Generación " + currentGen + " | Fitness: " + generaciones.get(currentGen).fitness);
                        evolucionList.setSelectedIndex(currentGen);
                    } else {
                        ((Timer)evt.getSource()).stop();
                        btnPausar.setEnabled(false);
                        btnReanudar.setEnabled(false);
                        btnSiguiente.setEnabled(false);
                        JOptionPane.showMessageDialog(MainWindow.this, "Última generación alcanzada");
                    }
                }
            });
            isPaused = false;
            animTimer.start();
            btnPausar.setEnabled(true);
            btnReanudar.setEnabled(false);
        });

        // Botón pausar animación
        btnPausar.addActionListener(e -> {
            isPaused = true;
            btnPausar.setEnabled(false);
            btnReanudar.setEnabled(true);
        });

        // Botón reanudar animación
        btnReanudar.addActionListener(e -> {
            isPaused = false;
            btnPausar.setEnabled(true);
            btnReanudar.setEnabled(false);
        });

        setVisible(true);
    }
}
