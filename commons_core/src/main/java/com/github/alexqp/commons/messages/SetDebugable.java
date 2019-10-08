package com.github.alexqp.commons.messages;

public interface SetDebugable extends Debugable {

    /**
     * Sets the debug status.
     * @param debug should debugMsgs be displayed?
     */
    void setDebug(boolean debug);
}
