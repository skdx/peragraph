package com.peralex.utilities.ui.titlebar;

/**
 *
 * FIXME (Noel) add cTitleBar parameters to the listener methods
 * 
 * @author  Jaco
 */
public interface ITitleBarListener
{
	void closeEvent();
	void lockEvent();
	void unlockEvent();
	void minimizeEvent();
	void maximizeEvent();
}
