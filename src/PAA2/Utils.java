package PAA2;

import java.io.*;

/**
 * Classe utilitaire fournissant des méthodes générales indépendantes
 * du cœur logique de l'application.
 *
 * Cette classe est dédiée à la persistance des données :
 * elle permet de sauvegarder l'état actuel d'un réseau électrique
 * dans un fichier texte afin de pouvoir le recharger ultérieurement.
 */
public class Utils {

    /**
     * Sauvegarde l'état complet d'un réseau dans un fichier texte.
     *
     * Le format généré est volontairement simple et structuré afin
     * d'être compatible avec le NetworkParser.
     *
     * Format des lignes produites :
     *  - generateur(nom, capacité).
     *  - maison(nom, type_consommation).
     *  - connexion(maison, generateur).
     *
     * Chaque instruction se termine par un point, ce qui permet
     * une relecture sans ambiguïté.
     *
     * @param net      le réseau à sauvegarder
     * @param filePath le chemin du fichier de sortie
     * @throws IOException si une erreur d'écriture survient
     */
    public static void saveNetworkToFile(Network net, String filePath) throws IOException {

        // Création d'un FileWriter pour écrire dans le fichier spécifié
        FileWriter fw = new FileWriter(filePath);

        /*
         * 1) Écriture des générateurs
         * Chaque générateur est transformé en une instruction textuelle
         * contenant son nom et sa capacité maximale.
         */
        for (Generator g : net.getGenerators().values()) {
            fw.write("generateur(" 
                     + g.getName() + ", " 
                     + g.getCapacity() + ").\n");
        }

        /*
         * 2) Écriture des maisons
         * Pour chaque maison, on sauvegarde son nom ainsi que son niveau
         * de consommation (enum ConsumptionLevel).
         */
        for (House h : net.getHouses().values()) {
            fw.write("maison(" 
                     + h.getName() + ", " 
                     + h.getConsumption().name() + ").\n");
        }

        /*
         * 3) Écriture des connexions
         * On parcourt la correspondance maison --> générateur afin de
         * reconstruire les liens du réseau.
         */
        for (House h : net.getHouseToGen().keySet()) {
            Generator g = net.getHouseToGen().get(h);
            fw.write("connexion(" 
                     + h.getName() + ", " 
                     + g.getName() + ").\n");
        }

        // Fermeture du flux pour garantir l'écriture complète du fichier
        fw.close();
    }
}
