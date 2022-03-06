package kanagawa.models;

import kanagawa.models.enums.Skill;
import kanagawa.models.enums.UVCategory;
import kanagawa.utilities.InvalidGameObjectException;

/**
 * Class implementing the UV part of the card. The UV contains a
 * {@code uvCategory} and a {@code Skill}.
 */
public class UV {

    private String code;

    private UVCategory uvCategory;

    private Skill skill;

    /**
     * Checks if the Object has been parsed and initialized correctly
     * 
     * @param parent Collection in which the object is stored
     * @throws InvalidGameObjectException
     */
    public void checkInitialization(Card parent) throws InvalidGameObjectException {
        if (code == null || uvCategory == null || skill == null) {
            throw new InvalidGameObjectException(parent);
        }
    }

    @Override
    public String toString() {
        return "UV{" +
                "code='" + code + '\'' +
                ", uvCategory=" + uvCategory +
                ", skill=" + skill +
                '}';
    }

    public String getCode() {
        return code;
    }

    public UVCategory getUvCategory() {
        return uvCategory;
    }

    public Skill getSkill() {
        return skill;
    }
}
