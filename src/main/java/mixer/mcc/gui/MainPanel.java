package mixer.mcc.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import mixer.mcc.command.PlayVideoCommand;
import mixer.mcc.config.ConfigKey;
import mixer.mcc.config.Configuration;
import mixer.mcc.services.mixer.api.MixerInfo;
import mixer.mcc.services.twitter.TwitterBot;
import mixer.mcc.timertasks.TimerThread;
import mixer.mcc.timertasks.ViewerCountUpdaterTask;
import net.miginfocom.swing.MigLayout;

public class MainPanel extends JPanel {
	
	private Configuration config = Configuration.getConfiguration();
	
	//TODO Das kommt alles noch in ne controller klasse
	private TwitterBot twitterBot = new TwitterBot();
	
	
	
	private MixerInfo mixerInfo = new MixerInfo();
	
	private TimerThread viewerCounterUpdater;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6829793510139914776L;

	public MainPanel() {
		super(new MigLayout());
		
		/*
		 * TODO Statt hier direkt alles zu definieren ist folgende Aufteilung geplant:
		 * - Triggers (wie ALT + L fuer Live Trigger
		 * - Events ("Live")
		 * - Actions, die ausgeführt werden, wenn ein Trigger einen Event ausgeloest hat (wie tweete auf twitter)
		 * - Parameter (wie fixe Texte oder Texte die über ne textarea eingegben werden muessen)
		 * 
		 * Das ganze soll dann moeglichst dynamisch (ne xml konfig?) konfigurierbar sein und wird dann nur noch eingelesen und verknuepft.
		 * Später kann man dann für die konfig auch noch ein gui bauen...
		 * 
		 */
		
		final JLabel jlViewerCountLabel = new JLabel("Total Viewers: n/a | Current Viewers: n/a");
		viewerCounterUpdater = new TimerThread("ViewerCounterUpdater", new ViewerCountUpdaterTask(mixerInfo, jlViewerCountLabel), config.getConfigInteger(ConfigKey.SERVICE_MIXER_UPDATE_INTERVAL));
		final JButton jbPauseViewerCount = new JButton("(P)ausiere update");
		jbPauseViewerCount.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				viewerCounterUpdater.togglePauseTask();
				
			}
		});
		jbPauseViewerCount.setMnemonic(KeyEvent.VK_P);
		
		
		final JTextArea jtLive = new JTextArea(config.getConfigValue(ConfigKey.INFO_USER_TWEET_LIVE));
		JButton jbLive = new JButton("(L)IVE GEHEN");
		jbLive.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String liveTxt = jtLive.getText();
				
				//TODO usually that would be a command being executed...
				twitterBot.tweet(liveTxt);
			}
		});
		
		jbLive.setMnemonic(KeyEvent.VK_L);
		
		//TODO Wahrscheinlich wuerde man das konfiguerbar haben wollen, wobei man
		//Ueberhaupt alle actions konfigurierbar haben will und nicht feste buttons!!!
		// -> generisches "Play Video Command" das man dann per properties x-mal spawnen kann
		// Man braucht auch keinen settings dialog, ne props file finde ich viel komfortabler ^^
		final JTextArea jtVideo = new JTextArea(config.getConfigValue(ConfigKey.SERVICE_VLC_VIDEO_DEFAULT));
		JButton jbBoat = new JButton("ON A (B)OAT");
		jbBoat.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				//TODO commands should be executed in a central place
				//for that we need a "custom" commandListener that would take any command and execute it
				//(the jut commandlistner can just execute commands with default instantiation (no args)
				PlayVideoCommand pvc = new PlayVideoCommand(jtVideo.getText());
				pvc.execute();
			}
		});
		jbBoat.setMnemonic(KeyEvent.VK_B);
		
		
		//Layout
		add(jlViewerCountLabel);
		add(jbPauseViewerCount,"wrap");
		
		add(jtLive);
		add(jbLive,"wrap");
		
		add(jtVideo);
		add(jbBoat);
		
		// Updates
		viewerCounterUpdater.startTask();
		
	}

	@Override
	protected void finalize() throws Throwable {
		//XXX Wird imo beim beenden gar nicht getriggered!!! net schlimm aber...
		viewerCounterUpdater.destroyTask();
		super.finalize();
	}
	
	
	
	
	

}
