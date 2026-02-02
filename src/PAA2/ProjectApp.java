package PAA2;

import java.util.List;
import java.util.Scanner;

/**
 * Classe principale de l'application (Point d'entrée).
 *
 * Cette classe gère l'interaction avec l'utilisateur via la console.
 * Elle orchestre les deux modes de fonctionnement demandés dans le sujet :
 * 1. Mode Manuel (Partie 1) : L'utilisateur construit le réseau pas à pas.
 * 2. Mode Fichier (Partie 2) : Le réseau est chargé depuis un fichier et optimisé automatiquement.
 */
public class ProjectApp {

    // Scanner unique pour gérer les entrées utilisateur dans toute l'application
    private static Scanner sc = new Scanner(System.in);
    
    // Réseau actuellement chargé en mémoire (manipulé par les deux modes)
    private static Network loadedNetwork = null;

    /**
     * Point d'entrée du programme.
     * Analyse les arguments de la ligne de commande pour décider du mode de lancement.
     * * @param args Arguments passés au programme.
     * - Aucun argument : Lancement du Mode Manuel.
     * - 2 arguments (cheminfichier, lambda) : Lancement du Mode Fichier.
     */
    public static void main(String[] args) {
        if (args.length == 2) {
            // --- MODE AUTOMATIQUE (PARTIE 2) ---
            String fileName = args[0];
            double lambda = 10.0; // Valeur par défaut
            
            try {
                // Tentative de conversion du paramètre lambda
                lambda = Double.parseDouble(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("Erreur: lambda doit être un nombre.");
                return; // Arrêt immédiat si lambda est invalide
            }
            
            // Lancement du flux de la partie 2
            runPart2(fileName, lambda);
            
        } else {
            // --- MODE MANUEL (PARTIE 1) ---
            // Si le nombre d'arguments est différent de 2, on lance le mode manuel par défaut
            runPart1();
        }
    }

    /**
     * Exécute la logique de la Partie 2 (Mode Fichier).
     * * Étapes :
     * 1. Charge et analyse le fichier réseau.
     * 2. Valide la structure du réseau importé.
     * 3. Propose un menu pour optimiser le réseau via des algorithmes ou sauvegarder.
     * * @param fileName Chemin du fichier à importer.
     * @param lambda Paramètre de pénalité pour le calcul du coût.
     */
    private static void runPart2(String fileName, double lambda) {
        System.out.println("=== PARTIE 2 : MODE FICHIER ===");
        
        // Étape 1 : Lecture et Parsing du fichier
        NetworkParser parser = new NetworkParser();
        loadedNetwork = parser.parseFile(fileName);

        // Si le parsing a échoué (fichier introuvable ou erreur de syntaxe), on arrête.        
        if (loadedNetwork == null) return;
        
        // Étape 2 : Validation du réseau (vérification des maisons orphelines)
        List<String> missing = loadedNetwork.validateNetwork();
        if (!missing.isEmpty()) {
            System.out.println("Erreur : Maisons non connectées: " + missing);
            return;
        }

        // Configuration du paramètre de pénalité
        loadedNetwork.setLambda(lambda);

        // Étape 3 : Boucle principale du menu automatique
        boolean running = true;
        while (running) {
            System.out.println("\n--- MENU AUTOMATIQUE ---");
            System.out.println("1. Résolution automatique");
            System.out.println("2. Sauvegarder la solution");
            System.out.println("3. Fin");
            System.out.print("Choix: ");
            
            int choice = readInt();
            if (choice < 1 || choice > 3) {
                System.out.println("Choix invalide. Veuillez réessayer.");
                continue;
            }

            switch (choice) {
                case 1: // RÉSOLUTION AUTOMATIQUE
                    System.out.println("\n--- CHOIX ALGORITHME ---");
                    System.out.println("1. Naïf (Aléatoire) [Basique]");
                    System.out.println("2. Recuit Simulé (Optimisé) [Bonus]");
                    System.out.print("Choix : ");
                    
                    int algoChoice = readInt();
                    if (algoChoice < 1 || algoChoice > 2) {
                        System.out.println("Choix invalide. Veuillez réessayer.");
                        continue;
                    }

                    Solver solver = new Solver();
                    Network optimized = null;
                    
                    // Mesure du temps d'exécution
                    long startTime = System.currentTimeMillis();

                    if (algoChoice == 1) {
                        System.out.println("Exécution de l'algorithme Naïf...");
                        // 10 000 itérations pour essayer de trouver une bonne solution au hasard
                        optimized = solver.solveRandom(loadedNetwork, 10000);
                    } else if (algoChoice == 2) {
                        System.out.println("Exécution de l'algorithme Optimisé...");
                        // Algorithme plus intelligent (Recuit Simulé)
                        optimized = solver.solveOptimized(loadedNetwork);
                    } else {
                        System.out.println("Choix invalide.");
                        continue;
                    }
                    
                    long duration = System.currentTimeMillis() - startTime;
                    CostResult res = optimized.calculateCost();
                    
                    // Affichage des résultats de l'optimisation
                    System.out.println("--- RÉSULTATS ---");
                    System.out.println("Temps d'exécution : " + duration + " ms");
                    System.out.println("Nouveau coût total : " + res.totalCost);
                    System.out.println("(Dispersion : " + res.disp + ", Surcharge: " + res.surcharge + ")");
                    
                    // Mise à jour du réseau actuel avec la meilleure solution trouvée
                    loadedNetwork = optimized;
                    break;
                    
                case 2: // SAUVEGARDE
                    handleSave();
                    break;
                    
                case 3: // QUITTER
                    running = false;
                    break;
                    
                default: System.out.println("Option invalide");
            }
        }
    }

    /**
     * Exécute la logique de la Partie 1 (Mode Manuel).
     * Permet à l'utilisateur de construire le réseau pas à pas via un menu interactif.
     * Une fois le réseau construit, permet de le gérer (calcul de coût, modifications).
     */
    private static void runPart1() {
        System.out.println("=== PARTIE 1 : MODE MANUEL ===");
        loadedNetwork = new Network(); // Initialisation d'un réseau vide
        
        // --- PHASE 1 : CONSTRUCTION DU RÉSEAU ---
        while (true) {
            System.out.println("\n--- MENU PRINCIPAL ---");
            System.out.println("1. Ajouter un générateur");
            System.out.println("2. Ajouter une maison");
            System.out.println("3. Ajouter une connexion");
            System.out.println("4. Supprimer une connexion");
            System.out.println("5. Fin (Passer à la gestion)");
            System.out.print("Choix : ");
            
            int choice = readInt();
            if (choice < 1 || choice > 5) {
                System.out.println("Choix invalide. Veuillez réessayer.");
                continue;
            }

            if (choice == 1) { // AJOUT GÉNÉRATEUR
                System.out.print("Nom et Capacité (ex: G1 60) : ");
                String[] parts = sc.nextLine().split(" ");
                if (parts.length != 2) {
                    System.out.println("Entrée invalide ! Format attendu : Nom Capacité (ex: G1 60)");
                } else {
                    try {
                        String msg = loadedNetwork.addGenerator(parts[0], Integer.parseInt(parts[1]));
                        System.out.println(msg);
                    } catch (NumberFormatException e) {
                        System.out.println("Erreur format : Capacité doit être un entier positif.");
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    }
                }
            } else if (choice == 2) { // AJOUT MAISON
                System.out.print("Nom et Conso (ex: M1 NORMAL) : ");
                String[] parts = sc.nextLine().split(" ");
                if (parts.length != 2) {
                    System.out.println("Entrée invalide ! Format attendu : Nom Conso (ex: M1 NORMAL)");
                } else {
                    try {
                        ConsumptionLevel level = ConsumptionLevel.fromString(parts[1]);
                        String msg = loadedNetwork.addHouse(parts[0], level);
                        System.out.println(msg);
                    } catch (Exception e) {
                        System.out.println("Type invalide (BASSE, NORMAL, FORTE).");
                    }
                }
            } else if (choice == 3) { // AJOUT CONNEXION
                System.out.print("Maison Générateur (ex: M1 G1) : ");
                String[] p = sc.nextLine().split(" ");
                if (p.length != 2) {
                    System.out.println("Entrée invalide ! Format attendu : Maison Générateur (ex: M1 G1)");
                } else {
                    // Tente de connecter dans un sens, si échoue, tente l'autre (pour flexibilité utilisateur)
                    boolean ok = loadedNetwork.addConnection(p[0], p[1]);
                    if(!ok) ok = loadedNetwork.addConnection(p[1], p[0]);
                    System.out.println(ok ? "Connecté avec succès." : "Erreur : Élément introuvable.");
                }
            } else if (choice == 4) { // SUPPRESSION CONNEXION
                System.out.print("Maison Générateur (ex: M1 G1) : ");
                String[] p = sc.nextLine().split(" ");
                if (p.length != 2) {
                    System.out.println("Entrée invalide ! Format attendu : Maison Générateur (ex: M1 G1)");
                } else {
                    boolean ok = loadedNetwork.removeConnection(p[0], p[1]);
                    if(!ok) ok = loadedNetwork.removeConnection(p[1], p[0]);
                    System.out.println(ok ? "Lien supprimé." : "Erreur : Lien inexistant.");
                }
            } else if (choice == 5) { // FIN CONSTRUCTION
                // Vérifications de cohérence avant de quitter la construction
                if (loadedNetwork.getGenerators().isEmpty()) {
                    System.out.println("Impossible de terminer : aucun générateur n'a été ajouté.");
                    continue;
                }

                List<String> missing = loadedNetwork.validateNetwork();
                if (!missing.isEmpty()) {
                    System.out.println("Impossible de terminer : Les maisons suivantes ne sont pas connectées : " + missing);
                    continue;
                }

                break; // Sortie de la boucle de construction
            }
        }

        // --- PHASE 2 : GESTION DU RÉSEAU ---
        while(true) {
            System.out.println("\n--- GESTION ---");
            System.out.println("1. Calculer le coût");
            System.out.println("2. Modifier une connexion");
            System.out.println("3. Afficher le réseau");
            System.out.println("4. Fin");
            System.out.print("Choix : ");
            
            int c = readInt();
            if (c < 1 || c > 4) {
                System.out.println("Choix invalide. Veuillez réessayer.");
                continue;
            }

            if (c == 1) { // CALCUL DU COÛT
                try {
                    CostResult res = loadedNetwork.calculateCost();
                    System.out.println("Dispersion : " + res.disp);
                    System.out.println("Surcharge : " + res.surcharge);
                    System.out.println("Coût Total : " + res.totalCost);
                } catch (IllegalStateException e) {
                    System.out.println(e.getMessage());
                }
            } else if (c == 2) { // MODIFICATION CONNEXION
                // Processus : Identifier l'ancienne connexion -> Identifier la nouvelle -> Appliquer
                System.out.print("Ancienne connexion (ex: M1 G1) : ");
                String[] old = sc.nextLine().split(" ");
                boolean exists = false;
                
                // Vérification de l'existence de l'ancien lien (sens indifférent)
                if (old.length == 2) {
                    exists = loadedNetwork.isConnected(old[0], old[1]);
                    if (!exists) exists = loadedNetwork.isConnected(old[1], old[0]);
                }
                
                if (exists) {
                    System.out.print("Nouvelle connexion (ex: M1 G2) : ");
                    String[] nw = sc.nextLine().split(" ");
                    if (nw.length == 2) {
                        // On connecte à la nouvelle cible (cela déconnecte automatiquement l'ancienne dans Network.java)                        
                        boolean ok = loadedNetwork.addConnection(nw[0], nw[1]);
                        if(!ok) ok = loadedNetwork.addConnection(nw[1], nw[0]);
                        System.out.println(ok ? "Connexion modifiée." : "Erreur lors de la modification.");
                    }
                } else {
                    System.out.println("Connexion introuvable.");
                }
            } else if (c == 3) { // AFFICHAGE
                loadedNetwork.printNetwork();
            } else if (c == 4) { // QUITTER
                return;
            }
        }
    }

    /**
     * Gère la sauvegarde du réseau dans un fichier texte.
     * Demande le nom du fichier à l'utilisateur et délègue l'écriture à Utils.
     */
    private static void handleSave() {
        if (loadedNetwork == null) return;
        System.out.print("Nom du fichier de sortie : ");
        String out = sc.nextLine();
        try {
            Utils.saveNetworkToFile(loadedNetwork, out);
            System.out.println("Réseau sauvegardé avec succès !");
        } catch (Exception e) { System.out.println("Erreur lors de la sauvegarde : " + e.getMessage()); }
    }

    /**
     * Lecture sécurisée d'un entier depuis la console.
     * Empêche le programme de planter si l'utilisateur entre du texte au lieu d'un chiffre.
     * * @return L'entier saisi ou -1 en cas d'erreur.
     */
    private static int readInt() {
        try { 
            return Integer.parseInt(sc.nextLine()); 
        } catch (Exception e) {
            return -1;
        }
    }
}