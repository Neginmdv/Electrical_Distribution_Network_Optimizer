package PAA2;

import java.util.*;

/**
 * Classe principale représentant un réseau électrique.
 *
 * Cette classe modélise :
 *  - les générateurs (sources d'énergie)
 *  - les maisons (consommateurs)
 *  - les connexions entre eux
 *
 * Elle fournit également les méthodes permettant :
 *  - de modifier dynamiquement le réseau
 *  - de vérifier sa cohérence
 *  - de calculer le coût global selon les formules du sujet
 */
public class Network {

    // STRUCTURES DE DONNÉES
    // Générateurs indexés par leur nom
    private HashMap<String, Generator> generators;

    // Maisons indexées par leur nom
    private HashMap<String, House> houses;

    // Pour chaque générateur : liste des maisons connectées
    private HashMap<Generator, List<House>> genToHouses;

    // Pour chaque maison : générateur auquel elle est connectée
    private HashMap<House, Generator> houseToGen;

    // Paramètre de pénalité de surcharge (λ du sujet)
    private double lambda = 10.0;

    // Constructeur
    public Network() {
        generators = new HashMap<>();
        houses = new HashMap<>();
        genToHouses = new HashMap<>();
        houseToGen = new HashMap<>();
    }

    // GETTERS / SETTERS
    public double getLambda() { return lambda; }
    public void setLambda(double lambda) { this.lambda = lambda; }

    public HashMap<String, Generator> getGenerators() { return generators; }
    public HashMap<String, House> getHouses() { return houses; }
    public HashMap<Generator, List<House>> getGenToHouses() { return genToHouses; }
    public HashMap<House, Generator> getHouseToGen() { return houseToGen; }

    // GESTION DES ENTITÉS
    /**
     * Ajoute un générateur ou met à jour sa capacité s'il existe déjà.
     */
    public String addGenerator(String name, int capacity) {

        if (capacity <= 0) {
            throw new IllegalArgumentException(
                "La capacité du générateur doit être strictement positive."
            );
        }

        if (generators.containsKey(name)) {
            generators.get(name).setCapacity(capacity);
            return "Mise à jour Générateur : " + name;
        }

        Generator g = new Generator(name, capacity);
        generators.put(name, g);
        genToHouses.put(g, new ArrayList<>());

        return "Création Générateur : " + name;
    }

    /**
     * Ajoute une maison ou met à jour son niveau de consommation.
     */
    public String addHouse(String name, ConsumptionLevel level) {

        if (houses.containsKey(name)) {
            houses.get(name).setConsumption(level);
            return "Mise à jour Maison : " + name;
        }

        House h = new House(name, level);
        houses.put(name, h);

        return "Création Maison : " + name;
    }

    // GESTION DES CONNEXIONS
    /**
     * Connecte une maison à un générateur.
     * Une maison ne peut être connectée qu'à un seul générateur.
     */
    public boolean addConnection(String houseName, String genName) {

        House h = houses.get(houseName);
        Generator g = generators.get(genName);
        if (h == null || g == null) return false;

        // Déconnexion automatique de l'ancien générateur
        if (houseToGen.containsKey(h)) {
            Generator old = houseToGen.get(h);
            genToHouses.get(old).remove(h);
        }

        houseToGen.put(h, g);
        genToHouses.get(g).add(h);
        return true;
    }

    /**
     * Supprime une connexion existante.
     */
    public boolean removeConnection(String houseName, String genName) {

        House h = houses.get(houseName);
        Generator g = generators.get(genName);
        if (h == null || g == null) return false;

        if (houseToGen.get(h) == g) {
            houseToGen.remove(h);
            genToHouses.get(g).remove(h);
            return true;
        }

        return false;
    }

    /**
     * Vérifie si une maison est connectée à un générateur donné.
     */
    public boolean isConnected(String houseName, String genName) {

        House h = houses.get(houseName);
        Generator g = generators.get(genName);
        if (h == null || g == null) return false;

        return houseToGen.get(h) == g;
    }

    /**
     * Vérifie que toutes les maisons sont connectées.
     *
     * @return liste des maisons orphelines
     */
    public List<String> validateNetwork() {

        List<String> missing = new ArrayList<>();
        for (House h : houses.values()) {
            if (!houseToGen.containsKey(h)) {
                missing.add(h.getName());
            }
        }
        return missing;
    }

    // CALCUL DU COÛ
    /**
     * Calcule le coût global du réseau selon la fonction objectif.
     */
    public CostResult calculateCost() {

        if (generators.isEmpty()) {
            throw new IllegalStateException(
                "Impossible de calculer le coût : aucun générateur."
            );
        }

        // Calcul des charges Lg pour chaque générateur
        HashMap<Generator, Integer> loads = new HashMap<>();
        HashMap<Generator, Double> rates = new HashMap<>();

        for (Generator g : generators.values()) {
            int load = 0;
            for (House h : genToHouses.get(g)) {
                load += h.getConsumption().getValue();
            }
            loads.put(g, load);
        }

        double totalSurcharge = 0;
        double sumRates = 0;

        // Calcul des taux ug et de la surcharge
        for (Generator g : generators.values()) {
            int Lg = loads.get(g);
            int Cg = g.getCapacity();
            double ug = (double) Lg / Cg;

            rates.put(g, ug);
            sumRates += ug;
            totalSurcharge += Math.max(0, (Lg - Cg) / (double) Cg);
        }

        // Calcul de la dispersion
        double avg = sumRates / generators.size();
        double totalDisp = 0;
        for (double ug : rates.values()) {
            totalDisp += Math.abs(ug - avg);
        }

        // Coût total = dispersion + λ × surcharge
        return new CostResult(
            totalDisp,
            totalSurcharge,
            totalDisp + this.lambda * totalSurcharge
        );
    }

    /**
     * Affiche le réseau sous forme textuelle.
     */
    public void printNetwork() {
        for (Generator g : generators.values()) {
            System.out.print(g + " -> ");
            List<House> list = genToHouses.get(g);
            if (list != null && !list.isEmpty()) {
                for (House h : list) System.out.print(h + " ");
            }
            System.out.println();
        }
    }
}
