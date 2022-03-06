package kanagawa.views;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.util.Callback;
import kanagawa.utils.Utils;
import kanagawa.models.*;
import kanagawa.models.enums.Bonus;
import kanagawa.models.enums.Skill;

import java.util.*;

public class MainGameController {

    private Game game; // Game instance

    @FXML
    private VBox playersList, availableDiplomasList;

    @FXML
    private AnchorPane one, two, three, four, five, six, seven, eight, nine, ten, eleven, twelve;

    @FXML
    private Label currentPlayerUsername;

    @FXML
    private Label creditCount, penCount, mathCount, infoCount, energyCount, industryCount, ergoCount, mechaCount,
            managementCount, languageCount;

    @FXML
    private Button firstColumnButton, secondColumnButton, thirdColumnButton, fourthColumnButton;

    @FXML
    private Button nextPlayerButton;

    @FXML
    private Label roundCountLabel;

    @FXML
    private Button quitGameButton;

    @FXML
    private HBox cardsList;

    private int countNewDistribution = 0; // count to know when to distribute again
    private int countSpaceRemainingOnBoard = 0; // count to know if board is full

    /**
     * This method is automatically called when the window is created
     * Initializes display of elements on the screen
     */
    @FXML
    public void initialize() {
        game = Game.getGameInstance();

        game.shuffleCards();
        game.randomFirstCardForPlayers();
        game.getCurrentRound().setRemainingPlayers(game.getPlayers());
        game.getCurrentRound().initBoardWithPlayersCount();

        game.distributeCards(); // First distribution

        createPlayers(game.getPlayers());

        updateData();
        showCardsOnBoard();
    }

    /**
     * Method called when the exit button is clicked
     * 
     * @param event button clicked
     */
    @FXML
    public void onQuitGameButtonClicked(MouseEvent event) {
        Utils.closeWindow(event);
    }

    /**
     * Method called when the next player button is clicked
     * 
     * @param event button clicked
     */
    @FXML
    public void onNextPlayerButtonClicked(MouseEvent event) {
        countNewDistribution++;

        if (countSpaceRemainingOnBoard >= 2 && countNewDistribution == game.getCurrentRound().getPlayers().size()) {
            nextPlayerButton.setDisable(true);
            if (game.getCurrentRound().getPlayers().isEmpty())
                countSpaceRemainingOnBoard = 0;

        }

        if (countNewDistribution == game.getCurrentRound().getPlayers().size() && countSpaceRemainingOnBoard < 2) {
            {
                game.distributeCards();
                countNewDistribution = 0;
                countSpaceRemainingOnBoard++;
            }

        }

        game.getCurrentRound().getCurrentPlayer().setPlaying(false);
        if (!game.getCurrentRound().getPlayers().isEmpty()) {
            game.getCurrentRound().nextPlayer();
        } else {

            game.nextRound();
            game.getCurrentRound().initBoardWithPlayersCount();
            game.distributeCards();
            roundCountLabel.setText("Tour n°" + game.getRoundCount());
            countNewDistribution = 0;
            countSpaceRemainingOnBoard = 0;
        }
        enableButtons();
        updateData();
        showCardsOnBoard();
    }

    /**
     * Method called when take first column button is clicked
     * Starts choice loop for first column for the player
     * 
     * @param event button clicked
     */
    @FXML
    public void onFirstColumnButtonClicked(MouseEvent event) {
        takeCardColumn(0);
    }

    /**
     * Method called when take second column button is clicked
     * Starts choice loop for second column for the player
     * 
     * @param event button clicked
     */
    @FXML
    public void onSecondColumnButtonClicked(MouseEvent event) {
        takeCardColumn(1);
    }

    /**
     * Method called when take third column button is clicked
     * Starts choice loop for third column for the player
     * 
     * @param event button clicked
     */
    @FXML
    public void onThirdColumnButtonClicked(MouseEvent event) {
        takeCardColumn(2);
    }

    /**
     * Method called when take fourth column button is clicked
     * Starts choice loop for fourth column for the player
     * 
     * @param event button clicked
     */
    @FXML
    public void onFourthColumnButtonClicked(MouseEvent event) {
        takeCardColumn(3);
    }

    /**
     * Starts the choice loop for taking a column of cards.
     * For each cards in the column, asks the player his choice with a dialog box
     * If the column is taken, removes it from the board
     * 
     * @param colIndex the column to take
     */
    private void takeCardColumn(int colIndex) {
        ArrayList<Card> firstColumnCards = game.getCurrentRound().getGameBoard()[colIndex];

        boolean deleteColumn = false;

        int columnSize = firstColumnCards.size();

        HashMap<Card, Boolean> takenCards = new HashMap<>();

        for (int i = 0; i < columnSize; i++) {
            HashMap<Card, Boolean> result = createChoiceDialog(firstColumnCards); // Create dialog box
            if (result != null) {// If button pressed is other than "cancel" button
                deleteColumn = true;
                Map.Entry<Card, Boolean> entry = result.entrySet().iterator().next();
                if (result.get(entry.getKey())) { // if player wants to add card to personal work
                    game.getCurrentRound().getCurrentPlayer().addToPersonalWork(entry.getKey());
                    takenCards.put(entry.getKey(), true);
                } else { // if player wants to add card to UV
                    Player currentPlayer = game.getCurrentRound().getCurrentPlayer();
                    boolean hasSkill = currentPlayer.hasSkillAvailable(entry.getKey().getUv().getSkill());
                    if (hasSkill) { // Checks if player has necessary skill to add UV
                        game.getCurrentRound().getCurrentPlayer().addToUv(entry.getKey());
                        takenCards.put(entry.getKey(), false);
                    } else { // If the player does not has the necessary skill, show an error dialog box
                        Alert errorDialog = new Alert(Alert.AlertType.ERROR);
                        errorDialog.setTitle("Erreur");
                        errorDialog.setHeaderText("Impossible d'ajouter l'UV");
                        errorDialog.setContentText("Vérifier les compétences disponibles et les stylos");
                        errorDialog.showAndWait();

                        fireEventButtonEvent(colIndex); // Come back to column choice

                        deleteColumn = false;

                        break;
                    }
                }

                firstColumnCards.remove(entry.getKey()); // Delete card from list

                updateData(); // Update data on interface
            } else { // If the cancel button is clicked
                deleteColumn = false;
                // Put back taken cards in the list (because the choice sequence was cancelled)
                for (Map.Entry<Card, Boolean> entry : takenCards.entrySet()) {
                    if (entry.getValue()) {
                        game.getCurrentRound().getCurrentPlayer().getInventory().getPwPossessed()
                                .remove(entry.getKey().getPersonalWork());
                    } else {
                        game.getCurrentRound().getCurrentPlayer().getInventory().getUvPossessed()
                                .remove(entry.getKey().getUv());
                    }

                    firstColumnCards.add(0, entry.getKey());
                }

                break;
            }
        }

        if (deleteColumn) { // If the column was taken
            game.getCurrentRound().removeColumn(colIndex); // remove the column from the board
            showCardsOnBoard(); // Update card display on the interface
            disableAllButtons();
            game.getCurrentRound().getPlayers().remove(game.getCurrentRound().getCurrentPlayer()); // Current player
                                                                                                   // cannot play
                                                                                                   // anymore (for this
                                                                                                   // round)
            countNewDistribution--;
            nextPlayerButton.setDisable(false);
        }

        updateData(); // Update data on the interface
    }

    /**
     * Creates a choice dialog box for the taking column choice sequence and
     * displays it on the screen
     * 
     * @param data the data to add to combo list in the dialog box
     * @return the choice made by the player
     */
    private HashMap<Card, Boolean> createChoiceDialog(ArrayList<Card> data) {
        ChoiceDialog dialog = new ChoiceDialog(data.get(0), data);
        dialog.setTitle("Faites votre choix !");
        dialog.setHeaderText("Faites votre choix !");
        dialog.getDialogPane().getButtonTypes().remove(0);

        ButtonType travailPersonelNoPen = new ButtonType("Travail Perso " + "\n" + "(Sans stylo)",
                ButtonBar.ButtonData.OK_DONE);
        ButtonType travailPersonelPen = new ButtonType("Travail Perso " + "\n" + "(Avec stylo)",
                ButtonBar.ButtonData.OK_DONE);
        ButtonType UV = new ButtonType("UV", ButtonBar.ButtonData.OK_DONE);

        dialog.getDialogPane().getButtonTypes().addAll(travailPersonelPen, travailPersonelNoPen, UV);

        // Apply event listeners on the choice dialog's buttons
        dialog.setResultConverter(new Callback<ButtonType, HashMap<Card, Boolean>>() {
            @Override
            public HashMap<Card, Boolean> call(ButtonType b) {
                if (b == travailPersonelNoPen) {
                    HashMap<Card, Boolean> res = new HashMap<>();
                    res.put((Card) dialog.getSelectedItem(), true);
                    updateData();
                    return res;
                }

                if (b == travailPersonelPen) {
                    HashMap<Card, Boolean> res = new HashMap<>();
                    Card selectedCard = (Card) dialog.getSelectedItem();
                    if (game.getCurrentRound().getCurrentPlayer().checkPenCount()) {
                        selectedCard.getPersonalWork().setHasPen(true);
                        game.getCurrentRound().getCurrentPlayer().removePen();
                    }

                    res.put(selectedCard, true);
                    updateData();
                    return res;
                }

                if (b == UV) {
                    HashMap<Card, Boolean> res = new HashMap<>();
                    res.put((Card) dialog.getSelectedItem(), false);
                    updateData();
                    return res;
                }

                return null;
            }
        });

        Optional result = dialog.showAndWait();
        if (result.isPresent()) {
            return (HashMap<Card, Boolean>) result.get();
        }
        return null;
    }

    /**
     * Allows to programmatically fire event on a column event
     * This allow to come back to the choice sequence after an error panel (If
     * player cannot add UV)
     * 
     * @param colIndex the column you want to come back to
     */
    private void fireEventButtonEvent(int colIndex) {
        Button button = null;

        switch (colIndex) {
            case 0:
                button = firstColumnButton;
                break;
            case 1:
                button = secondColumnButton;
                break;
            case 2:
                button = thirdColumnButton;
                break;
            case 3:
                button = fourthColumnButton;
                break;
            default:
                break;
        }

        double buttonX = button.getWidth() / 2;
        double buttonY = button.getHeight() / 2;

        Point2D screenCoords = button.localToScreen(buttonX, buttonX);
        Point2D sceneCoords = button.localToScene(buttonX, buttonY);

        button.fireEvent(
                new MouseEvent(MouseEvent.MOUSE_CLICKED, sceneCoords.getX(), sceneCoords.getY(), screenCoords.getX(),
                        screenCoords.getY(), MouseButton.PRIMARY, 1, true, true, true, true, true, true, true, true,
                        true, true, null));
    }

    /**
     * From the players list, displays them in the list at the right of the screen.
     * Also displays their characteristics (isPlaying, hasProfessor, isFirstPlayer)
     * 
     * @param players
     */
    private void createPlayers(ArrayList<Player> players) {
        playersList.getChildren().clear();

        playersList.getChildren().clear();
        for (Player player : game.getPlayers()) {
            AnchorPane anchorPane = new AnchorPane();
            anchorPane.setPrefWidth(199);
            anchorPane.setPrefHeight(87);
            anchorPane.setStyle("-fx-background-color: white; -fx-border-color: black");

            Label label = new Label(player.getUsername());
            label.setLayoutX(14);
            label.setLayoutY(32);

            label.setFont(new Font("Verdana", 18));

            anchorPane.getChildren().add(label);

            if (player.isFirstPlayer()) {
                ImageView imageView = new ImageView(
                        new Image(Objects.requireNonNull(getClass().getResourceAsStream("assets/medal.png"))));

                imageView.setFitWidth(18);
                imageView.setFitHeight(18);

                imageView.setLayoutX(166);
                imageView.setLayoutY(8);

                anchorPane.getChildren().add(imageView);
            }

            if (player.getInventory().hasProfessor()) {
                ImageView imageView = new ImageView(
                        new Image(Objects.requireNonNull(getClass().getResourceAsStream("assets/professor.png"))));

                imageView.setFitWidth(18);
                imageView.setFitHeight(18);

                imageView.setLayoutX(166);
                imageView.setLayoutY(34);

                anchorPane.getChildren().add(imageView);
            }

            if (player.isPlaying()) {
                ImageView imageView = new ImageView(new Image(
                        Objects.requireNonNull(getClass().getResourceAsStream("assets/game-controller.png"))));

                imageView.setFitWidth(18);
                imageView.setFitHeight(18);

                imageView.setLayoutX(166);
                imageView.setLayoutY(61);

                anchorPane.getChildren().add(imageView);
            }

            playersList.getChildren().add(anchorPane);
        }
    }

    /**
     * Takes player data and displays it on the left of the screen (skill counts)
     */
    private void showPlayerData() {
        Player currentPlayer = game.getCurrentRound().getCurrentPlayer();
        Inventory currentPlayerInventory = currentPlayer.getInventory();

        currentPlayerUsername.setText(currentPlayer.getUsername());
        creditCount.setText(String.valueOf(currentPlayerInventory.getCredits()));
        penCount.setText(String.valueOf(currentPlayerInventory.getPenCount()));
        mathCount.setText(String.valueOf(currentPlayerInventory.getSkillCount(Skill.MATH)));
        infoCount.setText(String.valueOf(currentPlayerInventory.getSkillCount(Skill.INFO)));
        energyCount.setText(String.valueOf(currentPlayerInventory.getSkillCount(Skill.ENERGY)));
        industryCount.setText(String.valueOf(currentPlayerInventory.getSkillCount(Skill.INDUSTRY)));
        ergoCount.setText(String.valueOf(currentPlayerInventory.getSkillCount(Skill.ERGO)));
        mechaCount.setText(String.valueOf(currentPlayerInventory.getSkillCount(Skill.MECHANICS)));
        managementCount.setText(String.valueOf(currentPlayerInventory.getSkillCount(Skill.MANAGEMENT)));
        languageCount.setText(String.valueOf(currentPlayerInventory.getSkillCount(Skill.LANGUAGE)));
    }

    /**
     * Displays all the cards present on the board
     */
    private void showCardsOnBoard() {
        HashMap<Integer, Card> cards = new HashMap<>();
        for (int i = 0; i < game.getCurrentRound().getGameBoard().length; i++) {
            if (game.getCurrentRound().getGameBoard()[i] != null) {
                for (int j = 0; j < game.getCurrentRound().getGameBoard()[i].size(); j++) {
                    cards.put(i + j * 4, game.getCurrentRound().getGameBoard()[i].get(j));
                }
            } else {
                int k = i;
                for (int j = 0; j < 3; j++) {
                    AnchorPane anchorPane = getAnchorPaneFromPositionNumber(k);
                    anchorPane.getChildren().clear();
                    k += 4;
                }

            }

        }

        for (Map.Entry<Integer, Card> entry : cards.entrySet()) {
            displayCardOnBoard(entry.getValue(), getAnchorPaneFromPositionNumber(entry.getKey()));
        }
    }

    /**
     * Display one card on the board at a specific position
     * 
     * @param card     The card to display
     * @param position The position where to display the card
     */
    private void displayCardOnBoard(Card card, AnchorPane position) {
        AnchorPane uv = new AnchorPane();
        uv.setPrefWidth(164);
        uv.setPrefHeight(200);
        uv.setLayoutX(157.0);

        AnchorPane.setBottomAnchor(uv, 0.0);
        AnchorPane.setTopAnchor(uv, 0.0);
        AnchorPane.setRightAnchor(uv, 0.0);
        uv.setStyle("-fx-border-color: black;");

        AnchorPane pw = new AnchorPane();
        pw.setPrefWidth(155);
        pw.setPrefHeight(200);
        AnchorPane.setBottomAnchor(pw, 0.0);
        AnchorPane.setTopAnchor(pw, 0.0);
        AnchorPane.setLeftAnchor(pw, 0.0);

        // Adding elements in the Personal Work section (left section)
        Label bonus = new Label("Bonus");
        bonus.setFont(new Font("Verdana Bold", 12));
        AnchorPane.setRightAnchor(bonus, 106.33333333333334);
        AnchorPane.setTopAnchor(bonus, 14.0);

        ImageView bonusImageView = new ImageView(new Image(Objects.requireNonNull(
                getClass().getResourceAsStream(getImageUrlFromBonus(card.getPersonalWork().getBonus())))));
        bonusImageView.setFitWidth(30);
        bonusImageView.setFitHeight(30);
        bonusImageView.setPickOnBounds(true);
        bonusImageView.setPreserveRatio(true);
        AnchorPane.setRightAnchor(bonusImageView, 104.33333333333334);
        AnchorPane.setTopAnchor(bonusImageView, 42.0);

        Label skill = new Label("Compétence");
        skill.setFont(new Font("Verdana Bold", 12));
        AnchorPane.setRightAnchor(skill, 65.0);
        AnchorPane.setTopAnchor(skill, 92.0);

        ImageView skillImageView = new ImageView(new Image(Objects.requireNonNull(
                getClass().getResourceAsStream(getImageUrlFromSkill(card.getPersonalWork().getSkill())))));
        skillImageView.setFitWidth(30);
        skillImageView.setFitHeight(30);
        skillImageView.setPickOnBounds(true);
        skillImageView.setPreserveRatio(true);
        AnchorPane.setRightAnchor(skillImageView, 104.33333333333334);
        AnchorPane.setBottomAnchor(skillImageView, 42.66666666666666);

        // Adding elements in the UV section (right section)
        Label uvCode = new Label(card.getUv().getCode());
        uvCode.setFont(new Font("Verdana Bold", 18));
        AnchorPane.setRightAnchor(uvCode, 50.33333333333334);
        AnchorPane.setTopAnchor(uvCode, 14.0);

        Label categoryLabel = new Label("Catégorie :");
        categoryLabel.setFont(new Font(13));
        AnchorPane.setRightAnchor(categoryLabel, 86.33333333333333);
        AnchorPane.setTopAnchor(categoryLabel, 52.0);

        Label category = new Label(card.getUv().getUvCategory().toString());
        category.setFont(new Font(20));
        AnchorPane.setBottomAnchor(category, 119.66666666666666);
        AnchorPane.setRightAnchor(category, 18.0);
        AnchorPane.setTopAnchor(category, 42.0);

        Label requiredSkillLabel = new Label("Compétence requise");
        requiredSkillLabel.setFont(new Font("Verdana Bold", 13));
        AnchorPane.setRightAnchor(requiredSkillLabel, 21.666666666666657);
        AnchorPane.setTopAnchor(requiredSkillLabel, 100.0);

        ImageView requiredSkillImageView = new ImageView(new Image(
                Objects.requireNonNull(getClass().getResourceAsStream(getImageUrlFromSkill(card.getUv().getSkill())))));
        requiredSkillImageView.setFitWidth(42);
        requiredSkillImageView.setFitHeight(42);
        requiredSkillImageView.setPickOnBounds(true);
        requiredSkillImageView.setPreserveRatio(true);
        AnchorPane.setRightAnchor(requiredSkillImageView, 64.33333333333334);
        AnchorPane.setTopAnchor(requiredSkillImageView, 133.0);

        pw.getChildren().addAll(bonus, bonusImageView, skill, skillImageView);
        uv.getChildren().addAll(uvCode, categoryLabel, category, requiredSkillLabel, requiredSkillImageView);

        position.getChildren().addAll(uv, pw);
    }

    /**
     * Display all the cards that the player owns on the list at the bottom
     */
    private void showPlayerCards() {
        cardsList.getChildren().clear();

        for (UV uv : game.getCurrentRound().getCurrentPlayer().getInventory().getUvPossessed()) {
            displayCardUv(uv);
        }

        for (PersonalWork pw : game.getCurrentRound().getCurrentPlayer().getInventory().getPwPossessed()) {
            displayCardPersonalWork(pw);
        }
    }

    /**
     * Display all the available diplomas that the player owns on the list at bottom
     * left
     */
    private void showAvailableDiplomas() {
        availableDiplomasList.getChildren().clear();

        ArrayList<Diploma> availableDiplomas = game.getCurrentRound().getCurrentPlayer().findAvailableDiplomas();

        if (availableDiplomas != null) {
            for (Diploma diploma : game.getCurrentRound().getCurrentPlayer().findAvailableDiplomas()) {
                displayAvailableDiploma(diploma);
            }
        }
    }

    /**
     * Displays one UV on the bottom list
     * 
     * @param uv The UV to display
     */
    private void displayCardUv(UV uv) {
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPrefWidth(250);
        anchorPane.setStyle("-fx-border-color: black; -fx-background-color: white;");

        Label uvCode = new Label(uv.getCode());
        uvCode.setFont(new Font("Tahoma Bold", 24));
        uvCode.setLayoutX(107);
        uvCode.setLayoutY(26);
        AnchorPane.setBottomAnchor(uvCode, 326.0);
        AnchorPane.setRightAnchor(uvCode, 107.66666666666669);
        AnchorPane.setTopAnchor(uvCode, 26.0);

        Label category = new Label("Catégorie :");
        category.setFont(new Font(19));
        category.setLayoutX(14);
        category.setLayoutY(110);
        AnchorPane.setBottomAnchor(category, 243.0);
        AnchorPane.setLeftAnchor(category, 14.0);
        AnchorPane.setTopAnchor(category, 110.0);

        Label cat = new Label(uv.getUvCategory().toString());
        cat.setFont(new Font(28));
        cat.setLayoutX(192);
        cat.setLayoutY(105);
        AnchorPane.setBottomAnchor(cat, 242.0);
        AnchorPane.setRightAnchor(cat, 24.0);
        AnchorPane.setTopAnchor(cat, 109.0);

        Label requiredSkill = new Label("Compétence requise");
        requiredSkill.setFont(new Font("Verdana Bold", 13));
        requiredSkill.setLayoutX(53);
        requiredSkill.setLayoutY(210);
        AnchorPane.setBottomAnchor(requiredSkill, 166.0);
        AnchorPane.setRightAnchor(requiredSkill, 65.0);
        AnchorPane.setTopAnchor(requiredSkill, 211.0);

        Line line = new Line();
        line.setStartX(-100);
        line.setEndX(100);
        line.setLayoutX(139);
        line.setLayoutY(79);
        AnchorPane.setBottomAnchor(line, 315.8333333333333);
        AnchorPane.setRightAnchor(line, 39.166666666666686);
        AnchorPane.setTopAnchor(line, 78.5);
        AnchorPane.setLeftAnchor(line, 38.5);

        ImageView imageView = new ImageView(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream(getImageUrlFromSkill(uv.getSkill())))));
        imageView.setFitHeight(42);
        imageView.setFitWidth(42);
        imageView.setLayoutX(119);
        imageView.setLayoutY(286);
        imageView.setPickOnBounds(true);
        imageView.setPreserveRatio(true);
        AnchorPane.setBottomAnchor(imageView, 69.33333333333331);
        AnchorPane.setRightAnchor(imageView, 119.66666666666669);
        AnchorPane.setTopAnchor(imageView, 286.0);

        anchorPane.getChildren().addAll(uvCode, category, cat, requiredSkill, line, imageView);

        cardsList.getChildren().add(anchorPane);
    }

    /**
     * Displays one Personal Work on the bottom list
     * 
     * @param uv The Personal Work to display
     */
    private void displayCardPersonalWork(PersonalWork pw) {
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPrefWidth(200);
        anchorPane.setStyle("-fx-border-color: black; -fx-background-color: white;");

        Label bonusLabel = new Label("Bonus");
        bonusLabel.setFont(new Font("System Bold", 16));
        bonusLabel.setLayoutX(77);
        bonusLabel.setLayoutY(14);

        ImageView bonusImageView = new ImageView(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream(getImageUrlFromBonus(pw.getBonus())))));
        bonusImageView.setFitHeight(42);
        bonusImageView.setFitWidth(42);
        bonusImageView.setLayoutX(79);
        bonusImageView.setLayoutY(63);
        bonusImageView.setPickOnBounds(true);
        bonusImageView.setPreserveRatio(true);

        Label skillLabel = new Label("Compétence :");
        skillLabel.setFont(new Font("System bold", 16));
        skillLabel.setLayoutX(53);
        skillLabel.setLayoutY(174);

        ImageView skillImageView = new ImageView(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream(getImageUrlFromSkill(pw.getSkill())))));
        skillImageView.setFitHeight(42);
        skillImageView.setFitWidth(42);
        skillImageView.setLayoutX(32);
        skillImageView.setLayoutY(253);
        skillImageView.setPickOnBounds(true);
        skillImageView.setPreserveRatio(true);

        CheckBox checkBox = new CheckBox();
        checkBox.setLayoutX(121);
        checkBox.setLayoutY(265);
        checkBox.setText("Stylo");
        checkBox.setSelected(pw.hasPen());
        Player currentPlayer = game.getCurrentRound().getCurrentPlayer();
        if (!currentPlayer.checkPenCount() && !checkBox.isSelected()) {
            checkBox.setDisable(true);
        }

        checkBox.selectedProperty().addListener(
                (ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
                    if (new_val) {
                        boolean hasEnoughPen = currentPlayer.checkPenCount();
                        if (hasEnoughPen) {
                            currentPlayer.removePen();
                            currentPlayer.getInventory().getPwPossessed()
                                    .get(currentPlayer.getInventory().getPwPossessed().indexOf(pw)).setHasPen(true);
                        }
                    } else {
                        currentPlayer.addPen();
                        currentPlayer.getInventory().getPwPossessed()
                                .get(currentPlayer.getInventory().getPwPossessed().indexOf(pw)).setHasPen(false);
                    }

                    updateData();
                });

        anchorPane.getChildren().addAll(bonusLabel, bonusImageView, skillLabel, skillImageView, checkBox);

        cardsList.getChildren().add(anchorPane);
    }

    /**
     * Displays one available diploma on the bottom left list
     * 
     * @param diploma The available diploma to display
     */
    private void displayAvailableDiploma(Diploma diploma) {
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPrefHeight(57);
        anchorPane.setStyle("-fx-background-color: white; -fx-border-color: black;");

        Button checkButton = new Button(" ");
        checkButton.setLayoutX(233);
        checkButton.setLayoutY(13);
        checkButton.setPrefHeight(32);
        checkButton.setPrefWidth(30);
        checkButton.setStyle("-fx-background-color: #ffbe76;");

        checkButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                game.getCurrentRound().getCurrentPlayer().getInventory().addDiploma(diploma);
                updateData();
            }
        });

        ImageView checkImageView = new ImageView(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("assets/check.png"))));
        checkImageView.setFitHeight(22);
        checkImageView.setFitWidth(22);
        checkImageView.setLayoutX(277);
        checkImageView.setLayoutY(18);
        checkImageView.setPickOnBounds(true);
        checkImageView.setPreserveRatio(true);

        checkButton.setGraphic(checkImageView);

        Button waitButton = new Button(" ");
        waitButton.setLayoutX(273);
        waitButton.setLayoutY(13);
        waitButton.setPrefHeight(32);
        waitButton.setPrefWidth(30);
        waitButton.setStyle("-fx-background-color: #6ab04c;");

        waitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                game.getCurrentRound().getCurrentPlayer().getInventory().addRefusedDiploma(diploma);
                updateData();
            }
        });

        ImageView waitImageView = new ImageView(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("assets/hourglass.png"))));
        waitImageView.setFitHeight(22);
        waitImageView.setFitWidth(22);
        waitImageView.setLayoutX(237);
        waitImageView.setLayoutY(18);
        waitImageView.setPickOnBounds(true);
        waitImageView.setPreserveRatio(true);

        waitButton.setGraphic(waitImageView);

        ImageView diplomaGroupImageView = new ImageView(new Image(Objects.requireNonNull(
                getClass().getResourceAsStream(getImageUrlFromDiplomaGroup(diploma.getGroup().getGroupeName())))));
        diplomaGroupImageView.setFitHeight(33);
        diplomaGroupImageView.setFitWidth(33);
        diplomaGroupImageView.setLayoutX(16);
        diplomaGroupImageView.setLayoutY(12);
        diplomaGroupImageView.setPickOnBounds(true);
        diplomaGroupImageView.setPreserveRatio(true);

        ImageView creditImageView = new ImageView(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("assets/credit.png"))));
        creditImageView.setFitHeight(25);
        creditImageView.setFitWidth(21);
        creditImageView.setLayoutX(76);
        creditImageView.setLayoutY(17);
        creditImageView.setPickOnBounds(true);
        creditImageView.setPreserveRatio(true);

        Label label = new Label(String.valueOf(diploma.getCredit()));
        label.setLayoutX(62);
        label.setLayoutY(19);

        anchorPane.getChildren().addAll(checkButton, waitButton, checkImageView, waitImageView, diplomaGroupImageView,
                creditImageView, label);

        availableDiplomasList.getChildren().add(anchorPane);
    }

    /**
     * If the game is over, displays a panel with the ranking of the players
     */
    private void displayEndGamePanel() {
        if (game.checkGameIsOver()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Fin de la partie !");
            alert.setHeaderText("Partie terminée !");
            String s = "";
            ArrayList<Player> sortedPlayers = new ArrayList<>(game.getPlayers());
            sortedPlayers.sort(new Comparator<Player>() {
                @Override
                public int compare(Player o1, Player o2) {
                    return Integer.compare(o2.getInventory().getCredits(), o1.getInventory().getCredits());
                }
            });
            for (int i = 0; i < sortedPlayers.size(); i++) {
                s += (i + 1) + ". " + sortedPlayers.get(i).getUsername() + " : "
                        + sortedPlayers.get(i).getInventory().getCredits() + " crédits.\n";
            }

            alert.setOnCloseRequest(new EventHandler<DialogEvent>() {
                @Override
                public void handle(DialogEvent dialogEvent) {
                    System.exit(0);
                }
            });

            alert.setContentText(s);

            alert.showAndWait();

        }

    }

    /**
     * Disable column buttons if the column is null
     */
    private void disableButtons() {
        if (game.getCurrentRound().getGameBoard()[0] == null) {
            firstColumnButton.setDisable(true);
        }

        if (game.getCurrentRound().getGameBoard()[1] == null) {
            secondColumnButton.setDisable(true);
        }

        if (game.getCurrentRound().getGameBoard()[2] == null) {
            thirdColumnButton.setDisable(true);
        }

        if (game.getCurrentRound().getGameBoard()[3] == null) {
            fourthColumnButton.setDisable(true);
        }
    }

    /**
     * Disables all column buttons
     */
    private void disableAllButtons() {
        firstColumnButton.setDisable(true);
        secondColumnButton.setDisable(true);
        thirdColumnButton.setDisable(true);
        fourthColumnButton.setDisable(true);
    }

    /**
     * Checks if there are any diplomas available for the players. If so, the player
     * cannot skip his turn
     */
    private void checkDiplomasAvailable() {
        if (game.getCurrentRound().getCurrentPlayer().findAvailableDiplomas() != null) {
            nextPlayerButton.setDisable(true);
        } else {
            nextPlayerButton.setDisable(false);
        }
    }

    /**
     * Enable all column buttons
     */
    private void enableButtons() {
        if (game.getCurrentRound().getGameBoard()[0] != null) {
            firstColumnButton.setDisable(false);
        }

        if (game.getCurrentRound().getGameBoard()[1] != null) {
            secondColumnButton.setDisable(false);
        }

        if (game.getCurrentRound().getGameBoard()[2] != null) {
            thirdColumnButton.setDisable(false);
        }

        if (game.getCurrentRound().getGameBoard()[3] != null) {
            fourthColumnButton.setDisable(false);
        }
    }

    /**
     * Returns the image path corresponding to a skill
     * 
     * @param skill
     * @return
     */
    private String getImageUrlFromSkill(Skill skill) {
        String urlBase = "assets/";
        switch (skill) {
            case MATH:
                urlBase += "math.png";
                break;
            case INFO:
                urlBase += "info.png";
                break;
            case ENERGY:
                urlBase += "energy.png";
                break;
            case ERGO:
                urlBase += "ergo.png";
                break;
            case MECHANICS:
                urlBase += "mechanics.png";
                break;
            case INDUSTRY:
                urlBase += "industry.png";
                break;
            case MANAGEMENT:
                urlBase += "management.png";
                break;
            case LANGUAGE:
                urlBase += "language.png";
                break;
            default:
                break;
        }

        return urlBase;
    }

    /**
     * Returns the image path corresponding to a bonus
     * 
     * @param bonus
     * @return
     */
    private String getImageUrlFromBonus(Bonus bonus) {
        String urlBase = "assets/";
        switch (bonus) {
            case PEN:
                urlBase += "pen.png";
                break;
            case PROFESSOR:
                urlBase += "professor.png";
                break;
            case CREDIT:
                urlBase += "credit.png";
                break;
            case DOUBLE_CREDIT:
                urlBase += "double_credits.png";
                break;
            default:
                urlBase += "empty.png";
                break;
        }

        return urlBase;
    }

    /**
     * Returns the image path corresponding to a diplomaGroup name
     * 
     * @param diplomaGroup
     * @return
     */
    private String getImageUrlFromDiplomaGroup(String diplomaGroup) {
        String urlBase = "assets/";
        switch (diplomaGroup) {
            case "INFO":
                urlBase += "info.png";
                break;
            case "ENERGY":
                urlBase += "energy.png";
                break;
            case "INDUSTRY":
                urlBase += "industry.png";
                break;
            case "ERGO":
                urlBase += "ergo.png";
                break;
            case "MECHANICS":
                urlBase += "mechanics.png";
                break;
            default:
                urlBase += "empty.png";
                break;
        }

        return urlBase;
    }

    /**
     * Get the right position on the right anchor pane on the interface
     * corresponding to a position
     * 
     * @param position
     * @return
     */
    private AnchorPane getAnchorPaneFromPositionNumber(int position) {
        AnchorPane anchorPane = null;
        switch (position) {
            case 0:
                anchorPane = one;
                break;
            case 1:
                anchorPane = two;
                break;
            case 2:
                anchorPane = three;
                break;
            case 3:
                anchorPane = four;
                break;
            case 4:
                anchorPane = five;
                break;
            case 5:
                anchorPane = six;
                break;
            case 6:
                anchorPane = seven;
                break;
            case 7:
                anchorPane = eight;
                break;
            case 8:
                anchorPane = nine;
                break;
            case 9:
                anchorPane = ten;
                break;
            case 10:
                anchorPane = eleven;
                break;
            case 11:
                anchorPane = twelve;
                break;
            default:
                break;
        }

        return anchorPane;
    }

    /**
     * Gets all the data from models and updates information displayed on the
     * screen.
     * This method is called each time a player makes an action.
     */
    private void updateData() {
        showPlayerData();
        showPlayerCards();
        showAvailableDiplomas();
        checkDiplomasAvailable();
        displayEndGamePanel();
        disableButtons();
        createPlayers(game.getPlayers());
    }
}
