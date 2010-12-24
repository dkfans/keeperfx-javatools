package keeperfx.launcher;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import keeperfx.configtool.ConfigurationBuffer;
import keeperfx.configtool.KeeperFXConfigTool;
import keeperfx.launcher.items.CommandLineItem;
import keeperfx.launcher.items.FlagSetItem;

public class MainWindow extends JFrame implements ActionListener, DocumentListener,
		CommandLineChanged {
	private static final long serialVersionUID = 1527215476970706506L;
	
	//components
	JPanel centerPanel;
	private JTextField commandLineField;
	private JButton launchButton;
	private JButton editConfigButton;
	private JButton exitButton;

	private final ArrayList<CommandLineItem> items = new ArrayList<CommandLineItem>();
	private final ConfigurationBuffer configBuffer = new KeeperFXCommandLine();

	private boolean debugExecutable;

	public MainWindow() {
		super("KeeperFX Launcher Tool");
		
		setLocationByPlatform(true);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		try {
			InputStream iconStream =
				MainWindow.class.getResourceAsStream("/keeperfx.png");
			
			if (iconStream != null) {
				BufferedImage icon = ImageIO.read(iconStream);
				setIconImage(icon);
			}
		} catch (IOException e) {
			//do nothing
		}
		
		initComponents();
		initCommandLineItems();
		pack();
	}

	private void initCommandLineItems() {
		final String[] flags = {
				"Debug Executable",
				"1 Player",
				"No Intro Movie",
				"No CD Check",
				"No Sound",
				"Alt. Input Mode",
				"Disable AWE32/64",
				"Easter Egg",
				"Quick Map 1",
				"Smooth Video",
				"Column Convert",
				"Light Convert",
		};
		final String[] flagKeys = {
				"!debug", //! means this is an implicit flag not seen on command line
				"1player",
				"nointro",
				"nocd",
				"nosound",
				"altinput",
				"usersfont",
				"alex",
				"q",
				"vidsmooth",
				"columnconvert",
				"lightconvert",
		};
		
		addCommandLineItem(new FlagSetItem(this, "Flags:", 2, flags, flagKeys));
	}

	private void initComponents() {
		setLayout(new BorderLayout());
		
		JPanel northPanel = new JPanel();
		northPanel.setBorder(BorderFactory.createTitledBorder("Command Line:"));
		add(northPanel, BorderLayout.NORTH);
		
		commandLineField = new JTextField();
		commandLineField.setColumns(25);
		commandLineField.addActionListener(this);
		commandLineField.getDocument().addDocumentListener(this);
		northPanel.add(commandLineField);
		
		centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
		add(centerPanel, BorderLayout.CENTER);
		
		initSouthPanel();
	}
	
	private void addCommandLineItem(CommandLineItem item) {
		centerPanel.add(item);
		items.add(item);
	}

	private void initSouthPanel() {
		JPanel southPanel = new JPanel();
		add(southPanel, BorderLayout.SOUTH);
		
		launchButton = new JButton("Launch");
		launchButton.addActionListener(this);
		southPanel.add(launchButton);
		
		editConfigButton = new JButton("Edit Configuration...");
		editConfigButton.addActionListener(this);
		southPanel.add(editConfigButton);
		
		exitButton = new JButton("Exit");
		exitButton.addActionListener(this);
		southPanel.add(exitButton);
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		if (ev.getSource() == editConfigButton) {
			KeeperFXConfigTool.main(null);
		}
		else if (ev.getSource() == exitButton) {
			dispose();
		}
		else if (ev.getSource() == commandLineField) {
			parseCommandLine();
		}
	}
	
	private void parseCommandLine() {
		configBuffer.replace(commandLineField.getText());
		
		for (CommandLineItem item : items) {
			item.load(configBuffer);
		}
	}
	
	@Override
	public void updateCommandLine() {
		for (CommandLineItem item : items) {
			item.save(configBuffer);
		}
		
		commandLineField.setText(configBuffer.toString().trim());
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		parseCommandLine();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		parseCommandLine();
	}

	@Override
	public void setImplicitFlag(String key, boolean val) {
		if (key.equals("!debug")) {
			debugExecutable = val;
			launchButton.setText(val? "Debug" : "Launch");
		}
	}
}