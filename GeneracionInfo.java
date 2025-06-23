public class GeneracionInfo {
    public int generacion;
    public int fitness;

    public GeneracionInfo(int generacion, int fitness) {
        this.generacion = generacion;
        this.fitness = fitness;
    }

    @Override
    public String toString() {
        return "Gen " + generacion + " → Fitness: " + fitness;
    }
}
