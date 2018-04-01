package by.ititon.orm.annotation;


/**
 * Defines the set of cascadable operations that are propagated
 * to the associated entity.
 * The value <code>cascade=ALL</code> is equivalent to
 * <code>cascade={SAVE, UPDATE, REMOVE, ALL}</code>.
 */
public enum CascadeType {

    SAVE, UPDATE, REMOVE, ALL



}
