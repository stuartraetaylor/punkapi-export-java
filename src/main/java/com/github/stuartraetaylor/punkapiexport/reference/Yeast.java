package com.github.stuartraetaylor.punkapiexport.reference;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public class Yeast implements ReferenceEntity {

    @JsonProperty("id")
    private int id;

    @JsonProperty("laboratory")
    private String laboratory;

    @JsonProperty("strain")
    private String strain;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("flocculation")
    private Flocculation flocculation;

    @JsonProperty("form")
    private Form form;

    @JsonProperty("attenuation_min")
    private double attenuationMin;

    @JsonProperty("attenuation_max")
    private double attenuationMax;

    @JsonProperty("temperature_min")
    private double temperatureMin;

    @JsonProperty("temperature_max")
    private double temperatureMax;

    @JsonProperty("tolerance")
    private double tolerance;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLaboratory() {
		return laboratory;
	}

    public void setLaboratory(String laboratory) {
        this.laboratory = laboratory;
    }

    public String getStrain() {
        return strain;
    }

    public void setStrain(String strain) {
		this.strain = strain;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Flocculation getFlocculation() {
		return flocculation;
	}

	public void setFlocculation(Flocculation flocculation) {
		this.flocculation = flocculation;
	}

	public Form getForm() {
		return form;
	}

	public void setForm(Form form) {
		this.form = form;
	}

	public double getAttenuationMin() {
		return attenuationMin;
	}

	public void setAttenuationMin(double attenuationMin) {
		this.attenuationMin = attenuationMin;
	}

	public double getAttenuationMax() {
		return attenuationMax;
	}

	public void setAttenuationMax(double attenuationMax) {
		this.attenuationMax = attenuationMax;
	}

	public double getTemperatureMin() {
		return temperatureMin;
	}

	public void setTemperatureMin(double temperatureMin) {
		this.temperatureMin = temperatureMin;
	}

	public double getTemperatureMax() {
		return temperatureMax;
	}

    public void setTemperatureMax(double temperatureMax) {
        this.temperatureMax = temperatureMax;
    }

    public double getTolerance() {
        return tolerance;
    }

	public void setTolerance(double tolerance) {
		this.tolerance = tolerance;
	}

    public static enum Flocculation {
        LOW("Low"),
        MEDIUM("Medium"),
        HIGH("High");

        private final String text;

        Flocculation(String text) {
            this.text = text;
        }

        @JsonCreator
        public static Flocculation parse(String value) {
            String enumValue = value.replaceAll("\\s+", "_").toUpperCase();
            if (enumValue.equals("N/A"))
                return null;

            try {
                return valueOf(enumValue);
            } catch (IllegalArgumentException e) { // alises..
                switch (enumValue) {
                    case "POWDERY":
                    case "LOW_TO_MEDIUM":
                        return LOW;
                    case "MEDIUM_TO_LOW":
                    case "MEDIUM_TO_HIGH":
                        return MEDIUM;
                    case "VERY_HIGH":
                        return HIGH;
                }
            }

            return null;
        }

        @JsonValue
        @Override
        public String toString() {
            return text;
        }
    }

    public static enum Form {
        LIQUID("Liquid"),
        DRY("Dry");

        private final String text;

        Form(String text) {
            this.text = text;
        }

        @JsonCreator
        public static Form parse(String value) {
            return valueOf(value.toUpperCase());
        }

        @JsonValue
        @Override
        public String toString() {
            return text;
        }
    }

    @Override
    public String identifier() {
        return normalise(strain);
    }

    public static String normalise(String identifier) {
        if (identifier == null)
            return null;

        return identifier.toLowerCase().trim();
    }

}
