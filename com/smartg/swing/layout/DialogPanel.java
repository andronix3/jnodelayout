package com.smartg.swing.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.function.Predicate;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
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

	private final JButton closeButton = new JButton("Close");
	private final JButton okayButton = new JButton("Okay");

	private final GridPanel buttonBox = new GridPanel(10);
	private final GridPanel systemButtonBox = new GridPanel(20);
	private final GridPanel userButtonBox = new GridPanel(20);

	private boolean canceled = true;
	private boolean closeDialogOnOkayClick = true;

	private String title;
	private Predicate<DialogPanel> closePredicate;

	private int closeOperation = JFrame.HIDE_ON_CLOSE;

	public DialogPanel() {
		this(10);
	}

	public DialogPanel(int gridWidth) {
		super(gridWidth);

		buttonBox.setVerticalAlignment(NodeAlignment.BOTTOM);
		buttonBox.setHorizontalAlignment(NodeAlignment.STRETCHED);

		userButtonBox.setHorizontalAlignment(NodeAlignment.LEFT);
		userButtonBox.setVerticalAlignment(NodeAlignment.CENTER);

		systemButtonBox.setHorizontalAlignment(NodeAlignment.RIGHT);
		systemButtonBox.setVerticalAlignment(NodeAlignment.BOTTOM);

		buttonBox.add(userButtonBox, 6);
		buttonBox.add(systemButtonBox, 4);

		systemButtonBox.add(closeButton, 1);
		systemButtonBox.add(okayButton, 1);

		buttonBox.setBorder(new EmptyBorder(0, 10, 20, 0));

		closeButton.addActionListener(e -> {
			if (closePredicate == null || closePredicate.test(this)) {
				closeDialog();
			}
		});
		okayButton.addActionListener(e -> {
			canceled = false;
			if (closeDialogOnOkayClick) {
				closeDialog();
			}
		});
	}

	private void closeDialog() {
		Window ancestor = SwingUtilities.getWindowAncestor(this);
		if (ancestor != null) {
			switch (closeOperation) {
			case JFrame.EXIT_ON_CLOSE:
				System.exit(0);
				break;
			case JFrame.DISPOSE_ON_CLOSE:
				ancestor.dispose();
				break;
			case JFrame.HIDE_ON_CLOSE:
				ancestor.setVisible(false);
				break;
			}
		}
	}

	public int getCloseOperation() {
		return closeOperation;
	}

	public void setCloseOperation(int closeOperation) {
		this.closeOperation = closeOperation;
	}

	public Predicate<DialogPanel> getClosePredicate() {
		return closePredicate;
	}

	public void setClosePredicate(Predicate<DialogPanel> closePredicate) {
		this.closePredicate = closePredicate;
	}

	public final JComponent addUserButton(JComponent b) {
		userButtonBox.add(b, 1);
		return b;
	}

	protected JComponent getButtonBox() {
		return buttonBox;
	}

	public JComponent removeUserButton(JComponent b) {
		userButtonBox.remove(b);
		return b;
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

	public void showInDialog(Component c) {
		showInDialog(SwingUtilities.getWindowAncestor(c));
	}

	public void showInDialog(Window owner) {
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
		if (getOkayButton().isDefaultCapable()) {
			rootPane.setDefaultButton(getOkayButton());
		}

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
		dialog.setVisible(true);
	}

	public void showInFrame() {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(closeOperation);

		if (title != null) {
			frame.setTitle(title);
		}
		final Container contentPane = frame.getContentPane();
		final LayoutNode.VerticalNode root = new LayoutNode.VerticalNode("root");
		root.setHorizontalAlignment(NodeAlignment.STRETCHED);
		root.setVerticalAlignment(NodeAlignment.CENTER);
		contentPane.setLayout(new JNodeLayout(contentPane, root));
		contentPane.add(this, new NodeConstraints("root"));
		contentPane.add(buttonBox, new NodeConstraints("root"));

		JRootPane rootPane = frame.getRootPane();
		if (getOkayButton().isDefaultCapable()) {
			rootPane.setDefaultButton(getOkayButton());
		}

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

		frame.pack();
		frame.setVisible(true);
	}

	public void showInPanel(JPanel contentPane) {
		final LayoutNode.VerticalNode root = new LayoutNode.VerticalNode("root");
		root.setHorizontalAlignment(NodeAlignment.STRETCHED);
		root.setVerticalAlignment(NodeAlignment.CENTER);
		contentPane.setLayout(new JNodeLayout(contentPane, root));
		contentPane.add(this, new NodeConstraints("root"));
		contentPane.add(buttonBox, new NodeConstraints("root"));
	}

	public void showInPopup(JComponent c) {
		JPanel contentPane = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			public Dimension getPreferredSize() {
				Dimension ps = super.getPreferredSize();
				ps.height += 5;
				return ps;
			}
		};
		final LayoutNode.VerticalNode root = new LayoutNode.VerticalNode("root");
		root.setHorizontalAlignment(NodeAlignment.STRETCHED);
		root.setVerticalAlignment(NodeAlignment.CENTER);
		contentPane.setLayout(new JNodeLayout(contentPane, root));
		contentPane.add(this, new NodeConstraints("root"));
		contentPane.add(buttonBox, new NodeConstraints("root"));

		JPopupMenu menu = new JPopupMenu();
		menu.add(contentPane);

		menu.show(c, 0, -contentPane.getPreferredSize().height);
	}
}
