package kanagawa.models.enums;

/**
 * Skills represent the discipline that each UV belongs to. Skill points can be
 * obtained via the personal work part of cards and are necessary to keep a UV
 * or to obtain a diploma.
 */
public enum Skill {
    MATH(0),
    INFO(1),
    ENERGY(2),
    ERGO(3),
    MECHANICS(4),
    INDUSTRY(5),
    LANGUAGE(6),
    MANAGEMENT(7);

    /**
     * Numerical value of the enum constant
     */
    private final int skill;

    /**
     * Number of existing Skill categories
     */
    public static final int length = 8;

    private Skill(int skill) {
        this.skill = skill;
    }

    /**
     * Returns the numerical value of the enum constant
     */
    public int toInt() {
        return this.skill;
    }
}
