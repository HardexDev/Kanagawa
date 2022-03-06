package kanagawa.models;

import kanagawa.utilities.InvalidGameObjectException;

/**
 * Class implementing the cards of the game. Each cards is composed of 2 parts,
 * the UV and Personal Work.
 */
public class Card {

    private PersonalWork personalWork;

    private UV uv;

    /**
     * Indicates that this card is the first card, the player will have in his hand.
     */
    private boolean isStarterCard;

    /**
     * Checks if the Object has been parsed and initialized correctly
     * 
     * @throws InvalidGameObjectException
     */
    public void checkInitialization() throws InvalidGameObjectException {
        if (personalWork != null && uv != null) {
            personalWork.checkInitialization(this);
            uv.checkInitialization(this);
        } else {
            throw new InvalidGameObjectException(this);
        }
    }

    public PersonalWork getPersonalWork() {
        return personalWork;
    }

    public UV getUv() {
        return uv;
    }

    /**
     * Indicates that this card is the first card, the player will have in his hand.
     */
    public boolean isStarterCard() {
        return isStarterCard;
    }

    @Override
    public String toString() {
        return this.getUv().getCode();
    }
}
