package kanagawa.models;

import java.util.ArrayList;

import kanagawa.utilities.InvalidGameObjectException;

/**
 * Class implementing the diploma groups. Each {@code DiplomaGroup} contains 3
 * diplomas, each player can only obtain 1 diploma from each group.
 * Once a diploma has been attributed to a player, it is removed from its group.
 */
public class DiplomaGroup {

    private String groupeName;

    /**
     * List of the diplomas belonging to this group.
     */
    private ArrayList<Diploma> diplomas;

    /**
     * Checks if the Object has been parsed and initialized correctly
     * 
     * @throws InvalidGameObjectException
     */
    public void checkInitialization() throws InvalidGameObjectException {
        if (groupeName != null && diplomas != null) {
            for (Diploma diploma : diplomas) {
                diploma.checkInitialization(this);
            }
        } else {
            throw new InvalidGameObjectException(this);
        }
    }

    public String getGroupeName() {
        return groupeName;
    }

    /**
     * List of the diplomas belonging to this group.
     */
    public ArrayList<Diploma> getDiplomas() {
        return diplomas;
    }
}
