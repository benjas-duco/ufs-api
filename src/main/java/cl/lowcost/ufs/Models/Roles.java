package cl.lowcost.ufs.Models;

public enum Roles {
    USER,
    SHIPPER,
    ADMIN;

    // Cada rol incluye los permisos de los menores: ADMIN > SHIPPER > USER
    public boolean atLeast(Roles other) {
        return this.ordinal() >= other.ordinal();
    }
}
