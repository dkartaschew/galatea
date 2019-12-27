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
package com.github.dkartaschew.galatea.graph;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;

/**
 * A basic Graph implementation
 */
public class Graph extends Canvas implements PaintListener {

	/**
	 * The number of points to graph
	 */
	private int points = 10;
	/**
	 * The list of values to render
	 */
	private List<Double> values = new LinkedList<>();
	/**
	 * Text to display.
	 */
	private String text;
	/**
	 * The font color.
	 */
	private Color fontColor;

	/**
	 * Constructs a new instance of this class given its parent and a style value describing its behavior and
	 * appearance.
	 * <p>
	 * The style value is either one of the style constants defined in class <code>SWT</code> which is applicable to
	 * instances of this class, or must be built by <em>bitwise OR</em>'ing together (that is, using the
	 * <code>int</code> "|" operator) two or more of those <code>SWT</code> style constants. The class description lists
	 * the style constants that are applicable to the class. Style bits are also inherited from superclasses.
	 * </p>
	 *
	 * @param parent a composite control which will be the parent of the new instance (cannot be null)
	 * @param style the style of control to construct
	 *
	 * @exception IllegalArgumentException
	 *            <ul>
	 *            <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *            </ul>
	 * @exception SWTException
	 *            <ul>
	 *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
	 *            </ul>
	 *
	 * @see SWT
	 * @see Widget#getStyle
	 */
	public Graph(Composite parent, int style) {
		super(parent, style);
		addPaintListener(this);
		this.fontColor = getForeground();
	}

	/**
	 * Set the number of points to render, must be a positive value.
	 * 
	 * @param points The number of points to render.
	 */
	public void setPoints(int points) {
		if (points <= 0) {
			throw new IllegalArgumentException("Points value is invalid");
		}
		this.points = points;
		while (values.size() > points) {
			values.remove(0);
		}
	}

	/**
	 * Add the given value to be rendered. (must be between 0 and 100).
	 * 
	 * @param value The value to be rendered
	 */
	public void addValue(double value) {
		if (value < 0 || value > 100) {
			throw new IllegalArgumentException("Value is outside of supported range.");
		}
		values.add(value);
		while (values.size() > points) {
			values.remove(0);
		}
	}

	/**
	 * Set the text to display.
	 * 
	 * @param text The text to display or NULL to unset.
	 */
	public void setText(String text) {
		checkWidget();
		this.text = text;
	}

	/**
	 * Set the font color
	 * 
	 * @param color The font color
	 */
	public void setFontColor(Color color) {
		checkWidget();
		if (color == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		this.fontColor = color;
	}

	@Override
	public void paintControl(PaintEvent e) {
		GC gc = e.gc;
		gc.setForeground(getForeground());
		gc.setAdvanced(true);
		gc.setAntialias(SWT.ON);
		gc.setAlpha(255);
		gc.setLineWidth(1);
		gc.setLineStyle(SWT.LINE_SOLID);

		Rectangle bounds = getClientArea();
		int width = bounds.width;
		int height = bounds.height;
		
		// Fill background
		gc.fillRectangle(bounds);

		// Draw points
		double pixelsPerHeight = (double) height / 100d;
		double pixelsPerPoint = (double) width / (double) (points - 1);
		if (pixelsPerPoint < 1) {
			pixelsPerPoint = 1;
		}
		double offset = 0;
		int i = 0;
		if (!values.isEmpty()) {
			int[] line = new int[values.size() * 2];
			int[] fill = new int[(values.size() + 2) * 2];
			for (Double v : values) {
				line[i] = (int) offset;
				line[i + 1] = (int) (height - (pixelsPerHeight * v));
				fill[i] = line[i];
				fill[i + 1] = line[i + 1];
				i += 2;
				offset += pixelsPerPoint;
			}
			fill[i] = (int) ((offset > 0) ? offset - pixelsPerPoint : 0);
			fill[i + 1] = height;
			fill[i + 2] = 0;
			fill[i + 3] = height;
			gc.setLineWidth(2);
			gc.drawPolyline(line);

			gc.setAlpha(128);
			gc.setLineWidth(1);
			gc.setBackground(getForeground());
			gc.fillPolygon(fill);
		}
		// Border
		gc.setAlpha(255);
		gc.drawRectangle(0, 0, width - 1, height - 1);

		// Grid
		gc.setLineWidth(1);
		gc.setAlpha(192);
		drawGrid(gc, 4, width, height);

		// Text
		if (text != null) {
			Point sz = gc.stringExtent(text);
			// Only draw if it'll fit
			if (sz.x < width - 4 && sz.y < height - 4) {
				gc.setAlpha(255);
				gc.setForeground(fontColor);
				gc.setFont(getFont());
				// Place on the bottom left corner
				gc.drawString(text, 4, height - 4 - sz.y, true);
			}
		}
	}

	/**
	 * Draw a grid onto the canvas
	 * 
	 * @param gc The GC
	 * @param width The width of the canvas
	 * @param height The height of the canvas
	 */
	private void drawGrid(GC gc, int grid, int width, int height) {
		double spaces = (double) height / (double) grid;
		for (int i = 0; i < grid; i++) {
			gc.drawLine(0, (int) (spaces * i), width, (int) (spaces * i));
		}
		double x = spaces;
		while (x < width) {
			gc.drawLine((int) x, 0, (int) x, height);
			x += spaces;
		}
	}

}
