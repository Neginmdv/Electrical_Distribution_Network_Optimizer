package PAA2;

/**
 * Classe de tests unitaires manuels pour la classe Network.
 *
 * Cette classe permet de vérifier :
 *  - la création et la mise à jour des générateurs et des maisons
 *  - la gestion des connexions entre maisons et générateurs
 *  - la validité des formules mathématiques (dispersion, surcharge, coût total)
 *
 * Les tests mathématiques sont directement basés sur les exemples
 * chiffrés fournis dans l'énoncé du projet.
 */
public class NetworkTest {

    /**
     * Lance l'ensemble des tests du réseau.
     *
     * @return true si tous les tests réussissent, false sinon
     */
    public static boolean run() {

        System.out.println("\n[NetworkTest] Démarrage des tests...");
        boolean ok = true;

        // Tests fonctionnels de base
        if (!testAddAndUpdate()) ok = false;
        if (!testConnections()) ok = false;

        // Tests mathématiques basés sur le sujet
        if (!testMathInstance1()) ok = false; // Figure 2 (Instance 1)
        if (!testMathInstance2()) ok = false; // Figure 3 (Instance 2)

        return ok;
    }

    /**
     * Teste la création et la mise à jour des générateurs et des maisons.
     */
    private static boolean testAddAndUpdate() {

        Network net = new Network();

        // Création d'un générateur
        net.addGenerator("G1", 60);

        // Mise à jour du générateur existant
        net.addGenerator("G1", 100);
        if (net.getGenerators().get("G1").getCapacity() != 100) {
            System.out.println("ÉCHEC : La mise à jour du générateur a échoué.");
            return false;
        }

        // Création d'une maison
        net.addHouse("H1", ConsumptionLevel.FORTE);
        if (net.getHouses().get("H1").getConsumption() != ConsumptionLevel.FORTE) {
            System.out.println("ÉCHEC : La création de la maison a échoué.");
            return false;
        }

        return true;
    }

    /**
     * Teste l'ajout et la suppression des connexions.
     */
    private static boolean testConnections() {

        Network net = new Network();
        net.addGenerator("G1", 100);
        net.addHouse("H1", ConsumptionLevel.NORMAL);

        // Ajout de la connexion
        net.addConnection("H1", "G1");
        if (!net.isConnected("H1", "G1")) {
            System.out.println("ÉCHEC : La connexion a échoué.");
            return false;
        }

        // Suppression de la connexion
        net.removeConnection("H1", "G1");
        if (net.isConnected("H1", "G1")) {
            System.out.println("ÉCHEC : La suppression de la connexion a échoué.");
            return false;
        }

        return true;
    }

    /**
     * Vérifie le calcul du coût pour l'Instance 1 du sujet (Figure 2).
     *
     * Cas équilibré :
     *  - aucune surcharge
     *  - dispersion faible
     */
    private static boolean testMathInstance1() {

        Network net = new Network();
        net.setLambda(10.0);

        // Générateurs
        net.addGenerator("G1", 60);
        net.addGenerator("G2", 60);

        // Maisons
        net.addHouse("M1", ConsumptionLevel.FORTE);   // 40
        net.addHouse("M2", ConsumptionLevel.FORTE);   // 40
        net.addHouse("M3", ConsumptionLevel.NORMAL);  // 20
        net.addHouse("M4", ConsumptionLevel.BASSE);   // 10

        // Connexions (configuration équilibrée)
        net.addConnection("M1", "G1");
        net.addConnection("M3", "G1");
        net.addConnection("M2", "G2");
        net.addConnection("M4", "G2");

        CostResult res = net.calculateCost();

        // Vérification avec tolérance numérique
        if (Math.abs(res.disp - 0.1666) > 0.01 || res.surcharge != 0) {
            System.out.println("ÉCHEC : Calcul incorrect pour l'Instance 1.");
            return false;
        }

        return true;
    }

    /**
     * Vérifie le calcul du coût pour l'Instance 2 du sujet (Figure 3).
     *
     * Cas avec surcharge :
     *  - G2 dépasse sa capacité maximale
     */
    private static boolean testMathInstance2() {

        Network net = new Network();
        net.setLambda(10.0);

        net.addGenerator("G1", 60);
        net.addGenerator("G2", 60);

        net.addHouse("M1", ConsumptionLevel.FORTE);
        net.addHouse("M2", ConsumptionLevel.FORTE);
        net.addHouse("M3", ConsumptionLevel.NORMAL);
        net.addHouse("M4", ConsumptionLevel.BASSE);

        // Configuration surchargée
        net.addConnection("M3", "G1");
        net.addConnection("M1", "G2");
        net.addConnection("M2", "G2");
        net.addConnection("M4", "G2");

        CostResult res = net.calculateCost();

        // surcharge = (90 - 60) / 60 = 0.5
        if (Math.abs(res.surcharge - 0.5) > 0.001) {
            System.out.println("ÉCHEC : Calcul de la surcharge incorrect.");
            return false;
        }

        return true;
    }
}