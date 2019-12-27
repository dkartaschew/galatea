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
package com.github.dkartaschew.galatea.regex;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Regex Tester, and with results.
 */
public class RegexTester extends Composite {

	/**
	 * User regex input field
	 */
	private Text txtInput;
	/**
	 * The text input control decoration.
	 */
	private ControlDecoration decoration;
	/**
	 * The user test field.
	 */
	private StyledText txtForm;

	/**
	 * The matching pattern to use to set the highlightre
	 */
	private Pattern regexPattern;

	/**
	 * The color to use when highlighting the line.
	 */
	private Color highlightColour;

	/**
	 * Create the regex tester composite.
	 * 
	 * @param parent The parent composite
	 * @param style The default style
	 */
	public RegexTester(Composite parent, int style) {
		super(parent, style);
		this.highlightColour = parent.getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION);

		setLayout(new GridLayout(2, false));

		Label lblRegex = new Label(this, SWT.NONE);
		lblRegex.setText("Pattern to test:");
		lblRegex.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).create());

		txtInput = new Text(this, SWT.BORDER);
		txtInput.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).indent(8, 0).create());
		txtInput.setMessage("Please enter regex");

		decoration = new ControlDecoration(txtInput, SWT.TOP | SWT.LEFT);
		FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault()
				.getFieldDecoration(FieldDecorationRegistry.DEC_WARNING);
		if (fieldDecoration != null) {
			decoration.setImage(fieldDecoration.getImage());
		}

		Label lblForm = new Label(this, SWT.NONE);
		lblForm.setText("Test Input:");
		lblForm.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(2, 1).create());

		txtForm = new StyledText(this, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		txtForm.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).span(2, 1).create());
		txtForm.setAlwaysShowScrollBars(true);

		// Setup input listeners.
		txtInput.addListener(SWT.Modify, e -> updateInput());
		txtForm.addListener(SWT.Modify, e -> updateInput());
	}

	/**
	 * Update the field information
	 */
	private void updateInput() {
		/*
		 * Update the pattern.
		 */
		String pattern = txtInput.getText();
		if (pattern.isEmpty()) {
			regexPattern = null;
			decoration.show();
			decoration.setDescriptionText("Please enter a regex pattern to test");
		} else {
			try {
				regexPattern = Pattern.compile(pattern);
				decoration.hide();
			} catch (PatternSyntaxException e) {
				regexPattern = null;
				decoration.show();
				decoration.setDescriptionText(e.getMessage());
			}
		}
		/*
		 * Update the styled text widget.
		 */
		if (regexPattern == null) {
			// Clear the lines
			txtForm.setLineBackground(0, txtForm.getLineCount(), null);
		} else {
			String[] lines = txtForm.getText().split("\\r?\\n");
			if (lines.length == 0) {
				txtForm.setLineBackground(0, txtForm.getLineCount(), null);
			}
			for (int i = 0; i < lines.length; i++) {
				String l = lines[i];
				if (!l.isEmpty() && regexPattern.matcher(l).matches()) {
					txtForm.setLineBackground(i, 1, highlightColour);
				} else {
					txtForm.setLineBackground(i, 1, null);
				}
			}
		}
		/*
		 * Request a redraw/update.
		 */
		redraw();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	/**
	 * Set the input string.
	 * 
	 * @param input The initial string
	 */
	public void setInputRegex(String input) {
		checkWidget();
		txtInput.setText(input != null ? input : "");
		Event event = new Event();
		event.item = txtInput;
		event.text = input != null ? input : "";
		txtInput.notifyListeners(SWT.Modify, event);
	}

	/**
	 * Get the text currently in the
	 * 
	 * @return The text currently in the input field.
	 */
	public String getInputRegex() {
		checkWidget();
		return txtInput.getText();
	}

	/**
	 * Set the input string.
	 * 
	 * @param input The initial string
	 */
	public void setTestFormValue(String input) {
		checkWidget();
		txtForm.setText(input != null ? input : "");
		Event event = new Event();
		event.item = txtForm;
		event.text = input != null ? input : "";
		txtForm.notifyListeners(SWT.Modify, event);
	}

	/**
	 * Set the highlight colour for mactched lines
	 * 
	 * @param color The colour to set.
	 */
	public void setHighlightColour(Color color) {
		checkWidget();
		if (color != null && !color.isDisposed()) {
			this.highlightColour = color;
		}
	}

}
