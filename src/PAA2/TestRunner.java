package PAA2;

/**
 * Classe utilitaire chargée d'exécuter l'ensemble des tests unitaires
 * et d'intégration du projet.
 *
 * Cette classe joue le rôle de "suite de tests" globale.
 * Elle permet de vérifier que chaque module principal du projet
 * fonctionne correctement avant validation finale.
 *
 * Les tests lancés sont :
 *  - NetworkTest : vérifie la logique métier et les calculs de coût
 *  - SolverTest  : vérifie le bon fonctionnement des algorithmes d'optimisation
 *  - ParserTest  : vérifie la lecture correcte des fichiers de données
 *
 * Un résumé final est affiché afin de fournir un diagnostic global
 * de la stabilité du projet.
 */
public class TestRunner {

    /**
     * Point d'entrée du programme de tests.
     * Lance séquentiellement tous les modules de test
     * et affiche un bilan final.
     */
    public static void main(String[] args) {

        // Affichage d'un en-tête clair pour signaler le début des tests
        System.out.println("=========================================");
        System.out.println("      LANCEMENT DE LA SUITE DE TESTS     ");
        System.out.println("=========================================");

        int passed = 0; // Nombre de tests réussis
        int total = 3;  // Nombre total de modules de test exécutés

        /*
         * 1) Tests du module Network
         * Vérifie la création du réseau, la gestion des connexions
         * ainsi que les calculs de coût (dispersion et surcharge).
         */
        if (NetworkTest.run()) {
            System.out.println("NetworkTest: SUCCÈS");
            passed++;
        } else {
            System.out.println("NetworkTest: ÉCHEC");
        }

        /*
         * 2) Tests des algorithmes d'optimisation
         * Vérifie que les algorithmes (naïf, recuit simulé, etc.)
         * améliorent effectivement une solution initiale.
         */
        if (SolverTest.run()) {
            System.out.println("SolverTest: SUCCÈS");
            passed++;
        } else {
            System.out.println("SolverTest: ÉCHEC");
        }

        /*
         * 3) Tests du parser
         * Vérifie la lecture correcte des fichiers décrivant un réseau,
         * notamment le fichier 'data/network.txt'.
         */
        if (ParserTest.run()) {
            System.out.println("ParserTest: SUCCÈS");
            passed++;
        } else {
            System.out.println("ParserTest: ÉCHEC");
        }

        // Affichage du résumé final des tests
        System.out.println("=========================================");
        System.out.println("RÉSULTAT FINAL : " + passed + " / " + total);

        /*
         * Diagnostic global :
         * - Si tous les tests passent, le projet est considéré comme stable
         * - Sinon, certaines parties nécessitent une correction
         */
        if (passed == total) {
            System.out.println("SUCCÈS GLOBAL : Tous les modules fonctionnent correctement !");
        } else {
            System.out.println("ATTENTION : Certaines parties du code contiennent des erreurs.");
        }
    }
}
