package tech.linard.android.unleash.model;

/**
 * Created by llinard on 06/12/17.
 */

public class StopLoss {
    private String token;
    private int exchangeId;
    private double cotacaoBTC;
    private double quantidadeBTC;

    public StopLoss(String token, int exchangeId, double cotacaoBTC, double quantidadeBTC) {
        this.token = token;
        this.exchangeId = exchangeId;
        this.cotacaoBTC = cotacaoBTC;
        this.quantidadeBTC = quantidadeBTC;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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
