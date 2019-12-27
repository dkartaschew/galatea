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
package com.github.dkartaschew.galatea.snippets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.github.dkartaschew.galatea.regex.RegexTester;

/**
 * Snippet for RegextTester widget
 */
public class RegexTesterSnippet {

	public static void main(String[] args) {
	    final Display display = new Display();
	    final Shell shell = new Shell(display);
	    shell.setLayout(new FillLayout());
	    
	    RegexTester regex = new RegexTester(shell, SWT.NONE);
	    regex.setTestFormValue("/mnt/file.txt");
	    
	    shell.open();
	    while (!shell.isDisposed()) {
	      if (!display.readAndDispatch()) {
	        display.sleep();
	      }
	    }
	    display.dispose();
	  }
}
