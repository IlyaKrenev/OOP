package panel;

import java.awt.*;

public abstract class AbstractController<DATA> {
    public abstract DATA loadData();

    public abstract void reloadView();

    public abstract Component mainViewComponent();
}