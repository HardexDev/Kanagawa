package kanagawa.models;

import kanagawa.models.enums.Skill;
import kanagawa.models.enums.UVCategory;
import kanagawa.utilities.InvalidGameObjectException;

import java.util.Arrays;

/**
 * Class implementing diplomas, each diploma can be obtained if the user has
 * enough UVs and Skills to get it.
 */
public class Diploma {

    /**
     * Array containing the number of UVs necessary in each {@code UVCategory} to
     * get the diploma.
     */
    private int[] UVArray;

    /**
     * Array containing the number of points necessay in each {@code Skill} to get
     * the diploma.
     */
    private int[] skillArray;

    /**
     * Number of ECTS credits available in this diploma.
     */
    private int credits;

    transient private DiplomaGroup group;

    /**
     * Checks if the Object has been parsed and initialized correctly
     * 
     * @param parent Collection in which the object is stored
     * @throws InvalidGameObjectException
     */
    public void checkInitialization(DiplomaGroup parent) throws InvalidGameObjectException {
        if (UVArray == null || UVArray.length != UVCategory.length || skillArray == null
                || skillArray.length != Skill.length || credits == 0) {
            throw new InvalidGameObjectException(this, parent);
        }
        this.group = parent;
    }

    /**
     * Array containing the number of UVs necessary in each {@code UVCategory} to
     * get the diploma.
     */
    public int[] getUVArray() {
        return UVArray;
    }

    /**
     * Array containing the number of points necessay in each {@code Skill} to get
     * the diploma.
     */
    public int[] getSkillArray() {
        return skillArray;
    }

    /**
     * Number of ECTS credits available in this diploma.
     */
    public int getCredit() {
        return credits;
    }

    public DiplomaGroup getGroup() {
        return group;
    }

    @Override
    public String toString() {
        return "Diploma{" +
                "UVArray=" + Arrays.toString(UVArray) +
                ", skillArray=" + Arrays.toString(skillArray) +
                ", credit=" + credits +
                ", group=" + group.getGroupeName() +
                '}';
    }
}
