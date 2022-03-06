package kanagawa.models;

import java.util.*;

/**
 * Class handling the rounds of the game. A new instance of {@code Round} is
 * created everytime all columns of the game board have been taken. The
 * {@code Round} class is responsible for managing the game board where the
 * cards are and the turn of each player.
 */
public class Round {

    /**
     * Player currently playing its turn.
     */
    private Player currentPlayer;

    /**
     * Array of {@code ArrayList<Card>} representing each column of the game board.
     */
    private ArrayList<Card>[] gameBoard;

    private ArrayList<Player> players;

    /**
     * Constructor of {@code Round} class.
     */
    Round() {
        gameBoard = new ArrayList[4];
        for (int i = 0; i < 4; i++) {
            gameBoard[i] = new ArrayList<Card>();
        }
    }

    /**
     * Initializes the game board for the adequate number of players.
     */
    public void initBoardWithPlayersCount() {
        for (int i = players.size(); i < gameBoard.length; i++) {
            // We initalize one column per player
            gameBoard[i] = null;

        }
    }

    /**
     * Deals 1 card to each column of the board
     * 
     * @param cards a pointer on the array of cards to add
     */
    public void addCards(Card[] cards) {
        int index = -1;
        for (int i = 0; i < cards.length; i++) {
            for (int j = index + 1; j < gameBoard.length; j++) {
                if (gameBoard[j] != null) {
                    gameBoard[j].add(cards[i]);
                    index = j;
                    break;
                }
            }
        }
    }

    /**
     * Removes the specified column of cards from the board and returns it
     * 
     * @return an {@code ArrayList<Card>}
     */
    public ArrayList<Card> removeColumn(int index) {
        ArrayList<Card> temp = null;
        if (index >= 0) {
            temp = this.gameBoard[index];
            this.gameBoard[index] = null;
        } else {
            System.err.println("Round.removeColumn() : Invalid index value.");
            System.exit(-1);
        }
        return temp;
    }

    /**
     * Computes the number of columns of cards that remain on the board
     * 
     * @return an {@code int}
     */
    public int getRemainingColumns() {
        int j = 0;
        for (int i = 0; i < 4; i++) {
            if (gameBoard[i] != null)
                j++;
        }
        return j;
    }

    /**
     * Sets which player is currently playing its turn.
     */
    public void setCurrentPlayer(Player player) {
        currentPlayer = player;
    }

    /**
     * Player currently playing its turn.
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void setRemainingPlayers(ArrayList<Player> players) {
        this.players = new ArrayList<>(players);
    }

    public ArrayList<Card>[] getGameBoard() {
        return gameBoard;
    }

    /**
     * Sets the next player as the current player
     *
     * @return ArrayList
     */
    public void nextPlayer() {
        currentPlayer = players.get((players.indexOf(currentPlayer) + 1) % players.size());
        currentPlayer.setPlaying(true);
    }
}
