package kanagawa.utilities;

/**
 * Class extending the {@code Exception} class, used to throw an exception when
 * the parsing of an object failed
 */
public class InvalidGameObjectException extends Exception {

    /**
     * Reference on the a parent {@code Object} or {@code Collection} containing the
     * object responsible for the Exception.
     */
    private Object parent;

    /**
     * {@code Object} responsible for the Exception.
     */
    private Object object;

    /**
     * Creates an exception if the parsing of an {@code Object} is incorrect.
     * 
     * @param object {@code Object} responsible for the Exception
     */
    public InvalidGameObjectException(Object object) {
        this.object = object;
        this.parent = null;
    }

    /**
     * Creates an exception if the parsing of an {@code Object} is incorrect.
     * 
     * @param object {@code Object} responsible for the Exception
     * @param parent Reference on the a parent {@code Object} or {@code Collection}
     *               containing {@code object}
     */
    public InvalidGameObjectException(Object object, Object parent) {
        this.object = object;
        this.parent = parent;
    }

    @Override
    public void printStackTrace() {
        if (this.parent == null) {
            System.err.println(object.toString() + " is invalid");
        } else {
            System.err.println(object.toString() + " in " + parent.toString() + " is invalid.");
        }
    }

    /**
     * Reference on the a parent {@code Object} or {@code Collection} containing the
     * object responsible for the Exception.
     */
    public Object getParent() {
        return parent;
    }

    /**
     * {@code Object} responsible for the Exception.
     */
    public Object getObject() {
        return object;
    }
}
