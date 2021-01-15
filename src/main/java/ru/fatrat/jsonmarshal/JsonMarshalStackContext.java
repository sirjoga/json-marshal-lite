package ru.fatrat.jsonmarshal;

import javax.annotation.Nonnull;

/**
 * Marshal/unmarshal trace info receiver.
 */
public interface JsonMarshalStackContext {

    /**
     * Entering array element.
     *
     * @param id array index
     */
    void pushArrayElementId(int id);

    /**
     * Entering object element.
     *
     * @param id element key (name)
     */
    void pushObjectFieldElementId(@Nonnull String id);

    /**
     * Exiting array or object element.
     */
    void popElementId();
}
