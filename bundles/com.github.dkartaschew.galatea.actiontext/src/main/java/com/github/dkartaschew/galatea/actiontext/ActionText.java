/*-
 * Galatea SWT Widgets
 * Copyright (C) 2017-2019 Darran Kartaschew 
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 */
package com.github.dkartaschew.galatea.actiontext;

import java.util.concurrent.Callable;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.GestureListener;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.SegmentEvent;
import org.eclipse.swt.events.SegmentListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TouchListener;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * A Text field with an action button to execute a task
 */
public class ActionText extends Composite {

	/**
	 * The text field to set.
	 */
	private final Text textField;

	/**
	 * The Image for the button.
	 */
	private final ToolItem actionButton;
	/**
	 * The action to evoke,
	 */
	private final Callable<String> action;

	/**
	 * Create a new text field with associated action
	 * 
	 * @param parent The parent
	 * @param style The base style
	 * @param image The Image for the button
	 * @param action The action to use to set the contents of the text field.
	 */
	public ActionText(Composite parent, int style, Image image, Callable<String> action) {
		super(parent, SWT.BORDER);
		if (image == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		if (action == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		this.action = action;

		setLayout(GridLayoutFactory.fillDefaults().numColumns(2).spacing(0, 0).margins(0, 0).create());

		textField = new Text(this, style | removeFields(style, SWT.BORDER));
		textField.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

		ToolBar toolbar = new ToolBar(this, SWT.NONE);
		toolbar.setLayoutData(GridDataFactory.fillDefaults().create());
		toolbar.setBackground(textField.getBackground());

		actionButton = new ToolItem(toolbar, SWT.FLAT);
		actionButton.setImage(image);

		// Add the button selection listener.
		actionButton.addListener(SWT.Selection, e -> {
			String result = null;
			try {
				result = this.action.call();
			} catch (Throwable t) {
				SWT.error(SWT.ERROR_FAILED_EXEC, t);
			}
			if (result != null) {
				// fire any modify listeners.
				Event event = new Event();
				event.doit = true;
				event.widget = textField;
				event.type = SWT.Modify;
				event.text = result;
				event.display = e.display;
				event.gc = e.gc;
				textField.notifyListeners(SWT.Modify, event);
				// If all listeners are ok, then doit.
				if (event.doit)
					textField.setText(result);
			}
		});
	}

	/**
	 * Remove the given style bits from the original
	 * 
	 * @param original The original style
	 * @param styles The style bits to remove
	 * @return The update style bits
	 */
	private int removeFields(int original, int... styles) {
		int returnedStyle = original;
		for (final int toBeRemoved : styles) {
			if ((returnedStyle & toBeRemoved) != 0) {
				returnedStyle = returnedStyle & ~toBeRemoved;
			}
		}
		return returnedStyle;
	}

	/**
	 * Get the action button
	 * 
	 * @return The action button.
	 */
	protected ToolItem getButton() {
		return actionButton;
	}

	/*
	 * Inherithed methods - You shouldn't have to touch anything below here.
	 */

	/**
	 * Adds the listener to the collection of listeners who will be notified when the receiver's text is modified, by
	 * sending it one of the messages defined in the <code>ModifyListener</code> interface.
	 *
	 * @param listener the listener which should be notified
	 * @exception IllegalArgumentException
	 *            <ul>
	 *            <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *            </ul>
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 * @see ModifyListener
	 * @see #removeModifyListener
	 */
	public void addModifyListener(ModifyListener listener) {
		textField.addModifyListener(listener);
	}

	/**
	 * Adds a segment listener.
	 * <p>
	 * A <code>SegmentEvent</code> is sent whenever text content is being modified or a segment listener is added or
	 * removed. You can customize the appearance of text by indicating certain characters to be inserted at certain text
	 * offsets. This may be used for bidi purposes, e.g. when adjacent segments of right-to-left text should not be
	 * reordered relative to each other. E.g., multiple Java string literals in a right-to-left language should
	 * generally remain in logical order to each other, that is, the way they are stored.
	 * </p>
	 * <p>
	 * <b>Warning</b>: This API is currently only implemented on Windows and GTK. <code>SegmentEvent</code>s won't be
	 * sent on Cocoa.
	 * </p>
	 *
	 * @param listener the listener which should be notified
	 * @exception IllegalArgumentException
	 *            <ul>
	 *            <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *            </ul>
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 * @see SegmentEvent
	 * @see SegmentListener
	 * @see #removeSegmentListener
	 * @since 3.8
	 */
	public void addSegmentListener(SegmentListener listener) {
		textField.addSegmentListener(listener);
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified when the control is selected by the user,
	 * by sending it one of the messages defined in the <code>SelectionListener</code> interface.
	 * <p>
	 * <code>widgetSelected</code> is not called for texts. <code>widgetDefaultSelected</code> is typically called when
	 * ENTER is pressed in a single-line text, or when ENTER is pressed in a search text. If the receiver has the
	 * <code>SWT.SEARCH | SWT.ICON_CANCEL</code> style and the user cancels the search, the event object detail field
	 * contains the value <code>SWT.ICON_CANCEL</code>. Likewise, if the receiver has the <code>SWT.ICON_SEARCH</code>
	 * style and the icon search is selected, the event object detail field contains the value
	 * <code>SWT.ICON_SEARCH</code>.
	 * </p>
	 *
	 * @param listener the listener which should be notified when the control is selected by the user
	 * @exception IllegalArgumentException
	 *            <ul>
	 *            <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *            </ul>
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 * @see SelectionListener
	 * @see #removeSelectionListener
	 * @see SelectionEvent
	 */
	public void addSelectionListener(SelectionListener listener) {
		textField.addSelectionListener(listener);
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified when the receiver's text is verified, by
	 * sending it one of the messages defined in the <code>VerifyListener</code> interface.
	 *
	 * @param listener the listener which should be notified
	 * @exception IllegalArgumentException
	 *            <ul>
	 *            <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *            </ul>
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 * @see VerifyListener
	 * @see #removeVerifyListener
	 */
	public void addVerifyListener(VerifyListener listener) {
		textField.addVerifyListener(listener);
	}

	/**
	 * Appends a string.
	 * <p>
	 * The new text is appended to the text at the end of the widget.
	 * </p>
	 *
	 * @param string the string to be appended
	 * @exception IllegalArgumentException
	 *            <ul>
	 *            <li>ERROR_NULL_ARGUMENT - if the string is null</li>
	 *            </ul>
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 */
	public void append(String string) {
		textField.append(string);
	}

	/**
	 * Clears the selection.
	 *
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 */
	public void clearSelection() {
		textField.clearSelection();
	}

	/**
	 * Copies the selected text.
	 * <p>
	 * The current selection is copied to the clipboard.
	 * </p>
	 *
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 */
	public void copy() {
		textField.copy();
	}

	/**
	 * Cuts the selected text.
	 * <p>
	 * The current selection is first copied to the clipboard and then deleted from the widget.
	 * </p>
	 *
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 */
	public void cut() {
		textField.cut();
	}

	/**
	 * Returns the line number of the caret.
	 * <p>
	 * The line number of the caret is returned.
	 * </p>
	 *
	 * @return the line number
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 */
	public int getCaretLineNumber() {
		return textField.getCaretLineNumber();
	}

	/**
	 * Returns a point describing the location of the caret relative to the receiver.
	 *
	 * @return a point, the location of the caret
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 */
	public Point getCaretLocation() {
		return textField.getCaretLocation();
	}

	/**
	 * Returns the character position of the caret.
	 * <p>
	 * Indexing is zero based.
	 * </p>
	 *
	 * @return the position of the caret
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 */
	public int getCaretPosition() {
		return textField.getCaretPosition();
	}

	/**
	 * Returns the number of characters.
	 *
	 * @return number of characters in the widget
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 */
	public int getCharCount() {
		return textField.getCharCount();
	}

	/**
	 * Returns the double click enabled flag.
	 * <p>
	 * The double click flag enables or disables the default action of the text widget when the user double clicks.
	 * </p>
	 *
	 * @return whether or not double click is enabled
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 */
	public boolean getDoubleClickEnabled() {
		return textField.getDoubleClickEnabled();
	}

	/**
	 * Returns the echo character.
	 * <p>
	 * The echo character is the character that is displayed when the user enters text or the text is changed by the
	 * programmer.
	 * </p>
	 *
	 * @return the echo character
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 * @see #setEchoChar
	 */
	public char getEchoChar() {
		return textField.getEchoChar();
	}

	/**
	 * Returns the editable state.
	 *
	 * @return whether or not the receiver is editable
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 */
	public boolean getEditable() {
		return textField.getEditable();
	}

	/**
	 * Returns the number of lines.
	 *
	 * @return the number of lines in the widget
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 */
	public int getLineCount() {
		return textField.getLineCount();
	}

	/**
	 * Returns the line delimiter.
	 *
	 * @return a string that is the line delimiter
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 * @see #DELIMITER
	 */
	public String getLineDelimiter() {
		return textField.getLineDelimiter();
	}

	/**
	 * Returns the height of a line.
	 *
	 * @return the height of a row of text
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 */
	public int getLineHeight() {
		return textField.getLineHeight();
	}

	/**
	 * Returns the widget message. The message text is displayed as a hint for the user, indicating the purpose of the
	 * field.
	 * <p>
	 * Typically this is used in conjunction with <code>SWT.SEARCH</code>.
	 * </p>
	 *
	 * @return the widget message
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 * @since 3.3
	 */
	public String getMessage() {
		return textField.getMessage();
	}

	/**
	 * Returns a <code>Point</code> whose x coordinate is the character position representing the start of the selected
	 * text, and whose y coordinate is the character position representing the end of the selection. An "empty"
	 * selection is indicated by the x and y coordinates having the same value.
	 * <p>
	 * Indexing is zero based. The range of a selection is from 0..N where N is the number of characters in the widget.
	 * </p>
	 *
	 * @return a point representing the selection start and end
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 */
	public Point getSelection() {
		return textField.getSelection();
	}

	/**
	 * Returns the number of selected characters.
	 *
	 * @return the number of selected characters.
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 */
	public int getSelectionCount() {
		return textField.getSelectionCount();
	}

	/**
	 * Gets the selected text, or an empty string if there is no current selection.
	 *
	 * @return the selected text
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 */
	public String getSelectionText() {
		return textField.getSelectionText();
	}

	/**
	 * Returns the number of tabs.
	 * <p>
	 * Tab stop spacing is specified in terms of the space (' ') character. The width of a single tab stop is the pixel
	 * width of the spaces.
	 * </p>
	 *
	 * @return the number of tab characters
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 */
	public int getTabs() {
		return textField.getTabs();
	}

	/**
	 * Returns the widget text.
	 * <p>
	 * The text for a text widget is the characters in the widget, or an empty string if this has never been set.
	 * </p>
	 *
	 * @return the widget text
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 */
	public String getText() {
		return textField.getText();
	}

	/**
	 * Returns the widget's text as a character array.
	 * <p>
	 * The text for a text widget is the characters in the widget, or a zero-length array if this has never been set.
	 * </p>
	 * <p>
	 * Note: Use this API to prevent the text from being written into a String object whose lifecycle is outside of your
	 * control. This can help protect the text, for example, when the widget is used as a password field. However, the
	 * text can't be protected if an {@link SWT#Segments} or {@link SWT#Verify} listener has been added to the widget.
	 * </p>
	 *
	 * @return a character array that contains the widget's text
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 * @see #setTextChars(char[])
	 * @since 3.7
	 */
	public char[] getTextChars() {
		return textField.getTextChars();
	}

	/**
	 * Returns a range of text. Returns an empty string if the start of the range is greater than the end.
	 * <p>
	 * Indexing is zero based. The range of a selection is from 0..N-1 where N is the number of characters in the
	 * widget.
	 * </p>
	 *
	 * @param start the start of the range
	 * @param end the end of the range
	 * @return the range of text
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 */
	public String getText(int start, int end) {
		return textField.getText(start, end);
	}

	/**
	 * Returns the maximum number of characters that the receiver is capable of holding.
	 * <p>
	 * If this has not been changed by <code>setTextLimit()</code>, it will be the constant <code>Text.LIMIT</code>.
	 * </p>
	 *
	 * @return the text limit
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 * @see #LIMIT
	 */
	public int getTextLimit() {
		return textField.getTextLimit();
	}

	/**
	 * Returns the zero-relative index of the line which is currently at the top of the receiver.
	 * <p>
	 * This index can change when lines are scrolled or new lines are added or removed.
	 * </p>
	 *
	 * @return the index of the top line
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 */
	public int getTopIndex() {
		return textField.getTopIndex();
	}

	/**
	 * Returns the zero-relative index of the line which is currently at the top of the receiver.
	 * <p>
	 * This index can change when lines are scrolled or new lines are added or removed.
	 * </p>
	 *
	 * @return the index of the top line
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 */
	public int getTopPixel() {
		return textField.getTopPixel();
	}

	/**
	 * Inserts a string.
	 * <p>
	 * The old selection is replaced with the new text.
	 * </p>
	 *
	 * @param string the string
	 * @exception IllegalArgumentException
	 *            <ul>
	 *            <li>ERROR_NULL_ARGUMENT - if the string is <code>null</code></li>
	 *            </ul>
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 */
	public void insert(String string) {
		textField.insert(string);
	}

	/**
	 * Pastes text from clipboard.
	 * <p>
	 * The selected text is deleted from the widget and new text inserted from the clipboard.
	 * </p>
	 *
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 */
	public void paste() {
		textField.paste();
	}

	/**
	 * Removes the listener from the collection of listeners who will be notified when the receiver's text is modified.
	 *
	 * @param listener the listener which should no longer be notified
	 * @exception IllegalArgumentException
	 *            <ul>
	 *            <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *            </ul>
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 * @see ModifyListener
	 * @see #addModifyListener
	 */
	public void removeModifyListener(ModifyListener listener) {
		textField.removeModifyListener(listener);
	}

	/**
	 * Removes the listener from the collection of listeners who will be notified when the receiver's text is modified.
	 *
	 * @param listener the listener which should no longer be notified
	 * @exception IllegalArgumentException
	 *            <ul>
	 *            <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *            </ul>
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 * @see SegmentEvent
	 * @see SegmentListener
	 * @see #addSegmentListener
	 * @since 3.8
	 */
	public void removeSegmentListener(SegmentListener listener) {
		textField.removeSegmentListener(listener);
	}

	/**
	 * Removes the listener from the collection of listeners who will be notified when the control is selected by the
	 * user.
	 *
	 * @param listener the listener which should no longer be notified
	 * @exception IllegalArgumentException
	 *            <ul>
	 *            <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *            </ul>
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 * @see SelectionListener
	 * @see #addSelectionListener
	 */
	public void removeSelectionListener(SelectionListener listener) {
		textField.removeSelectionListener(listener);
	}

	/**
	 * Removes the listener from the collection of listeners who will be notified when the control is verified.
	 *
	 * @param listener the listener which should no longer be notified
	 * @exception IllegalArgumentException
	 *            <ul>
	 *            <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *            </ul>
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 * @see VerifyListener
	 * @see #addVerifyListener
	 */
	public void removeVerifyListener(VerifyListener listener) {
		textField.removeVerifyListener(listener);
	}

	/**
	 * Selects all the text in the receiver.
	 *
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 */
	public void selectAll() {
		textField.selectAll();
	}

	/**
	 * Sets the double click enabled flag.
	 * <p>
	 * The double click flag enables or disables the default action of the text widget when the user double clicks.
	 * </p>
	 * <p>
	 * Note: This operation is a hint and is not supported on platforms that do not have this concept.
	 * </p>
	 *
	 * @param doubleClick the new double click flag
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 */
	public void setDoubleClickEnabled(boolean doubleClick) {
		textField.setDoubleClickEnabled(doubleClick);
	}

	/**
	 * Sets the echo character.
	 * <p>
	 * The echo character is the character that is displayed when the user enters text or the text is changed by the
	 * programmer. Setting the echo character to '\0' clears the echo character and redraws the original text. If for
	 * any reason the echo character is invalid, or if the platform does not allow modification of the echo character,
	 * the default echo character for the platform is used.
	 * </p>
	 *
	 * @param echo the new echo character
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 */
	public void setEchoChar(char echo) {
		textField.setEchoChar(echo);
	}

	/**
	 * Sets the editable state.
	 *
	 * @param editable the new editable state
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 */
	public void setEditable(boolean editable) {
		textField.setEditable(editable);
	}

	/**
	 * Sets the font that the receiver will use to paint textual information to the font specified by the argument, or
	 * to the default font for that kind of control if the argument is null.
	 *
	 * @param font the new font (or null)
	 * @exception IllegalArgumentException
	 *            <ul>
	 *            <li>ERROR_INVALID_ARGUMENT - if the argument has been disposed</li>
	 *            </ul>
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 */
	@Override
	public void setFont(Font font) {
		super.setFont(font);
		textField.setFont(font);
	}

	/**
	 * Sets the widget message. The message text is displayed as a hint for the user, indicating the purpose of the
	 * field.
	 * <p>
	 * Typically this is used in conjunction with <code>SWT.SEARCH</code>.
	 * </p>
	 *
	 * @param message the new message
	 * @exception IllegalArgumentException
	 *            <ul>
	 *            <li>ERROR_NULL_ARGUMENT - if the message is null</li>
	 *            </ul>
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 * @since 3.3
	 */
	public void setMessage(String message) {
		textField.setMessage(message);
	}

	/**
	 * Sets the orientation of the receiver, which must be one of the constants <code>SWT.LEFT_TO_RIGHT</code> or
	 * <code>SWT.RIGHT_TO_LEFT</code>.
	 * <p>
	 * Note: This operation is a hint and is not supported on platforms that do not have this concept.
	 * </p>
	 *
	 * @param orientation new orientation style
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 * @since 2.1.2
	 */
	@Override
	public void setOrientation(int orientation) {
		textField.setOrientation(orientation);
	}

	/**
	 * Sets the selection.
	 * <p>
	 * Indexing is zero based. The range of a selection is from 0..N where N is the number of characters in the widget.
	 * </p>
	 * <p>
	 * Text selections are specified in terms of caret positions. In a text widget that contains N characters, there are
	 * N+1 caret positions, ranging from 0..N. This differs from other functions that address character position such as
	 * getText () that use the regular array indexing rules.
	 * </p>
	 *
	 * @param start new caret position
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 */
	public void setSelection(int start) {
		textField.setSelection(start);
	}

	/**
	 * Sets the selection to the range specified by the given start and end indices.
	 * <p>
	 * Indexing is zero based. The range of a selection is from 0..N where N is the number of characters in the widget.
	 * </p>
	 * <p>
	 * Text selections are specified in terms of caret positions. In a text widget that contains N characters, there are
	 * N+1 caret positions, ranging from 0..N. This differs from other functions that address character position such as
	 * getText () that use the usual array indexing rules.
	 * </p>
	 *
	 * @param start the start of the range
	 * @param end the end of the range
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 */
	public void setSelection(int start, int end) {
		textField.setSelection(start, end);
	}

	/**
	 * Sets the selection to the range specified by the given point, where the x coordinate represents the start index
	 * and the y coordinate represents the end index.
	 * <p>
	 * Indexing is zero based. The range of a selection is from 0..N where N is the number of characters in the widget.
	 * </p>
	 * <p>
	 * Text selections are specified in terms of caret positions. In a text widget that contains N characters, there are
	 * N+1 caret positions, ranging from 0..N. This differs from other functions that address character position such as
	 * getText () that use the usual array indexing rules.
	 * </p>
	 *
	 * @param selection the point
	 * @exception IllegalArgumentException
	 *            <ul>
	 *            <li>ERROR_NULL_ARGUMENT - if the point is null</li>
	 *            </ul>
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 */
	public void setSelection(Point selection) {
		textField.setSelection(selection);
	}

	/**
	 * Sets the number of tabs.
	 * <p>
	 * Tab stop spacing is specified in terms of the space (' ') character. The width of a single tab stop is the pixel
	 * width of the spaces.
	 * </p>
	 *
	 * @param tabs the number of tabs
	 *        </ul>
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 */
	public void setTabs(int tabs) {
		textField.setTabs(tabs);
	}

	/**
	 * Sets the contents of the receiver to the given string. If the receiver has style SINGLE and the argument contains
	 * multiple lines of text, the result of this operation is undefined and may vary from platform to platform.
	 * <p>
	 * Note: If control characters like '\n', '\t' etc. are used in the string, then the behavior is platform dependent.
	 * </p>
	 * 
	 * @param string the new text
	 * @exception IllegalArgumentException
	 *            <ul>
	 *            <li>ERROR_NULL_ARGUMENT - if the string is null</li>
	 *            </ul>
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 */
	public void setText(String string) {
		textField.setText(string);
	}

	/**
	 * Sets the contents of the receiver to the characters in the array. If the receiver has style
	 * <code>SWT.SINGLE</code> and the argument contains multiple lines of text then the result of this operation is
	 * undefined and may vary between platforms.
	 * <p>
	 * Note: Use this API to prevent the text from being written into a String object whose lifecycle is outside of your
	 * control. This can help protect the text, for example, when the widget is used as a password field. However, the
	 * text can't be protected if an {@link SWT#Segments} or {@link SWT#Verify} listener has been added to the widget.
	 * </p>
	 *
	 * @param text a character array that contains the new text
	 * @exception IllegalArgumentException
	 *            <ul>
	 *            <li>ERROR_NULL_ARGUMENT - if the array is null</li>
	 *            </ul>
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 * @see #getTextChars()
	 * @since 3.7
	 */
	public void setTextChars(char[] text) {
		textField.setTextChars(text);
	}

	/**
	 * Sets the maximum number of characters that the receiver is capable of holding to be the argument.
	 * <p>
	 * Instead of trying to set the text limit to zero, consider creating a read-only text widget.
	 * </p>
	 * <p>
	 * To reset this value to the default, use <code>setTextLimit(Text.LIMIT)</code>. Specifying a limit value larger
	 * than <code>Text.LIMIT</code> sets the receiver's limit to <code>Text.LIMIT</code>.
	 * </p>
	 *
	 * @param limit new text limit
	 * @exception IllegalArgumentException
	 *            <ul>
	 *            <li>ERROR_CANNOT_BE_ZERO - if the limit is zero</li>
	 *            </ul>
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 * @see #LIMIT
	 */
	public void setTextLimit(int limit) {
		textField.setTextLimit(limit);
	}

	/**
	 * Sets the zero-relative index of the line which is currently at the top of the receiver. This index can change
	 * when lines are scrolled or new lines are added and removed.
	 *
	 * @param index the index of the top item
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 */
	public void setTopIndex(int index) {
		textField.setTopIndex(index);
	}

	/**
	 * Shows the selection.
	 * <p>
	 * If the selection is already showing in the receiver, this method simply returns. Otherwise, lines are scrolled
	 * until the selection is visible.
	 * </p>
	 *
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *            </ul>
	 */
	public void showSelection() {
		textField.showSelection();
	}

	@Override
	public void addListener(int eventType, Listener listener) {
		super.addListener(eventType, listener);
		textField.addListener(eventType, listener);
	}

	@Override
	public void removeListener(int eventType, Listener listener) {
		super.removeListener(eventType, listener);
		textField.removeListener(eventType, listener);
	}

	@Override
	public void addControlListener(ControlListener listener) {
		super.addControlListener(listener);
		textField.addControlListener(listener);
	}

	@Override
	public void addDragDetectListener(DragDetectListener listener) {
		super.addDragDetectListener(listener);
		textField.addDragDetectListener(listener);
	}

	@Override
	public void addFocusListener(FocusListener listener) {
		super.addFocusListener(listener);
		textField.addFocusListener(listener);
	}

	@Override
	public void addGestureListener(GestureListener listener) {
		super.addGestureListener(listener);
		textField.addGestureListener(listener);
	}

	@Override
	public void addHelpListener(HelpListener listener) {
		super.addHelpListener(listener);
		textField.addHelpListener(listener);
	}

	@Override
	public void addKeyListener(KeyListener listener) {
		super.addKeyListener(listener);
		textField.addKeyListener(listener);
	}

	@Override
	public void addMenuDetectListener(MenuDetectListener listener) {
		super.addMenuDetectListener(listener);
		textField.addMenuDetectListener(listener);
	}

	@Override
	public void addMouseListener(MouseListener listener) {
		super.addMouseListener(listener);
		textField.addMouseListener(listener);
	}

	@Override
	public void addMouseTrackListener(MouseTrackListener listener) {
		super.addMouseTrackListener(listener);
		textField.addMouseTrackListener(listener);
	}

	@Override
	public void addMouseMoveListener(MouseMoveListener listener) {
		super.addMouseMoveListener(listener);
		textField.addMouseMoveListener(listener);
	}

	@Override
	public void addMouseWheelListener(MouseWheelListener listener) {
		super.addMouseWheelListener(listener);
		textField.addMouseWheelListener(listener);
	}

	@Override
	public void addTouchListener(TouchListener listener) {
		super.addTouchListener(listener);
		textField.addTouchListener(listener);
	}

	@Override
	public boolean forceFocus() {
		return textField.forceFocus();
	}

	@Override
	public boolean isFocusControl() {
		return textField.isFocusControl();
	}

	@Override
	public void removeControlListener(ControlListener listener) {
		super.removeControlListener(listener);
		textField.removeControlListener(listener);
	}

	@Override
	public void removeFocusListener(FocusListener listener) {
		super.removeFocusListener(listener);
		textField.removeFocusListener(listener);
	}

	@Override
	public void removeGestureListener(GestureListener listener) {
		super.removeGestureListener(listener);
		textField.removeGestureListener(listener);
	}

	@Override
	public void removeHelpListener(HelpListener listener) {
		super.removeHelpListener(listener);
		textField.removeHelpListener(listener);
	}

	@Override
	public void removeKeyListener(KeyListener listener) {
		super.removeKeyListener(listener);
		textField.removeKeyListener(listener);
	}

	@Override
	public void removeMenuDetectListener(MenuDetectListener listener) {
		super.removeMenuDetectListener(listener);
		textField.removeMenuDetectListener(listener);
	}

	@Override
	public void removeMouseTrackListener(MouseTrackListener listener) {
		super.removeMouseTrackListener(listener);
		textField.removeMouseTrackListener(listener);
	}

	@Override
	public void removeMouseListener(MouseListener listener) {
		super.removeMouseListener(listener);
		textField.removeMouseListener(listener);
	}

	@Override
	public void removeMouseMoveListener(MouseMoveListener listener) {
		super.removeMouseMoveListener(listener);
		textField.removeMouseMoveListener(listener);
	}

	@Override
	public void removeMouseWheelListener(MouseWheelListener listener) {
		super.removeMouseWheelListener(listener);
		textField.removeMouseWheelListener(listener);
	}

	@Override
	public void removeTouchListener(TouchListener listener) {
		super.removeTouchListener(listener);
		textField.removeTouchListener(listener);
	}

	@Override
	public void setBackground(Color color) {
		super.setBackground(color);
		textField.setBackground(color);
	}

	@Override
	public void setCapture(boolean capture) {
		super.setCapture(capture);
		textField.setCapture(capture);
	}

	@Override
	public void setCursor(Cursor cursor) {
		super.setCursor(cursor);
		textField.setCursor(cursor);
	}

	@Override
	public void setDragDetect(boolean dragDetect) {
		super.setDragDetect(dragDetect);
		textField.setDragDetect(dragDetect);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if (!textField.isDisposed())
			textField.setFocus();
		if (!actionButton.isDisposed()) {
			actionButton.setEnabled(enabled);
		}
	}

	@Override
	public boolean setFocus() {
		if (!textField.isDisposed())
			return textField.setFocus();
		return false;
	}

	@Override
	public void setForeground(Color color) {
		super.setForeground(color);
		textField.setForeground(color);
	}

	@Override
	public void setMenu(Menu menu) {
		super.setMenu(menu);
		textField.setMenu(menu);
	}

	@Override
	public void setToolTipText(String string) {
		super.setToolTipText(string);
		textField.setToolTipText(string);
	}

	@Override
	public void setTouchEnabled(boolean enabled) {
		super.setTouchEnabled(enabled);
		textField.setTouchEnabled(enabled);
	}

	@Override
	public boolean traverse(int traversal) {
		return textField.traverse(traversal);
	}

	@Override
	public boolean traverse(int traversal, Event event) {
		return textField.traverse(traversal, event);
	}

	@Override
	public boolean traverse(int traversal, KeyEvent event) {
		return textField.traverse(traversal, event);
	}
}
