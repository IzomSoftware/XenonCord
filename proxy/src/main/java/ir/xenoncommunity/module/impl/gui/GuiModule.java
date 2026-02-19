/*

CREDITS: KiloSheet
THANKS FOR SUPPORTING XENON COMMUNITY.

 */

package ir.xenoncommunity.module.impl.gui;

import com.sun.management.OperatingSystemMXBean;
import ir.xenoncommunity.XenonCore;
import ir.xenoncommunity.annotations.ModuleInfo;
import ir.xenoncommunity.module.ModuleBase;
import ir.xenoncommunity.utils.Colorize;
import ir.xenoncommunity.utils.Language;
import ir.xenoncommunity.utils.Message;
import net.md_5.bungee.api.CommandSender;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.lang.management.ManagementFactory;

@ModuleInfo(name = "GUI-Interface", version = 1.1, description = "UI Module for server analytics")
public class GuiModule extends ModuleBase {
    private JLabel onlinePlayersLabel;
    private JLabel memoryUsageLabel;
    private JLabel cpuUsageLabel;
    private JLabel uptimeLabel;
    private JTextArea playerListArea;
    private OperatingSystemMXBean osBean;
    private JFrame frame;
    private long startTime;

    public static GuiModule instance;

    @Override
    public void onInit() {
        instance = this;
        if (!getConfig().getModules().getGui_module().isEnabled())
            return;
        
        if (GraphicsEnvironment.isHeadless()) {
            getLogger().warn("Headless environment detected. GUI module will be available via commands if display is found later, but won't auto-show.");
            return;
        }

        startTime = System.currentTimeMillis();
        SwingUtilities.invokeLater(this::createAndShowGUI);
    }

    public void toggleGUI(CommandSender sender) {
        if (GraphicsEnvironment.isHeadless()) {
            Message.send(sender, "&b&lXenonCord &cCannot open GUI: Headless environment!", true);
            return;
        }

        SwingUtilities.invokeLater(() -> {
            if (frame == null) {
                createAndShowGUI();
            } else {
                frame.setVisible(!frame.isVisible());
                if (frame.isVisible()) {
                    frame.toFront();
                }
            }
            Message.send(sender, frame.isVisible() ? Language.get("gui_opened") : Language.get("gui_closed"), true);
        });
    }

    private void createAndShowGUI() {
        osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        frame = new JFrame("XenonCord Proxy");
        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());

        frame.add(createMainPanel());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        Timer timer = new Timer((int) XenonCore.instance.getConfigData().getModules().getGui_module().getRefresh_rate(), e -> {
            if (frame.isVisible()) {
                onlinePlayersLabel.setText("Online Players: " + XenonCore.instance.getBungeeInstance().getOnlineCount());
                memoryUsageLabel.setText(getMemoryUsageText());
                cpuUsageLabel.setText(getCPUUsageText());
                uptimeLabel.setText("Uptime: " + getUptimeText());
                updatePlayerList();
            }
        });
        timer.start();
    }

    private JPanel createMainPanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(30, 30, 30));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        onlinePlayersLabel = createLabel("Online Players: 0", 24, new Color(0, 255, 255));
        panel.add(onlinePlayersLabel);

        uptimeLabel = createLabel("Uptime: 0s", 18, new Color(255, 255, 255));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(uptimeLabel);

        memoryUsageLabel = createLabel(getMemoryUsageText(), 20, new Color(255, 105, 180));
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(memoryUsageLabel);

        cpuUsageLabel = createLabel(getCPUUsageText(), 20, new Color(135, 206, 250));
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(cpuUsageLabel);

        playerListArea = new JTextArea();
        playerListArea.setEditable(false);
        playerListArea.setBackground(new Color(40, 40, 40));
        playerListArea.setForeground(Color.WHITE);
        playerListArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        playerListArea.setLineWrap(true);
        playerListArea.setWrapStyleWord(true);

        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(createRoundedPanel(new JScrollPane(playerListArea)));

        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(createCloseButton());

        return panel;
    }

    private JLabel createLabel(String text, int fontSize, Color color) {
        final JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setForeground(color);
        label.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JButton createCloseButton() {
        final JButton closeButton = new JButton("Hide GUI");
        closeButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        closeButton.setForeground(Color.WHITE);
        closeButton.setBackground(new Color(220, 20, 60));
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setOpaque(true);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.addActionListener(e -> frame.setVisible(false));
        return closeButton;
    }

    private JPanel createRoundedPanel(JComponent component) {
        final JPanel roundedPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(50, 50, 50));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
        };
        roundedPanel.setLayout(new BorderLayout());
        roundedPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        roundedPanel.setBackground(new Color(30, 30, 30));
        roundedPanel.add(component, BorderLayout.CENTER);
        return roundedPanel;
    }

    private void updatePlayerList() {
        final StringBuilder playerNames = new StringBuilder();
        XenonCore.instance.getPlayerNames().forEach(playerName -> playerNames.append(playerName).append("\n"));
        if (playerNames.length() == 0) playerNames.append("No players online.");
        playerListArea.setText(playerNames.toString());
    }

    public String getCPUUsageText() {
        double load = osBean.getProcessCpuLoad();
        if (load < 0) load = 0;
        return String.format("Process CPU: %.2f%%", load * 100);
    }

    public String getMemoryUsageText() {
        Runtime runtime = Runtime.getRuntime();
        long used = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        long max = runtime.maxMemory() / (1024 * 1024);
        return "Memory usage: " + used + "MB / " + max + "MB";
    }

    private String getUptimeText() {
        long diff = (System.currentTimeMillis() - startTime) / 1000;
        long hours = diff / 3600;
        long minutes = (diff % 3600) / 60;
        long seconds = diff % 60;
        return String.format("%02dh %02dm %02ds", hours, minutes, seconds);
    }
}