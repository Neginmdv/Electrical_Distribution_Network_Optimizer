package PAA2;

/**
 * Classe de test pour le module de lecture de fichiers (Parsing).
 *
 * Son rôle est de valider que la classe NetworkParser fonctionne correctement
 * avec un fichier réel. Elle vérifie deux choses :
 *
 * - Le fichier est bien trouvé et lu (pas de retour null).
 * - Le contenu est correctement extrait (le réseau n'est pas vide après lecture).
 *
 */
public class ParserTest {

    /*
     * Exécute le test de lecture.
     * Suppose qu'un fichier "data/network.txt" existe (le fichier d'exemple du projet).
     */
    
    public static boolean run() {
        System.out.println("\n[ParserTest] Démarrage du test de lecture...");
        
        NetworkParser parser = new NetworkParser();
        
        // Tentative de chargement du fichier standard
        // Assurez-vous que le dossier 'data' existe à la racine du projet
        Network net = parser.parseFile("data/network.txt");

        // Cas 1 : Le parser renvoie null (Fichier introuvable ou erreur de syntaxe grave)
        if (net == null) {
            System.out.println("ÉCHEC : Impossible de lire le fichier (vérifiez le chemin 'data/network.txt').");
            return false;
        }

        // Cas 2 : Le fichier a été lu, mais aucun élément n'a été créé
        // Cela peut arriver si le fichier est vide ou si le format est incorrect sans provoquer d'erreur fatale.
        if (net.getGenerators().isEmpty() || net.getHouses().isEmpty()) {
            System.out.println("ÉCHEC : Fichier lu mais le réseau résultant est vide (pas de générateurs ou pas de maisons).");
            return false;
        }

        return true;
    }
}
