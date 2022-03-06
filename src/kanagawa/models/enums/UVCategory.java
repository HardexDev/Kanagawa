package kanagawa.models.enums;

/**
 * UVCategory represents the different ECTS categories of UVs. Points in each
 * category can be obtained via UVs and are necessary to obtain diplomas.
 */
public enum UVCategory {
    CS(0),
    TM(1),
    EC(2),
    TSS(3);

    /**
     * Numerical value of the enum constant
     */
    private final int uvCategory;

    /**
     * Number of existing UV categories
     */
    public static final int length = 4;

    private UVCategory(int uvCategory) {
        this.uvCategory = uvCategory;
    }

    /**
     * Returns the numerical value of the enum constant
     */
    public int toInt() {
        return this.uvCategory;
    }
}