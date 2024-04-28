package io.github.itzispyder.improperui.config.settings.types;

import io.github.itzispyder.improperui.config.settings.SettingBuilder;
import io.github.itzispyder.improperui.render.Element;
import io.github.itzispyder.improperui.util.MathUtils;

public class DoubleSetting extends NumberSetting<Double> {

    private int decimalPlaces;

    public DoubleSetting(String name, double def, double val, double min, double max, int decimalPlaces) {
        super(name, def, val, min, max);
        this.decimalPlaces = Math.max(1, decimalPlaces);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Element toGuiElement(int x, int y) {
        return null;
    }

    @Override
    public void setMax(Double max) {
        super.setMax(Math.max(min, max));
    }

    @Override
    public void setMin(Double min) {
        super.setMin(Math.min(min, max));
    }

    @Override
    public void setDef(Double def) {
        super.setDef(round(def));
    }

    @Override
    public void setVal(Object val) {
        super.setVal(round((double)val));
    }

    @Override
    public Double getVal() {
        return round(super.getVal());
    }

    @Override
    public Double getDef() {
        return round(super.getDef());
    }

    public int getDecimalPlaces() {
        return decimalPlaces;
    }

    public void setDecimalPlaces(int decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
    }

    private double round(double val) {
        double ex = Math.pow(10, decimalPlaces);
        return Math.floor(val * ex) / ex;
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder extends SettingBuilder<Double, Builder, DoubleSetting> {

        private double min, max;
        private int decimalPlaces;

        public Builder() {
            this.min = 0;
            this.max = 1;
        }

        public Builder min(double min) {
            this.min = Math.min(min, max);
            return this;
        }

        public Builder max(double max) {
            this.max = Math.max(min, max);
            return this;
        }

        public Builder decimalPlaces(int decimalPlaces) {
            this.decimalPlaces = decimalPlaces;
            return this;
        }

        @Override
        public DoubleSetting buildSetting() {
            return new DoubleSetting(name, MathUtils.clamp(def, min, max), getOrDef(val, def), min, max, decimalPlaces);
        }
    }
}
