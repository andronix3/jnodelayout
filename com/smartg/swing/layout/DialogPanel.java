package com.smartg.swing.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

/**
 * GridPanel with "Okay" and "Cancel" or "Close" buttons and possibility to add
 * own buttons on the bottom line. Moreover it taking just one line of code to
 * show GridPanel in a Dialog.
 *
 * @author User
 *
 */
public class DialogPanel extends GridPanel {

	private static final long serialVersionUID = -6770092063928679808L;

	private JDialog currentDialog;
	private final JButton closeButton = new JButton("Close");
	private final JButton okayButton = new JButton("Okay");
	private final Box buttonBox = Box.createHorizontalBox();
	private final Box userButtonBox = Box.createHorizontalBox();
	private boolean canceled = true;
	private boolean closeDialogOnOkayClick = true;
	// After each user Button we add a small strut, strutMap is used to remove
	// proper strut, if we remove Button
	private final Map<AbstractButton, Component> strutMap = new HashMap<>();
	private String title;
	private Predicate<DialogPanel> closePredicate;

	public DialogPanel() {
		this(10);
	}

	public DialogPanel(int gridWidth) {
		super(gridWidth);
		buttonBox.add(userButtonBox);
		buttonBox.add(Box.createHorizontalGlue());
		buttonBox.add(closeButton);
		buttonBox.add(Box.createHorizontalStrut(10));
		buttonBox.add(okayButton);
		buttonBox.add(Box.createHorizontalStrut(20));

		buttonBox.setBorder(new EmptyBorder(0, 10, 20, 0));

		closeButton.addActionListener(e -> {
			if (currentDialog != null && closePredicate.test(this)) {
				currentDialog.setVisible(false);
			}
		});
		okayButton.addActionListener(e -> {
			canceled = false;
			if (closeDialogOnOkayClick && currentDialog != null) {
				currentDialog.setVisible(false);
			}
		});
	}

	public Predicate<DialogPanel> getClosePredicate() {
		return closePredicate;
	}

	public void setClosePredicate(Predicate<DialogPanel> closePredicate) {
		this.closePredicate = closePredicate;
	}

	public final void addUserButton(AbstractButton b) {
		userButtonBox.add(b);
		Component strut = Box.createHorizontalStrut(10);
		userButtonBox.add(strut);
		strutMap.put(b, strut);
	}

	protected Box getButtonBox() {
		return buttonBox;
	}

	public void removeUserButton(AbstractButton b) {
		userButtonBox.remove(b);
		Component strut = strutMap.remove(b);
		if (strut != null) {
			userButtonBox.remove(strut);
		}
	}

	public final JButton getCloseButton() {
		return closeButton;
	}

	public final JButton getOkayButton() {
		return okayButton;
	}

	public String getTitle() {
		return title;
	}

	public boolean isCanceled() {
		return canceled;
	}

	public boolean isCloseDialogOnOkayClick() {
		return closeDialogOnOkayClick;
	}

	public void setCloseDialogOnOkayClick(boolean closeDialogOnOkayClick) {
		this.closeDialogOnOkayClick = closeDialogOnOkayClick;
	}

	public final void setTitle(String title) {
		this.title = title;
	}

	public void showPanelInDialog(Component c) {
		showPanelInDialog(SwingUtilities.getWindowAncestor(c));
	}

	public void showPanelInDialog(Window owner) {
		JDialog dialog = new JDialog(owner, Dialog.ModalityType.APPLICATION_MODAL);
		if (title != null) {
			dialog.setTitle(title);
		}
		final Container contentPane = dialog.getContentPane();
		final LayoutNode.VerticalNode root = new LayoutNode.VerticalNode("root");
		root.setHorizontalAlignment(NodeAlignment.STRETCHED);
		root.setVerticalAlignment(NodeAlignment.CENTER);
		contentPane.setLayout(new JNodeLayout(contentPane, root));
		contentPane.add(this, new NodeConstraints("root"));
		contentPane.add(buttonBox, new NodeConstraints("root"));

		JRootPane rootPane = dialog.getRootPane();
		rootPane.setDefaultButton(getOkayButton());

		InputMap inputMap = rootPane.getInputMap(JButton.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		ActionMap actionMap = rootPane.getActionMap();

		KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		String name = "CloseOnEscape";
		inputMap.put(ks, name);
		actionMap.put(name, new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				closeButton.doClick();
			}
		});

		dialog.pack();
		dialog.setLocationRelativeTo(owner);
		this.currentDialog = dialog;
		dialog.setVisible(true);
	}

	public void showInPanel(JPanel contentPane) {
		final LayoutNode.VerticalNode root = new LayoutNode.VerticalNode("root");
		root.setHorizontalAlignment(NodeAlignment.STRETCHED);
		root.setVerticalAlignment(NodeAlignment.CENTER);
		contentPane.setLayout(new JNodeLayout(contentPane, root));
		contentPane.add(this, new NodeConstraints("root"));
		contentPane.add(buttonBox, new NodeConstraints("root"));
	}

	/**
	 * @return the currentDialog
	 */
	public JDialog getCurrentDialog() {
		return currentDialog;
	}
}
