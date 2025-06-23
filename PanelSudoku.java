import java.awt.*;
import javax.swing.*;

public class PanelSudoku extends JPanel {
    private final int tamano;
    private final JTextField[][] celdas;
    private final JTextArea areaCambiosGenes;
    private int[][] tableroAnterior;
    private final JButton btnSiguienteGen;
    private final JButton btnEjecutarAutomatico;
    private final JSlider sliderVelocidad;
    private int generacionActual;
    private final int generacionMaxima = 100; // Ejemplo de límite máximo
    private final JComboBox<String> cmbTamaño;

    public PanelSudoku(int tamano) {
        this.tamano = tamano;
        this.setLayout(new BorderLayout());

        // Panel de botones y control de velocidad
        JPanel panelBotones = new JPanel();
        btnSiguienteGen = new JButton("Siguiente Gen");
        btnEjecutarAutomatico = new JButton("Ejecutar Automático");
        sliderVelocidad = new JSlider(1, 10, 5); // Velocidad entre 1 y 10
        sliderVelocidad.setMajorTickSpacing(1);
        sliderVelocidad.setPaintTicks(true);
        sliderVelocidad.setPaintLabels(true);

        btnSiguienteGen.setEnabled(true); // Habilitar al inicio
        btnEjecutarAutomatico.setEnabled(true); // Habilitar al inicio

        panelBotones.add(new JLabel("Velocidad:"));
        panelBotones.add(sliderVelocidad);

        this.add(panelBotones, BorderLayout.NORTH);

        JPanel panelCuadricula = new JPanel(new GridLayout(tamano, tamano));
        this.celdas = new JTextField[tamano][tamano];
        for (int fila = 0; fila < tamano; fila++) {
            for (int col = 0; col < tamano; col++) {
                celdas[fila][col] = new JTextField();
                celdas[fila][col].setHorizontalAlignment(JTextField.CENTER);
                panelCuadricula.add(celdas[fila][col]);
            }
        }
        this.add(panelCuadricula, BorderLayout.CENTER);
        areaCambiosGenes = new JTextArea(10, 15);
        areaCambiosGenes.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(areaCambiosGenes);
        this.add(scrollPane, BorderLayout.EAST);

        // ComboBox para seleccionar tamaño
        cmbTamaño = new JComboBox<>();
        for (int i = 2; i <= 100; i++) {
            cmbTamaño.addItem(String.valueOf(i));
        }

        cmbTamaño.addActionListener(e -> {
            String nuevoTamanoStr = (String) cmbTamaño.getSelectedItem();
            int nuevoTamano = Integer.parseInt(nuevoTamanoStr);

            // Reiniciar el programa con el nuevo tamaño
            SwingUtilities.invokeLater(() -> {
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
                frame.dispose(); // Cerrar la ventana actual

                // Crear una nueva ventana con el nuevo tamaño
                MainWindow nuevaVentana = new MainWindow(nuevoTamano);
                nuevaVentana.setVisible(true);
            });
        });

        panelBotones.add(new JLabel("Tamaño:"));
        panelBotones.add(cmbTamaño);

        tableroAnterior = null;

        // Configurar funcionalidad de botones
        btnEjecutarAutomatico.addActionListener(e -> {
            generacionActual = 0; // Reiniciar contador al iniciar
            Timer timer = new Timer(1000 / sliderVelocidad.getValue(), event -> {
                generacionActual++;
                // Lógica para avanzar generación

                // Simulación de finalización del proceso
                if (generacionActual >= generacionMaxima) { // Condición de finalización
                    ((Timer) event.getSource()).stop();
                    SwingUtilities.invokeLater(() -> {
                        int option = JOptionPane.showOptionDialog(
                            this,
                            "No hay más generaciones disponibles. ¿Qué desea hacer?",
                            "Generaciones Completadas",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.INFORMATION_MESSAGE,
                            null,
                            new String[]{"Reiniciar", "Cerrar"},
                            "Cerrar"
                        );

                        if (option == JOptionPane.YES_OPTION) {
                            generacionActual = 0;
                            // Reiniciar tablero o lógica adicional
                        } else if (option == JOptionPane.NO_OPTION) {
                            System.exit(0);
                        }
                    });
                }
            });
            timer.start();
        });

        btnSiguienteGen.addActionListener(e -> {
            // Lógica para avanzar una generación
        });
    }

    public int[][] obtenerTableroEntrada() {
        int[][] tablero = new int[tamano][tamano];
        for (int fila = 0; fila < tamano; fila++) {
            for (int col = 0; col < tamano; col++) {
                try {
                    String texto = celdas[fila][col].getText().trim();
                    tablero[fila][col] = texto.isEmpty() ? 0 : Integer.parseInt(texto);
                } catch (NumberFormatException e) {
                    tablero[fila][col] = 0;
                }
            }
        }
        return tablero;
    }

    public void establecerTablero(int[][] tablero) {
        StringBuilder cambios = new StringBuilder();
        if (tableroAnterior != null) {
            for (int fila = 0; fila < tamano; fila++) {
                for (int col = 0; col < tamano; col++) {
                    if (tableroAnterior[fila][col] != tablero[fila][col]) {
                        cambios.append(String.format("Celda [%d,%d]: %d → %d\n", fila+1, col+1, tableroAnterior[fila][col], tablero[fila][col]));
                    }
                }
            }
        }
        for (int fila = 0; fila < tamano; fila++) {
            for (int col = 0; col < tamano; col++) {
                celdas[fila][col].setText(tablero[fila][col] == 0 ? "" : String.valueOf(tablero[fila][col]));
            }
        }
        tableroAnterior = new int[tamano][tamano];
        for (int fila = 0; fila < tamano; fila++) {
            System.arraycopy(tablero[fila], 0, tableroAnterior[fila], 0, tamano);
        }
        areaCambiosGenes.setText(cambios.length() > 0 ? cambios.toString() : "Sin cambios de genes");
    }

    public void establecerTablero(int[][] tablero, int[][] anterior) {
        StringBuilder cambios = new StringBuilder();
        // Limpiar colores
        for (int fila = 0; fila < tamano; fila++) {
            for (int col = 0; col < tamano; col++) {
                celdas[fila][col].setBackground(Color.WHITE);
            }
        }
        if (anterior != null) {
            for (int fila = 0; fila < tamano; fila++) {
                for (int col = 0; col < tamano; col++) {
                    if (anterior[fila][col] != tablero[fila][col]) {
                        cambios.append(String.format("Celda [%d,%d]: %d → %d\n", fila+1, col+1, anterior[fila][col], tablero[fila][col]));
                        celdas[fila][col].setBackground(new Color(255, 255, 150)); // Amarillo suave
                    }
                }
            }
        }
        for (int fila = 0; fila < tamano; fila++) {
            for (int col = 0; col < tamano; col++) {
                celdas[fila][col].setText(tablero[fila][col] == 0 ? "" : String.valueOf(tablero[fila][col]));
            }
        }
        tableroAnterior = new int[tamano][tamano];
        for (int fila = 0; fila < tamano; fila++) {
            System.arraycopy(tablero[fila], 0, tableroAnterior[fila], 0, tamano);
        }
        areaCambiosGenes.setText(cambios.length() > 0 ? cambios.toString() : "Sin cambios de genes");
    }

    public void setEditable(boolean editable) {
        for (int fila = 0; fila < tamano; fila++)
            for (int col = 0; col < tamano; col++)
                celdas[fila][col].setEditable(editable);
    }
}
