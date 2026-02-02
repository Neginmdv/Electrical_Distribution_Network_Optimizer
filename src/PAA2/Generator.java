package PAA2;

/**
 * Classe représentant un générateur électrique.
 *
 * Un générateur est une source de production d'énergie du réseau.
 * Il est caractérisé par :
 *  - un nom unique
 *  - une capacité maximale de production (en kW)
 *
 * Cette classe est volontairement simple et ne contient aucune
 * logique métier complexe. Tous les calculs sont délégués
 * à la classe Network.
 */
public class Generator {

    // Nom unique du générateur
    private String name;

    // Capacité maximale de production en kW
    private int capacity;

    /**
     * Constructeur d'un générateur.
     *
     * @param name nom du générateur
     * @param capacity capacité maximale en kW
     */
    public Generator(String name, int capacity) {
        this.name = name;
        this.capacity = capacity;
    }

    /**
     * @return le nom du générateur
     */
    public String getName() {
        return name;
    }

    /**
     * @return la capacité maximale du générateur
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Modifie la capacité du générateur.
     *
     * @param capacity nouvelle capacité maximale
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    /**
     * Représentation textuelle du générateur.
     * Utile pour l'affichage et le débogage.
     */
    @Override
    public String toString() {
        return name + " (" + capacity + "kW)";
    }
}
