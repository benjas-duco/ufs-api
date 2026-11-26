package cl.lowcost.ufs.Models;

public enum States {
    AVAILABLE,
    UNAVAILABLE,
    ON_PRODUCTION,
    PRE_SALE,
    DELETED,
    ON_SALE;

    // Se puede agregar al carrito y comprar
    public boolean isPurchasable() {
        return this == AVAILABLE || this == ON_SALE || this == PRE_SALE;
    }

    // Exige stock y lo descuenta al comprar (la preventa no maneja stock)
    public boolean needsStock() {
        return this == AVAILABLE || this == ON_SALE;
    }
}
