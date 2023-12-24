package utils;

import panel.AbstractController;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class CustomPanelListener implements ComponentListener {
    AbstractController controller;
    public CustomPanelListener(AbstractController controller) {
        this.controller = controller;
    }

    @Override
    public void componentResized(ComponentEvent e) {}

    @Override
    public void componentMoved(ComponentEvent e) {}

    @Override
    public void componentShown(ComponentEvent e) {
        this.controller.loadData();
        this.controller.reloadView();
    }

    @Override
    public void componentHidden(ComponentEvent e) {}
}
