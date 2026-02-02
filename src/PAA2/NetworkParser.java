package PAA2;

import java.io.*;
import java.util.*;

/**
 * Classe responsable de la lecture et de l'analyse (parsing)
 * du fichier texte décrivant un réseau électrique.
 *
 * Le parser assure plusieurs rôles essentiels :
 *  - Lecture du fichier ligne par ligne
 *  - Vérification de la syntaxe (point final, format des prédicats)
 *  - Vérification de l'ordre logique des sections
 *    (générateurs --> maisons --> connexions)
 *  - Vérification de la cohérence sémantique
 *    (références à des entités existantes)
 *
 * En cas d'erreurs, celles-ci sont collectées, affichées,
 * et la création du réseau est annulée.
 */
public class NetworkParser {

    // Liste des erreurs détectées pendant le parsing
    private List<String> errors = new ArrayList<>();

    /**
     * Sections possibles du fichier.
     * L'ordre est strictement imposé afin d'éviter les incohérences.
     */
    private enum Section {
        GENERATORS,
        HOUSES,
        CONNECTIONS
    }

    /**
     * Analyse un fichier texte et construit le réseau correspondant.
     *
     * @param filePath chemin vers le fichier réseau
     * @return un Network valide ou null si des erreurs sont détectées
     */
    public Network parseFile(String filePath) {

        Network network = new Network();
        errors.clear();

        Section currentSection = Section.GENERATORS;

        // Mémorisation des entités déclarées pour validation ultérieure
        Set<String> declaredGens = new HashSet<>();
        Set<String> declaredHouses = new HashSet<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line;
            int lineNum = 0;

            while ((line = br.readLine()) != null) {
                lineNum++;
                String trimmed = line.trim();

                // Ignorer lignes vides et commentaires
                if (trimmed.isEmpty() || trimmed.startsWith("%")) continue;

                // Chaque instruction doit se terminer par un point
                if (!trimmed.endsWith(".")) {
                    errors.add("Ligne " + lineNum + " : Point final manquant.");
                    continue;
                }

                // Suppression du point final
                String content = trimmed.substring(0, trimmed.length() - 1).trim();

                // Analyse du type d'instruction
                if (content.startsWith("generateur(")) {

                    // Les générateurs doivent apparaître en premier
                    if (currentSection != Section.GENERATORS)
                        errors.add("Ligne " + lineNum + " : Ordre incorrect (générateur).");

                    parseGenerator(content, lineNum, network, declaredGens);

                } else if (content.startsWith("maison(")) {

                    // Passage à la section maisons
                    if (currentSection == Section.GENERATORS)
                        currentSection = Section.HOUSES;

                    parseHouse(content, lineNum, network, declaredHouses);

                } else if (content.startsWith("connexion(")) {

                    // Passage définitif à la section connexions
                    currentSection = Section.CONNECTIONS;
                    parseConnection(content, lineNum, network, declaredHouses, declaredGens);

                } else {
                    errors.add("Ligne " + lineNum + " : Instruction inconnue ou mal formée.");
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("Erreur : fichier introuvable -> " + filePath);
            return null;
        } catch (IOException e) {
            System.out.println("Erreur de lecture : " + e.getMessage());
            return null;
        }

        // Si des erreurs ont été détectées, on annule la création
        if (!errors.isEmpty()) {
            for (String err : errors)
                System.out.println(err);
            return null;
        }

        return network;
    }

    /**
     * Analyse une déclaration de générateur.
     * Format attendu : generateur(nom, capacité)
     */
    private void parseGenerator(String content, int line, Network net, Set<String> gens) {

        String args = extractArgs(content, "generateur");
        if (args == null) {
            errors.add("Erreur ligne " + line + " : Syntaxe invalide.");
            return;
        }

        String[] parts = args.split(",");
        if (parts.length != 2) {
            errors.add("Erreur ligne " + line + " : generateur attend 2 arguments.");
            return;
        }

        try {
            String name = parts[0].trim();
            int capacity = Integer.parseInt(parts[1].trim());

            net.addGenerator(name, capacity);
            gens.add(name);

        } catch (NumberFormatException e) {
            errors.add("Erreur ligne " + line + " : Capacité invalide.");
        }
    }

    /**
     * Analyse une déclaration de maison.
     * Format attendu : maison(nom, type)
     */
    private void parseHouse(String content, int line, Network net, Set<String> houses) {

        String args = extractArgs(content, "maison");
        if (args == null) return;

        String[] parts = args.split(",");
        if (parts.length != 2) return;

        try {
            String name = parts[0].trim();
            String typeStr = parts[1].trim();
            ConsumptionLevel level;

            // Autorise les valeurs numériques (10, 20, 40)
            if (typeStr.matches("\\d+")) {
                int val = Integer.parseInt(typeStr);
                if (val == 10) level = ConsumptionLevel.BASSE;
                else if (val == 20) level = ConsumptionLevel.NORMAL;
                else if (val == 40) level = ConsumptionLevel.FORTE;
                else throw new IllegalArgumentException("Valeur de consommation invalide : " + val);
            } else {
                level = ConsumptionLevel.fromString(typeStr);
            }

            net.addHouse(name, level);
            houses.add(name);

        } catch (Exception e) {
            errors.add("Erreur ligne " + line + " : " + e.getMessage());
        }
    }

    /**
     * Analyse une déclaration de connexion.
     * Format attendu : connexion(maison, générateur)
     * ou connexion(générateur, maison)
     */
    private void parseConnection(String content, int line, Network net,
                                 Set<String> houses, Set<String> gens) {

        String args = extractArgs(content, "connexion");
        if (args == null) return;

        String[] parts = args.split(",");
        if (parts.length != 2) {
            errors.add("Erreur ligne " + line + " : connexion attend 2 arguments.");
            return;
        }

        String a = parts[0].trim();
        String b = parts[1].trim();

        // Détection automatique du sens de la connexion
        if (houses.contains(a) && gens.contains(b)) {
            net.addConnection(a, b);
        } else if (gens.contains(a) && houses.contains(b)) {
            net.addConnection(b, a);
        } else {
            errors.add("Erreur ligne " + line +
                    " : Connexion impossible (" + a + ", " + b + ").");
        }
    }

    /**
     * Extrait les arguments situés entre parenthèses.
     *
     * Exemple :
     *  - "generateur(G1, 50)" --> "G1, 50"
     */
    private String extractArgs(String content, String keyword) {

        if (!content.startsWith(keyword + "(") || !content.endsWith(")"))
            return null;

        return content.substring(keyword.length() + 1, content.length() - 1);
    }
}
