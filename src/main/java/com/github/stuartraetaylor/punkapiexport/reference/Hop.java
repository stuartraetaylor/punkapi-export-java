package com.github.stuartraetaylor.punkapiexport.reference;

import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Hop implements ReferenceEntity {

    @JsonProperty("Id")
    private Integer id;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Info")
    private String info;

    @JsonProperty("Styles")
    private String styles;

    @JsonProperty("Aroma")
    private String armoa;

    @JsonProperty("BrewingUsage")
    private String brewingUsage;

    @JsonProperty("Pedigree")
    private String pedigree;

    @JsonProperty("AlphaMax")
    private Double alphaMax;

    @JsonProperty("AlphaMin")
    private Double alphaMin;

    @JsonProperty("BetaMax")
    private Double betaMax;

    @JsonProperty("BetaMin")
    private Double betaMin;

    @JsonProperty("CoHumuloneMax")
    private Integer coHumuloneMax;

    @JsonProperty("CoHumuloneMin")
    private Integer coHumuloneMin;

    @JsonProperty("TotalOilMax")
    private Double totalOilMax;

    @JsonProperty("TotalOilMin")
    private Double totalOilMin;

    @JsonProperty("Trade")
    private String trade;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getStyles() {
        return styles;
    }

    public void setStyles(String styles) {
        this.styles = styles;
    }

    public String getArmoa() {
        return armoa;
    }

    public void setArmoa(String armoa) {
        this.armoa = armoa;
    }

    public String getBrewingUsage() {
        return brewingUsage;
    }

    public void setBrewingUsage(String brewingUsage) {
        this.brewingUsage = brewingUsage;
    }

    public String getPedigree() {
        return pedigree;
    }

    public void setPedigree(String pedigree) {
        this.pedigree = pedigree;
    }

    public Double getAlphaMax() {
        return alphaMax;
    }

    public void setAlphaMax(Double alphaMax) {
        this.alphaMax = alphaMax;
    }

    public Double getAlphaMin() {
        return alphaMin;
    }

    public void setAlphaMin(Double alphaMin) {
        this.alphaMin = alphaMin;
    }

    public Double getBetaMax() {
        return betaMax;
    }

    public void setBetaMax(Double betaMax) {
        this.betaMax = betaMax;
    }

    public Double getBetaMin() {
        return betaMin;
    }

    public void setBetaMin(Double betaMin) {
        this.betaMin = betaMin;
    }

    public Integer getCoHumuloneMax() {
        return coHumuloneMax;
    }

    public void setCoHumuloneMax(Integer coHumuloneMax) {
        this.coHumuloneMax = coHumuloneMax;
    }

    public Integer getCoHumuloneMin() {
        return coHumuloneMin;
    }

    public void setCoHumuloneMin(Integer coHumuloneMin) {
        this.coHumuloneMin = coHumuloneMin;
    }

    public Double getTotalOilMax() {
        return totalOilMax;
    }

    public void setTotalOilMax(Double totalOilMax) {
        this.totalOilMax = totalOilMax;
    }

    public Double getTotalOilMin() {
        return totalOilMin;
    }

    public void setTotalOilMin(Double totalOilMin) {
        this.totalOilMin = totalOilMin;
    }

    public String getTrade() {
        return trade;
    }

    public void setTrade(String trade) {
        this.trade = trade;
    }

    @Override
    public String identifier() {
        return normalise(name);
    }

    public static String normalise(String identifier) {
        if (identifier == null)
            return null;

        return normalisePattern.matcher(identifier)
            .replaceAll("")
            .toLowerCase()
            .trim();
    }

    private static Pattern normalisePattern = Pattern.compile("\\([^\\)]+\\)");

}
