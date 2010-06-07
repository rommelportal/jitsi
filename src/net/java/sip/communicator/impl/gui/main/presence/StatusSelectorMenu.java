/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.gui.main.presence;

import java.awt.*;
import java.awt.image.*;
import java.util.List;

import javax.swing.*;

import net.java.sip.communicator.impl.gui.*;
import net.java.sip.communicator.service.configuration.*;
import net.java.sip.communicator.service.protocol.*;
import net.java.sip.communicator.util.swing.*;

/**
 * A parent class for all status selector boxes.
 * 
 * @see SimpleStatusMenu
 * @see GlobalStatusSelectorBox
 * 
 * @author Yana Stamcheva
 */
public abstract class StatusSelectorMenu
    extends SIPCommMenu
    implements ImageObserver
{
    private final Image connectingIcon
        = GuiActivator.getResources().getImage("service.gui.icons.CONNECTING")
            .getImage();

    private boolean isConnecting;

    /**
     * Creates a <tt>StatusSelectorMenu</tt>.
     */
    public StatusSelectorMenu()
    {
        super();
    }

    /**
     * Creates a <tt>StatusSelectorMenu</tt> by specifying the text and icon to
     * show.
     * @param text the text of the menu
     * @param defaultIcon the icon of the menu
     */
    public StatusSelectorMenu(String text, Icon defaultIcon)
    {
        super(text, defaultIcon);
    }

    /**
     * Updates the current status.
     */
    public void updateStatus(){}

    /**
     * Returns the account index (In case of more than one account for one and
     * the same protocol).
     * 
     * @return the account index
     */
    public int getAccountIndex(){return -1;}

    /**
     * Sets the account index.
     * 
     * @param index
     */
    public void setAccountIndex(int index){}

    /**
     * Saves the last status for all accounts. This information is used
     * on loging. Each time user logs in he's logged with the same status
     * as he was the last time before closing the application.
     * @param protocolProvider the protocol provider to save status information
     * for
     * @param statusName the name of the status to save
     */
    protected void saveStatusInformation(
            ProtocolProviderService protocolProvider,
            String statusName)
    {
        ConfigurationService configService
            = GuiActivator.getConfigurationService();

        String prefix = "net.java.sip.communicator.impl.gui.accounts";

        List<String> accounts = configService
                .getPropertyNamesByPrefix(prefix, true);

        boolean savedAccount = false;

        for (String accountRootPropName : accounts) {
            String accountUID
                = configService.getString(accountRootPropName);

            if(accountUID.equals(protocolProvider
                    .getAccountID().getAccountUniqueID())) {

                configService.setProperty(
                        accountRootPropName + ".lastAccountStatus",
                        statusName);

                savedAccount = true;
            }
        }

        if(!savedAccount) {
            String accNodeName
                = "acc" + Long.toString(System.currentTimeMillis());

            String accountPackage
                = "net.java.sip.communicator.impl.gui.accounts."
                        + accNodeName;

            configService.setProperty(accountPackage,
                    protocolProvider.getAccountID().getAccountUniqueID());

            configService.setProperty(
                    accountPackage+".lastAccountStatus",
                    statusName);
        }

        this.setPreferredSize(new Dimension(28, 24));
    }

    /**
     * Paints this component. If the state of this menu is connecting, paints
     * the connecting icon.
     */
    public void paintComponent(Graphics g)
    {
        AntialiasingManager.activateAntialiasing(g);
        super.paintComponent(g);

        if(isConnecting)
            g.drawImage(connectingIcon, 0, 3, this);
    }

    /**
     * Starts the connecting.
     */
    public void startConnecting()
    {
        setConnecting(true);
    }

    /**
     * Stops the connecting.
     */
    public void stopConnecting()
    {
        setConnecting(false);
    }

    /**
     * Sets the connecting state for this menu.
     * @param isConnecting indicates if this menu is currently connecting
     */
    private void setConnecting(boolean isConnecting)
    {
        this.isConnecting = isConnecting;
        this.repaint();
    }

    /**
     * Updates the menu icon.
     * @param img the image to update
     * @param infoflags the info flags
     * @param x the x coordinate
     * @param y the y coordinate
     * @param w the width
     * @param h the height
     * @return <tt>true</tt> if the image has been updated, <tt>false</tt>
     * otherwise
     */
    public boolean imageUpdate(Image img, int infoflags,
                                int x, int y,
                                int w, int h)
    {
        repaint();
        return true;
    }
}
