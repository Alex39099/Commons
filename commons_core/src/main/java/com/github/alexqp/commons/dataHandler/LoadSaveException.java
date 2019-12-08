package com.github.alexqp.commons.dataHandler;

/**
 * Thrown to indicate that a method has problems with loading/saving data to file.
 */
@SuppressWarnings("WeakerAccess")
public class LoadSaveException extends Exception {

    /**
     * Constructs a LoadSaveException with the specified message.
     * @param msg the message
     */
    public LoadSaveException(String msg) {
        super(msg);
    }

}
