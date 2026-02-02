package PAA2;

/**
 * Classe de test dédiée à la vérification des algorithmes d'optimisation
 * implémentés dans la classe Solver.
 *
 * L'objectif principal est de s'assurer que :
 *  - L'algorithme naïf (aléatoire) ne dégrade jamais une solution
 *  - L'algorithme optimisé (recuit simulé) améliore effectivement
 *    une configuration initialement mauvaise
 */
public class SolverTest {

    /**
     * Lance l'ensemble des tests liés aux algorithmes de résolution.
     *
     * @return true si tous les tests réussissent, false sinon
     */
    public static boolean run() {
        System.out.println("\n[SolverTest] Démarrage des tests d'optimisation...");
        boolean ok = true;

        // Test de l'algorithme aléatoire
        if (!testRandomSolver()) ok = false;

        // Test de l'algorithme optimisé (recuit simulé)
        if (!testOptimizedSolver()) ok = false;

        return ok;
    }

    /**
     * Teste l'algorithme naïf (basé sur des modifications aléatoires).
     *
     * Le principe vérifié est le suivant :
     * après un certain nombre d'itérations, la solution obtenue
     * ne doit pas être pire que la solution initiale.
     *
     * @return true si le test est validé, false sinon
     */
    private static boolean testRandomSolver() {

        // Création d'un réseau volontairement mal configuré
        Network net = createBadNetwork();

        // Calcul du coût initial
        double startCost = net.calculateCost().totalCost;

        // Exécution de l'algorithme aléatoire sur 100 itérations
        Solver solver = new Solver();
        Network res = solver.solveRandom(net, 100);

        // Calcul du coût final
        double endCost = res.calculateCost().totalCost;

        /*
         * L'algorithme aléatoire ne doit jamais augmenter le coût.
         * Il peut rester identique ou s'améliorer.
         */
        if (endCost > startCost) {
            System.out.println(
                "ÉCHEC : L'algorithme aléatoire a augmenté le coût (ceci ne devrait pas arriver)."
            );
            return false;
        }

        return true;
    }

    /**
     * Teste l'algorithme optimisé basé sur le recuit simulé.
     *
     * Pour un réseau simple et déséquilibré, l'algorithme doit
     * impérativement trouver une meilleure solution que l'initiale.
     *
     * @return true si le coût est amélioré, false sinon
     */
    private static boolean testOptimizedSolver() {

        // Création d'un réseau volontairement mal équilibré
        Network net = createBadNetwork();

        // Coût initial
        double startCost = net.calculateCost().totalCost;

        // Exécution de l'algorithme optimisé
        Solver solver = new Solver();
        Network res = solver.solveOptimized(net);

        // Coût final
        double endCost = res.calculateCost().totalCost;

        /*
         * Le recuit simulé doit absolument améliorer ce réseau simple.
         * Une absence d'amélioration indique une erreur dans l'algorithme.
         */
        if (endCost >= startCost) {
            System.out.println(
                "ÉCHEC : L'algorithme optimisé n'a pas réussi à améliorer le réseau."
            );
            return false;
        }

        return true;
    }

    /**
     * Crée un réseau volontairement mal configuré afin de servir
     * de cas de test pour les algorithmes d'optimisation.
     *
     * Configuration :
     *  - Deux générateurs de 100 kW
     *  - Deux maisons à forte consommation (40 kW chacune)
     *  - Les deux maisons sont connectées au même générateur
     *
     * Cette configuration provoque une forte dispersion de charge,
     * ce qui constitue un cas idéal pour tester les algorithmes.
     *
     * @return un réseau mal équilibré
     */
    private static Network createBadNetwork() {

        Network net = new Network();

        // Ajout des générateurs
        net.addGenerator("G1", 100);
        net.addGenerator("G2", 100);

        // Ajout des maisons à forte consommation
        net.addHouse("H1", ConsumptionLevel.FORTE);
        net.addHouse("H2", ConsumptionLevel.FORTE);

        // Mauvaise configuration volontaire :
        // toutes les maisons sont connectées au même générateur
        net.addConnection("H1", "G1");
        net.addConnection("H2", "G1");

        return net;
    }
}