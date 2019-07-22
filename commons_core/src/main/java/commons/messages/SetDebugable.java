package commons.messages;

import commons.messages.Debugable;

public interface SetDebugable extends Debugable {

    /**
     * Sets the debug status.
     * @param debug should debugMsgs be displayed?
     */
    void setDebug(boolean debug);
}
