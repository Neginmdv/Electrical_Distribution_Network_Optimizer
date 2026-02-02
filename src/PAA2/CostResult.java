package PAA2;

/**
 * Classe représentant le résultat du calcul du coût d'un réseau.
 *
 * Cette classe regroupe les trois composantes définies dans le sujet :
 *
 *  - Dispersion (D) :
 *    mesure l'écart entre les taux d'utilisation des générateurs.
 *    Plus la dispersion est faible, plus la charge est équilibrée.
 *
 *  - Surcharge (S) :
 *    représente la part de demande énergétique dépassant
 *    la capacité maximale des générateurs.
 *
 *  - Coût total (C) :
 *    combinaison pondérée des deux métriques :
 *    C = D + lambda × S
 *
 * Cette structure permet de retourner plusieurs valeurs
 * de manière claire et sécurisée depuis la méthode de calcul.
 */
public class CostResult {

    /**
     * Dispersion du réseau.
     * Indique le déséquilibre de charge entre les générateurs.
     */
    public final double disp;

    /**
     * Surcharge totale du réseau.
     * Une valeur strictement positive indique une surcharge.
     */
    public final double surcharge;

    /**
     * Coût total combiné du réseau.
     * Calculé comme : dispersion + lambda × surcharge.
     */
    public final double totalCost;

    /**
     * Constructeur du résultat de coût.
     *
     * @param disp dispersion calculée
     * @param surcharge surcharge totale
     * @param totalCost coût global combiné
     */
    public CostResult(double disp, double surcharge, double totalCost) {
        this.disp = disp;
        this.surcharge = surcharge;
        this.totalCost = totalCost;
    }
}
