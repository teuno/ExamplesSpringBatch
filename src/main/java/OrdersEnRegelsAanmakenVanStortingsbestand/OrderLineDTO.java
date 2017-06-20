package OrdersEnRegelsAanmakenVanStortingsbestand;

class OrderLineDTO {
    private int orderId;

    private int fondsId;

    int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    int getFondsId() {
        return fondsId;
    }

    public void setFondsId(int fondsId) {
        this.fondsId = fondsId;
    }

    @Override
    public String toString() {
        return "OrderLineDTO{orderId=" + orderId + ", fondsId=" + fondsId + "}";
    }
}
