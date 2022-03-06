package kanagawa.models;

import kanagawa.models.enums.Bonus;
import kanagawa.models.enums.Skill;
import kanagawa.utilities.InvalidGameObjectException;

/**
 * Class implementing the personal work part of the card. The personal work
 * contains a {@code Skill} and a {@code Bonus}.
 */
public class PersonalWork {

    /**
     * Indicates if the skill of this {@code PersonalWork} is being used of not
     */
    private boolean hasPen;

    private Skill skill;

    private Bonus bonus;

    /**
     * Checks if the Object has been parsed and initialized correctly
     * 
     * @param parent Collection in which the object is stored
     * @throws InvalidGameObjectException
     */
    public void checkInitialization(Card parent) throws InvalidGameObjectException {
        if (hasPen != false || skill == null || bonus == null) {
            throw new InvalidGameObjectException(parent);
        }
    }

    @Override
    public String toString() {
        return "PersonalWork{" +
                "hasPen=" + hasPen +
                ", skill=" + skill +
                ", bonus=" + bonus +
                '}';
    }

    /**
     * Indicates if the skill of this {@code PersonalWork} is being used of not
     */
    public boolean hasPen() {
        return hasPen;
    }

    public Skill getSkill() {
        return skill;
    }

    public Bonus getBonus() {
        return bonus;
    }

    /**
     * Sets if the skill of this {@code PersonalWork} is being used of not.
     * 
     * @param hasPen
     */
    public void setHasPen(boolean hasPen) {
        this.hasPen = hasPen;
    }
}
