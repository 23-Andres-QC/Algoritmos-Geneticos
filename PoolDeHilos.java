import java.util.ArrayList;
import java.util.List;

public class PoolDeHilos {
    private static PoolDeHilos instancia;
    private final Thread[] trabajadores;
    private final ColaDeTareas colaDeTareas;
    private final int numeroDeHilos;
    private volatile boolean apagado = false;
    
    private PoolDeHilos() {
        // Usar solo 75% de los procesadores disponibles
        int procesadoresDisponibles = Runtime.getRuntime().availableProcessors();
        this.numeroDeHilos = Math.max(2, (int)(procesadoresDisponibles * 0.75));
        this.colaDeTareas = new ColaDeTareas();
        this.trabajadores = new Thread[numeroDeHilos];
        
        // Crear hilos trabajadores sin iniciarlos aún
        for (int i = 0; i < numeroDeHilos; i++) {
            trabajadores[i] = new Thread(new Trabajador());
        }
        
        System.out.println("Pool de hilos inicializado con " + numeroDeHilos + " hilos (de " + procesadoresDisponibles + " disponibles)");
    }
    
    public static PoolDeHilos obtenerInstancia() {
        if (instancia == null) {
            instancia = new PoolDeHilos();
            instancia.iniciarHilos();
        }
        return instancia;
    }
    
    private void iniciarHilos() {
        for (Thread trabajador : trabajadores) {
            trabajador.start();
        }
    }
    
    
    // Enviar una tarea al pool
    public void enviar(Runnable tarea) {
        if (!apagado) {
            colaDeTareas.agregarTarea(tarea);
        }
    }
    
    // Esperar a que terminen todas las tareas pendientes
    public void esperarFinalizacion() {
        synchronized (colaDeTareas) {
            while (!colaDeTareas.estaVacia() || colaDeTareas.tieneTareasActivas()) {
                try {
                    colaDeTareas.wait(100); // Esperar con timeout
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
    
    public void apagar() {
        apagado = true;
        colaDeTareas.apagar();
        
        // Esperar a que terminen todos los trabajadores
        for (Thread trabajador : trabajadores) {
            try {
                trabajador.join(5000); // Esperar máximo 5 segundos
            } catch (InterruptedException e) {
                trabajador.interrupt();
            }
        }
    }
    
    // Clase para manejar la cola de tareas
    private static class ColaDeTareas {
        private final List<Runnable> tareas = new ArrayList<>();
        private volatile boolean apagado = false;
        private volatile int tareasActivas = 0;
        
        public synchronized void agregarTarea(Runnable tarea) {
            if (!apagado) {
                tareas.add(tarea);
                notify(); // Despertar un hilo esperando
            }
        }
        
        public synchronized Runnable obtenerTarea() {
            while (tareas.isEmpty() && !apagado) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
            
            if (!tareas.isEmpty()) {
                tareasActivas++;
                return tareas.remove(0);
            }
            return null;
        }
        
        public synchronized void tareaCompletada() {
            tareasActivas--;
            notifyAll(); // Notificar siempre que se complete una tarea
        }
        
        public synchronized boolean estaVacia() {
            return tareas.isEmpty();
        }
        
        public synchronized boolean tieneTareasActivas() {
            return tareasActivas > 0;
        }
        
        public synchronized void apagar() {
            apagado = true;
            notifyAll();
        }
    }
    
    // Clase worker que ejecuta las tareas
    private class Trabajador implements Runnable {
        @Override
        public void run() {
            while (!apagado) {
                Runnable tarea = colaDeTareas.obtenerTarea();
                if (tarea != null) {
                    try {
                        tarea.run();
                    } catch (Exception e) {
                        System.err.println("Error ejecutando tarea: " + e.getMessage());
                    } finally {
                        colaDeTareas.tareaCompletada();
                    }
                }
            }
        }
    }
    
}
