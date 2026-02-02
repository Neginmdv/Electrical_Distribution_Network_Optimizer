package PAA2;

/**
 * Classe représentant une maison, c'est-à-dire un consommateur d'énergie
 * dans le réseau électrique.
 *
 * Chaque maison est caractérisée par :
 *  - un nom unique
 *  - un niveau de consommation énergétique
 *
 * La consommation n'est pas représentée par un entier brut,
 * mais par une énumération (ConsumptionLevel) afin de garantir
 * que seules les valeurs autorisées par le sujet soient utilisées
 * (10, 20 ou 40 kW).
 */
public class House {

    // Nom unique de la maison
    private String name;

    // Niveau de consommation énergétique
    private ConsumptionLevel consumption;

    /**
     * Constructeur d'une maison.
     *
     * @param name nom de la maison
     * @param consumption niveau de consommation
     */
    public House(String name, ConsumptionLevel consumption) {
        this.name = name;
        this.consumption = consumption;
    }

    /**
     * @return le nom de la maison
     */
    public String getName() {
        return name;
    }

    /**
     * @return le niveau de consommation de la maison
     */
    public ConsumptionLevel getConsumption() {
        return consumption;
    }

    /**
     * Modifie le niveau de consommation de la maison.
     *
     * @param consumption nouveau niveau de consommation
     */
    public void setConsumption(ConsumptionLevel consumption) {
        this.consumption = consumption;
    }

    /**
     * Représentation textuelle de la maison.
     * Utile pour l'affichage et le débogage.
     */
    @Override
    public String toString() {
        return name + " (" + consumption + " - " + consumption.getValue() + "kW)";
    }
}
