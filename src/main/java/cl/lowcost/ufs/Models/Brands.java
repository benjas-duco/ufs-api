package cl.lowcost.ufs.Models;

public enum Brands {
    POPUP_PARADE("Pop Up Parade"),
    NENDROID("Nendroid Series"),
    FIGMA("Figma"),
    SCALE_FIGURE("Scale Figure");

    private final String label;

    Brands(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
