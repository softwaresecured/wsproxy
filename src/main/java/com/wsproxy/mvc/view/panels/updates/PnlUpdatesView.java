package com.wsproxy.mvc.view.panels.updates;

import com.wsproxy.mvc.model.UpdatesModel;

import javax.swing.*;
import java.awt.*;

public class PnlUpdatesView extends JPanel {
    private UpdatesModel updatesModel;
    public JSplitPane splt;
    public PnlUpdateContentViewer pnlUpdateContentViewer = new PnlUpdateContentViewer();
    public PnlUpdatesTableViewer pnlUpdatesTableViewer;
    public PnlUpdatesView(UpdatesModel updatesModel) {
        this.updatesModel = updatesModel;
        initLayout();
    }
    public void initLayout() {
        pnlUpdatesTableViewer = new PnlUpdatesTableViewer(updatesModel);
        splt = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,pnlUpdatesTableViewer, pnlUpdateContentViewer);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weighty = 1;
        gbc.weightx = 1;
        add(splt,gbc);
        int parentWidth = (int)this.getSize().getWidth();
        pnlUpdatesTableViewer.setPreferredSize(new Dimension(parentWidth/2,(int)this.getPreferredSize().getHeight()));
        pnlUpdateContentViewer.setPreferredSize(new Dimension(parentWidth/2,(int)this.getPreferredSize().getHeight()));
        splt.setResizeWeight(0.25);
    }
}
