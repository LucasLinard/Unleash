package tech.linard.android.unleash.model;

/**
 * Created by llinard on 06/12/17.
 */

public class StopLoss {
    private String id;
    private String uuid;
    private int exchangeId;
    private double cotacaoBTC;
    private double quantidadeBTC;

    public StopLoss(String id, String uuid, int exchangeId, double cotacaoBTC, double quantidadeBTC) {
        this.id = id;
        this.uuid = uuid;
        this.exchangeId = exchangeId;
        this.cotacaoBTC = cotacaoBTC;
        this.quantidadeBTC = quantidadeBTC;
    }

    public StopLoss() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(int exchangeId) {
        this.exchangeId = exchangeId;
    }

    public double getCotacaoBTC() {
        return cotacaoBTC;
    }

    public void setCotacaoBTC(double cotacaoBTC) {
        this.cotacaoBTC = cotacaoBTC;
    }

    public double getQuantidadeBTC() {
        return quantidadeBTC;
    }

    public void setQuantidadeBTC(double quantidadeBTC) {
        this.quantidadeBTC = quantidadeBTC;
    }
}