package PAA2;

import java.util.*;

/**
 * Classe contenant les algorithmes d'optimisation du réseau électrique.
 *
 * Cette classe propose deux approches complémentaires :
 *  1) Une approche naïve basée sur des modifications aléatoires locales,
 *     qui n'accepte que les améliorations strictes.
 *  2) Une approche avancée basée sur le recuit simulé, capable d'accepter
 *     temporairement des dégradations afin d'échapper aux optimums locaux
 *     et de converger vers la solution globale optimale (coût minimal).
 */
public class Solver {

    /**
     * ALGORITHME 1 : Approche naïve (aléatoire).
     *
     * Le principe est le suivant :
     *  - Effectuer k tentatives de modifications aléatoires
     *  - Accepter une modification uniquement si elle améliore le coût total
     *
     * Cette méthode est simple mais peut rester bloquée
     * dans un optimum local.
     *
     * @param original le réseau initial
     * @param k        nombre d'itérations aléatoires
     * @return un réseau potentiellement amélioré
     */
    public Network solveRandom(Network original, int k) {

        // Travail sur une copie afin de ne jamais modifier la solution originale
        Network currentS = cloneNetwork(original);
        double currentCost = currentS.calculateCost().totalCost;

        // Conversion des collections en listes pour permettre un tirage aléatoire
        List<House> houses = new ArrayList<>(currentS.getHouses().values());
        List<Generator> generators = new ArrayList<>(currentS.getGenerators().values());
        Random rand = new Random();

        for (int i = 0; i < k; i++) {

            // Sécurité : arrêt si le réseau est vide
            if (houses.isEmpty() || generators.isEmpty()) break;

            // 1) Sélection aléatoire d'une maison et d'un générateur cible
            House m = houses.get(rand.nextInt(houses.size()));
            Generator newG = generators.get(rand.nextInt(generators.size()));
            Generator oldG = currentS.getHouseToGen().get(m);

            // Si aucun changement réel, on passe à l'itération suivante
            if (oldG == newG) continue;

            // 2) Application temporaire de la modification
            currentS.addConnection(m.getName(), newG.getName());
            double newCost = currentS.calculateCost().totalCost;

            /*
             * 3) Critère d'acceptation :
             *    - Si le coût s'améliore, on conserve la modification
             *    - Sinon, on annule et on revient à l'état précédent
             */
            if (newCost >= currentCost) {
                currentS.addConnection(m.getName(), oldG.getName());
            } else {
                currentCost = newCost;
            }
        }

        return currentS;
    }

    /**
     * ALGORITHME 2 : Recuit simulé.
     *
     * Algorithme d'optimisation inspiré de la thermodynamique.
     * Contrairement à l'approche naïve, il accepte parfois
     * des solutions moins bonnes afin de sortir des optimums locaux.
     *
     * @param original le réseau initial
     * @return la meilleure solution trouvée
     */
    public Network solveOptimized(Network original) {

        // Solution courante et meilleure solution trouvée
        Network currentS = cloneNetwork(original);
        Network bestS = cloneNetwork(currentS);

        double currentCost = currentS.calculateCost().totalCost;
        double bestCost = currentCost;

        // Paramètres du recuit simulé
        double temperature = 1000.0;   // Température initiale
        double coolingRate = 0.995;    // Facteur de refroidissement

        List<House> houses = new ArrayList<>(currentS.getHouses().values());
        List<Generator> generators = new ArrayList<>(currentS.getGenerators().values());
        Random rand = new Random();

        // Boucle principale : le système se refroidit progressivement
        while (temperature > 1) {

            // 1) Sélection aléatoire d'un mouvement
            House m = houses.get(rand.nextInt(houses.size()));
            Generator newG = generators.get(rand.nextInt(generators.size()));
            Generator oldG = currentS.getHouseToGen().get(m);

            if (oldG == newG) continue;

            // 2) Application temporaire du mouvement
            currentS.addConnection(m.getName(), newG.getName());
            double newCost = currentS.calculateCost().totalCost;

            /*
             * 3) Critère de Metropolis :
             *    - Amélioration : toujours acceptée
             *    - Dégradation : acceptée avec une certaine probabilité
             */
            if (acceptanceProbability(currentCost, newCost, temperature) > rand.nextDouble()) {

                currentCost = newCost;

                // Mise à jour de la meilleure solution globale
                if (currentCost < bestCost) {
                    bestS = cloneNetwork(currentS);
                    bestCost = currentCost;
                }
            } else {
                // Annulation du mouvement si refus
                currentS.addConnection(m.getName(), oldG.getName());
            }

            // 4) Refroidissement progressif
            temperature *= coolingRate;
        }

        // Retour de la meilleure solution rencontrée
        return bestS;
    }

    /**
     * Calcule la probabilité d'acceptation d'une solution,
     * selon la loi de Boltzmann.
     *
     * @param currentCost coût actuel
     * @param newCost     coût après modification
     * @param temperature température du système
     * @return une probabilité comprise entre 0 et 1
     */
    private double acceptanceProbability(double currentCost, double newCost, double temperature) {

        // Toute amélioration est acceptée automatiquement
        if (newCost < currentCost) {
            return 1.0;
        }

        // Sinon, la probabilité dépend de l'écart de coût et de la température
        return Math.exp((currentCost - newCost) / temperature);
    }

    /**
     * Crée une copie profonde du réseau.
     *
     * Cette étape est essentielle car les objets Java sont manipulés
     * par référence. Sans clonage, toute modification impacterait
     * directement la solution originale.
     *
     * @param net le réseau à cloner
     * @return une copie indépendante du réseau
     */
    private Network cloneNetwork(Network net) {

        Network c = new Network();
        c.setLambda(net.getLambda());

        // Copie des générateurs
        for (Generator g : net.getGenerators().values())
            c.addGenerator(g.getName(), g.getCapacity());

        // Copie des maisons
        for (House h : net.getHouses().values())
            c.addHouse(h.getName(), h.getConsumption());

        // Copie des connexions
        for (House h : net.getHouseToGen().keySet()) {
            Generator g = net.getHouseToGen().get(h);
            c.addConnection(h.getName(), g.getName());
        }

        return c;
    }
}