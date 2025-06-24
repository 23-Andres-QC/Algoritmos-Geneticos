import java.util.ArrayList;
import java.util.List;

public class ThreadPool {
    private static ThreadPool instance;
    private final Thread[] workers;
    private final TaskQueue taskQueue;
    private final int numThreads;
    private volatile boolean shutdown = false;
    
    private ThreadPool() {
        // Usar solo 75% de los procesadores disponibles para dejar espacio al SO
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        this.numThreads = Math.max(2, (int)(availableProcessors * 0.75));
        this.taskQueue = new TaskQueue();
        this.workers = new Thread[numThreads];
        
        // Crear y iniciar hilos trabajadores
        for (int i = 0; i < numThreads; i++) {
            workers[i] = new Thread(new Worker());
            workers[i].start();
        }
        
        System.out.println("Pool de hilos inicializado con " + numThreads + " hilos (de " + availableProcessors + " disponibles)");
    }
    
    public static ThreadPool getInstance() {
        if (instance == null) {
            instance = new ThreadPool();
        }
        return instance;
    }
    
    public int getNumThreads() {
        return numThreads;
    }
    
    // Enviar una tarea al pool
    public void submit(Runnable task) {
        if (!shutdown) {
            taskQueue.addTask(task);
        }
    }
    
    // Esperar a que terminen todas las tareas pendientes
    public void waitForCompletion() {
        while (!taskQueue.isEmpty() || taskQueue.hasActiveTasks()) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    public void shutdown() {
        shutdown = true;
        taskQueue.shutdown();
        
        // Esperar a que terminen todos los workers
        for (Thread worker : workers) {
            try {
                worker.join(5000); // Esperar máximo 5 segundos
            } catch (InterruptedException e) {
                worker.interrupt();
            }
        }
    }
    
    // Clase para manejar la cola de tareas
    private static class TaskQueue {
        private final List<Runnable> tasks = new ArrayList<>();
        private volatile boolean shutdown = false;
        private volatile int activeTasks = 0;
        
        public synchronized void addTask(Runnable task) {
            if (!shutdown) {
                tasks.add(task);
                notify(); // Despertar un hilo esperando
            }
        }
        
        public synchronized Runnable getTask() {
            while (tasks.isEmpty() && !shutdown) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
            
            if (!tasks.isEmpty()) {
                activeTasks++;
                return tasks.remove(0);
            }
            return null;
        }
        
        public synchronized void taskCompleted() {
            activeTasks--;
            if (activeTasks == 0 && tasks.isEmpty()) {
                notifyAll();
            }
        }
        
        public synchronized boolean isEmpty() {
            return tasks.isEmpty();
        }
        
        public synchronized boolean hasActiveTasks() {
            return activeTasks > 0;
        }
        
        public synchronized void shutdown() {
            shutdown = true;
            notifyAll();
        }
    }
    
    // Clase worker que ejecuta las tareas
    private class Worker implements Runnable {
        @Override
        public void run() {
            while (!shutdown) {
                Runnable task = taskQueue.getTask();
                if (task != null) {
                    try {
                        task.run();
                    } catch (Exception e) {
                        System.err.println("Error ejecutando tarea: " + e.getMessage());
                    } finally {
                        taskQueue.taskCompleted();
                    }
                }
            }
        }
    }
    
    // Método utilitario para dividir trabajo en chunks
    public static List<Integer> dividirEnChunks(int totalItems, int numChunks) {
        List<Integer> chunks = new ArrayList<>();
        int itemsPorChunk = Math.max(1, totalItems / numChunks);
        
        for (int i = 0; i < totalItems; i += itemsPorChunk) {
            chunks.add(Math.min(i + itemsPorChunk, totalItems));
        }
        
        return chunks;
    }
}
