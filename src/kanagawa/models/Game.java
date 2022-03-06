package kanagawa.models;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import kanagawa.utilities.InvalidGameObjectException;

/**
 * Game class. Contains all game objects and the main methods to play the game.
 * The Game class is singleton, in order to have only one instance of the game
 */
public class Game {

    /**
     * Reference on the {@code Round} that is currently being played.
     */
    private Round currentRound;

    /**
     * Number of rounds that have been played since the game started.
     */
    private int roundCount;

    /**
     * List of the players.
     */
    private ArrayList<Player> players;

    /**
     * List of the cards that have not been distributed yet.
     */
    private ArrayList<Card> cardDeck;

    private ArrayList<DiplomaGroup> diplomaGroups;

    private static Game gameInstance = null;

    /**
     * Private constructor for singleton pattern.
     * Initializes game objects.
     */
    private Game() {
        players = new ArrayList<>();
        cardDeck = new ArrayList<>();
        diplomaGroups = new ArrayList<>();

        currentRound = new Round();

        roundCount = 1;
        loadCards();
        loadDiplomas();
    }

    /**
     * Initializes the game instance if null and returns it.
     * 
     * @return the instance of {@code Game}
     */
    public static Game getGameInstance() {
        if (gameInstance == null) {
            gameInstance = new Game();
        }

        return gameInstance;
    }

    /**
     * Reference on the {@code Round} that is currently being played.
     */
    public Round getCurrentRound() {
        return currentRound;
    }

    public ArrayList<DiplomaGroup> getDiplomaGroups() {
        return this.diplomaGroups;
    }

    /**
     * Number of rounds that have been played since the game started.
     */
    public int getRoundCount() {
        return roundCount;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * Load cards data from the json files and checks if they have been parsed
     * correctly
     */
    private void loadCards() {
        File file = new File("./cards.json");
        Gson gson = new GsonBuilder().serializeNulls().create();
        Type cardListType = new TypeToken<ArrayList<Card>>() {
        }.getType();
        JsonReader jsonReader;

        try {
            jsonReader = new JsonReader(new FileReader(file));
            // We read all cards inside the file
            cardDeck = gson.fromJson(jsonReader, cardListType);
            for (Card card : cardDeck) {
                // We check if each card has been initialized correctly
                card.checkInitialization();
            }
            jsonReader.close();

        } catch (InvalidGameObjectException e) {
            System.out.println("Game.loadCards() : Failed to load cards.");
            if (e.getObject() != null) {
                System.err.println("Index in cardDeck : " + cardDeck.indexOf(e.getObject()));
            }
            System.exit(-1);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Game.loadCards() : Failed to load cards.");
            System.exit(-1);
        }
    }

    /**
     * Load diplomas data from the json files and checks if they have been parsed
     * correctly
     */
    private void loadDiplomas() {
        File file = new File("./diplomas.json");
        Gson gson = new GsonBuilder().serializeNulls().create();
        Type diplomaGroupListType = new TypeToken<ArrayList<DiplomaGroup>>() {
        }.getType();
        JsonReader jsonReader;

        try {
            jsonReader = new JsonReader(new FileReader(file));
            // We read all diplomas inside the file
            diplomaGroups = gson.fromJson(jsonReader, diplomaGroupListType);
            for (DiplomaGroup diplomaGroup : diplomaGroups) {
                // We check if each diploma has been initialized correctly
                diplomaGroup.checkInitialization();
            }
            jsonReader.close();

        } catch (InvalidGameObjectException e) {
            Object object = e.getObject();
            Object parent = e.getParent();
            System.out.println("Game.loadDiplomas() : Failed to load diplomas.");
            if (object != null)
                if (parent == null) {
                    System.err.println("Index in diplomaGroups : " + diplomaGroups.indexOf(object));
                } else {
                    int index = diplomaGroups.indexOf(parent);
                    System.err.println("Index in diplomaGroups : " + index);
                    System.err.println(
                            "Index of diploma in diplomas : " + diplomaGroups.get(index).getDiplomas().indexOf(object));
                }
            System.exit(-1);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Game.loadCards() : Failed to load cards.");
            System.exit(-1);
        }
    }

    /**
     * Deals new cards on the board for the current round and passes them to the
     * {@code Round} class
     */
    public void distributeCards() {

        Card[] cardsToDeal = new Card[currentRound.getRemainingColumns()];

        for (int i = 0; i < cardsToDeal.length; i++) {
            if (cardDeck.isEmpty()) {
                loadCards();
                shuffleCards();
            }
            cardsToDeal[i] = cardDeck.remove(0);
        }

        currentRound.addCards(cardsToDeal);
    }

    /**
     * Allows to end current round and to start a new one
     */
    public void nextRound() {
        currentRound = new Round();
        roundCount++;
        currentRound.setRemainingPlayers(players);
        for (Player player : players) {
            player.setFirstPlayer(false);
            if (player.getInventory().hasProfessor()) {
                currentRound.setCurrentPlayer(player);
                currentRound.getCurrentPlayer().setFirstPlayer(true);
            }
        }
        currentRound.getCurrentPlayer().setPlaying(true);
    }

    /**
     * Adds all the players in the list
     * 
     * @param player1 first {@code Player}
     * @param player2 second {@code Player}
     * @param player3 third {@code Player}
     * @param player4 fourth {@code Player}
     */
    public void addPlayers(Player player1, Player player2, Player player3, Player player4) {
        players.add(player1);
        players.add(player2);
        if (player3 != null)
            players.add(player3);

        if (player4 != null)
            players.add(player4);
    }

    /**
     * Chooses a random first player from the players' list
     * This player will be the first one to play on the first round
     */
    public void chooseRandomFirstPlayer() {
        Random rand = new Random();
        int i = rand.nextInt(players.size());
        Player player = players.get(i);
        player.setPlaying(true);
        player.setFirstPlayer(true);
        player.getInventory().setHasProfessor(true);

        currentRound.setCurrentPlayer(players.get(i));
    }

    /**
     * Picks one random card from four starter cards in the deck.
     * Each starter card is randomly assigned to each player
     */
    public void randomFirstCardForPlayers() {
        ArrayList<Card> starterCards = new ArrayList<>();
        for (Card card : cardDeck) {
            if (card.isStarterCard()) {
                starterCards.add(card);
            }
        }

        for (Player player : players) {
            Random rand = new Random();
            Card randomCard = starterCards.remove(rand.nextInt(starterCards.size()));
            cardDeck.remove(randomCard);

            player.addToPersonalWork(randomCard);
            player.addToUv(randomCard);
        }
    }

    /**
     * Allows to shuffle the values of the card deck
     */
    public void shuffleCards() {
        Collections.shuffle(cardDeck);
    }

    /**
     * Checks if the game if over :
     * either 11 rounds have been played
     * or one player has 11 UVs
     *
     * @return a {@code boolean}
     */
    public boolean checkGameIsOver() {
        boolean gameOver = false;

        if (roundCount >= 15) {
            gameOver = true;
        } else {
            for (Player player : players) {
                int uvCount = 0;
                for (UV uv : player.getInventory().getUvPossessed()) {
                    uvCount++;
                }

                if (uvCount >= 11) {
                    gameOver = true;
                    break;
                }
            }
        }
        return gameOver;
    }
}