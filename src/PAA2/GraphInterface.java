package PAA2;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class GraphInterface extends Application {

    private Network currentNetwork;
    private Canvas canvas;
    private Label statusLabel;
    private Label costLabel;
    private TextField lambdaField;

    // Constantes graphiques
    private static final double WIDTH = 1000;
    private static final double HEIGHT = 700;
    private static final double NODE_SIZE = 40;
    private static final double MIN_VERTICAL_SPACING = 60;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Projet Réseau Électrique - Interface Graphique");

        // --- HAUT : Barre d'outils ---
        HBox topMenu = new HBox(10);
        topMenu.setPadding(new Insets(10));
        topMenu.setAlignment(Pos.CENTER_LEFT);
        topMenu.setStyle("-fx-background-color: #ddd;");

        Button btnLoad = new Button("Charger Fichier");
        btnLoad.setOnAction(e -> loadFile(primaryStage));

        Button btnSave = new Button("Sauvegarder");
        btnSave.setOnAction(e -> saveFile(primaryStage));

        Label lblLambda = new Label("Lambda:");
        lambdaField = new TextField("10");
        lambdaField.setPrefWidth(50);

        topMenu.getChildren().addAll(btnLoad, btnSave, lblLambda, lambdaField);

        // --- CENTRE : Zone de dessin (Canvas) ---
        canvas = new Canvas(WIDTH, HEIGHT * 0.75);
        BorderPane centerPane = new BorderPane(canvas);
        centerPane.setStyle("-fx-background-color: white; -fx-border-color: black;");

        // --- BAS : Contrôles et Statistiques ---
        VBox bottomPanel = new VBox(10);
        bottomPanel.setPadding(new Insets(15));
        bottomPanel.setStyle("-fx-background-color: #eee;");

        HBox algoButtons = new HBox(15);
        algoButtons.setAlignment(Pos.CENTER);

        Button btnRandom = new Button("Algorithme Naïf (Random)");
        btnRandom.setStyle("-fx-font-size: 14px; -fx-base: #add8e6;");
        btnRandom.setOnAction(e -> runAlgorithm(1));

        Button btnOptimized = new Button("Algorithme Optimisé (Recuit)");
        btnOptimized.setStyle("-fx-font-size: 14px; -fx-base: #90ee90;");
        btnOptimized.setOnAction(e -> runAlgorithm(2));

        algoButtons.getChildren().addAll(btnRandom, btnOptimized);

        statusLabel = new Label("État: Aucun réseau chargé");
        statusLabel.setFont(Font.font("Arial", 14));
        
        costLabel = new Label("Coût Total: N/A");
        costLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        bottomPanel.getChildren().addAll(algoButtons, statusLabel, costLabel);

        BorderPane root = new BorderPane();
        root.setTop(topMenu);
        root.setCenter(centerPane);
        root.setBottom(bottomPanel);

        Scene scene = new Scene(root, WIDTH, HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // --- ACTIONS (Logique métier) ---

    private void loadFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Ouvrir fichier réseau");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers Texte", "*.txt"));
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            NetworkParser parser = new NetworkParser();
            Network net = parser.parseFile(file.getAbsolutePath());
            if (net != null) {
                List<String> missing = net.validateNetwork();
                if (!missing.isEmpty()) {
                    updateStats("Maisons non connectées : " + missing);
                    return;
                }
                this.currentNetwork = net;
                updateStats("Réseau chargé: " + file.getName());
                drawNetwork();
            } else {
                updateStats("Erreur lors du chargement !");
            }
        }
    }

    private void saveFile(Stage stage) {
        if (currentNetwork == null) return;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sauvegarder réseau");
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                Utils.saveNetworkToFile(currentNetwork, file.getAbsolutePath());
                updateStats("Sauvegardé !");
            } catch (Exception e) {
                updateStats("Erreur sauvegarde: " + e.getMessage());
            }
        }
    }

    private void runAlgorithm(int type) {
        if (currentNetwork == null) {
            updateStats("Veuillez d'abord charger un réseau !");
            return;
        }

        try {
            double lambda = Double.parseDouble(lambdaField.getText());
            currentNetwork.setLambda(lambda);
        } catch (NumberFormatException e) {
            updateStats("Lambda invalide !");
            return;
        }

        Solver solver = new Solver();
        Network optimized;
        long start = System.currentTimeMillis();

        optimized = (type == 1)
                ? solver.solveRandom(currentNetwork, 10000)
                : solver.solveOptimized(currentNetwork);

        long time = System.currentTimeMillis() - start;
        this.currentNetwork = optimized;

        drawNetwork();

        CostResult res = currentNetwork.calculateCost();
        String algoName = (type == 1) ? "Naïf" : "Optimisé";
        statusLabel.setText(String.format("Algorithme %s terminé en %d ms", algoName, time));
        costLabel.setText(String.format(
                "Coût: %.4f (Disp: %.4f, Surch: %.4f)",
                res.totalCost, res.disp, res.surcharge));
    }

    private void updateStats(String msg) {
        statusLabel.setText("État: " + msg);
    }

    // --- LOGIQUE DE DESSIN (CANVAS) ---

    private void drawNetwork() {
        if (currentNetwork == null) return;

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setTransform(1, 0, 0, 1, 0, 0);
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        List<Generator> gens = new ArrayList<>(currentNetwork.getGenerators().values());
        List<House> houses = new ArrayList<>(currentNetwork.getHouses().values());
        Map<House, Generator> connections = currentNetwork.getHouseToGen();

        gens.sort(Comparator.comparing(Generator::getName));
        houses.sort(Comparator.comparing(House::getName));

        int maxNodes = Math.max(gens.size(), houses.size());
        double requiredHeight = maxNodes * MIN_VERTICAL_SPACING + 100;
        double scale = Math.min(1.0, canvas.getHeight() / requiredHeight);

        gc.scale(scale, scale);

        double genX = 150;
        double houseX = (canvas.getWidth() / scale) - 150;

        double genYStep = MIN_VERTICAL_SPACING;
        double houseYStep = MIN_VERTICAL_SPACING;

        gc.setStroke(Color.GRAY);
        gc.setLineWidth(2);

        for (int i = 0; i < houses.size(); i++) {
            House h = houses.get(i);
            Generator g = connections.get(h);
            if (g != null) {
                int gIndex = gens.indexOf(g);
                double startX = genX + NODE_SIZE;
                double startY = (gIndex + 1) * genYStep + NODE_SIZE / 2;
                double endX = houseX;
                double endY = (i + 1) * houseYStep + NODE_SIZE / 2;
                gc.strokeLine(startX, startY, endX, endY);
            }
        }

        for (int i = 0; i < gens.size(); i++) {
            double y = (i + 1) * genYStep;
            Generator g = gens.get(i);

            gc.setFill(Color.LIGHTBLUE);
            gc.setStroke(Color.BLUE);
            gc.fillRect(genX, y, NODE_SIZE, NODE_SIZE);
            gc.strokeRect(genX, y, NODE_SIZE, NODE_SIZE);

            gc.setFill(Color.BLACK);
            gc.fillText(g.getName(), genX - 40, y + 25);
            gc.fillText(g.getCapacity() + "kW", genX, y + NODE_SIZE + 15);
        }

        for (int i = 0; i < houses.size(); i++) {
            double y = (i + 1) * houseYStep;
            House h = houses.get(i);

            Color hColor;
            switch (h.getConsumption()) {
                case FORTE:  hColor = Color.TOMATO; break;
                case NORMAL: hColor = Color.ORANGE; break;
                case BASSE:  hColor = Color.LIGHTGREEN; break;
                default:     hColor = Color.GRAY;
            }

            gc.setFill(hColor);
            gc.setStroke(Color.DARKGREEN);
            gc.fillOval(houseX, y, NODE_SIZE, NODE_SIZE);
            gc.strokeOval(houseX, y, NODE_SIZE, NODE_SIZE);

            gc.setFill(Color.BLACK);
            gc.fillText(h.getName(), houseX + NODE_SIZE + 10, y + 25);
            gc.fillText(h.getConsumption().getValue() + "kW", houseX, y + NODE_SIZE + 15);
        }
    }
}
