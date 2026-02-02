package PAA2;

/*
 * Enumération représentant les différents niveaux de consommation électrique d'une maison.
 * 
 * Ce type énuméré impose les trois catégories définies dans le sujet :
 * 
 * BASSE : 10 kW
 * NORMAL : 20 kW
 * FORTE : 40 kW
 * 
 * Il permet de manipuler ces niveaux de manière sûre et typée plutôt que d'utiliser de simples entiers.
 */

public enum ConsumptionLevel {
    BASSE(10),
    NORMAL(20),
    FORTE(40);

    // La valeur numérique de la consommation en kilowatts (kW).
    private final int value;

    // Constructeur
    ConsumptionLevel(int value) {
        this.value = value;
    }

    //GETTER
    public int getValue() {
        return value;
    }

    // Convertit une chaîne de caractères en un niveau de consommation (ConsumptionLevel).
    // Cette méthode est utile lors de la lecture du fichier texte ou de la saisie utilisateur.
    // Elle gère les majuscules/minuscules et accepte "NORMAL" ou "NORMALE".
    public static ConsumptionLevel fromString(String text) {

        // On refuse les valeurs nulles.
        if (text == null) {
            throw new IllegalArgumentException("Le type de consommation ne peut pas être null.");
        }        
        // On nettoie la chaîne (trim) et on la met en majuscules pour éviter les erreurs de saisie.
        switch (text.trim().toUpperCase()) {
            case "BASSE": return BASSE;
            case "NORMAL": 
            case "NORMALE": return NORMAL;
            case "FORTE": return FORTE;
            default: throw new IllegalArgumentException("Type inconnu: " + text);
        }
    }
}